package ua.edu.duit.medical.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import ua.edu.duit.medical.exception.ValidationException;

public final class Json {
    private Json() {
    }

    public static Map<String, Object> parseObject(String json) {
        Object value = new Parser(json).parse();
        if (!(value instanceof Map)) {
            throw new ValidationException("JSON тіло має бути об'єктом.");
        }
        return castMap(value);
    }

    public static String stringify(Object value) {
        StringBuilder builder = new StringBuilder();
        write(builder, value);
        return builder.toString();
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> castMap(Object value) {
        return (Map<String, Object>) value;
    }

    @SuppressWarnings("unchecked")
    private static void write(StringBuilder builder, Object value) {
        if (value == null) {
            builder.append("null");
        } else if (value instanceof String) {
            writeString(builder, (String) value);
        } else if (value instanceof Number || value instanceof Boolean) {
            builder.append(value.toString());
        } else if (value instanceof Map) {
            builder.append('{');
            boolean first = true;
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) value).entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                writeString(builder, entry.getKey());
                builder.append(':');
                write(builder, entry.getValue());
            }
            builder.append('}');
        } else if (value instanceof Iterable) {
            builder.append('[');
            boolean first = true;
            for (Object item : (Iterable<?>) value) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                write(builder, item);
            }
            builder.append(']');
        } else {
            writeString(builder, value.toString());
        }
    }

    private static void writeString(StringBuilder builder, String value) {
        builder.append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if (ch == '"' || ch == '\\') {
                builder.append('\\').append(ch);
            } else if (ch == '\n') {
                builder.append("\\n");
            } else if (ch == '\r') {
                builder.append("\\r");
            } else if (ch == '\t') {
                builder.append("\\t");
            } else {
                builder.append(ch);
            }
        }
        builder.append('"');
    }

    public static Map<String, Object> object() {
        return new LinkedHashMap<String, Object>();
    }

    public static List<Object> array() {
        return new ArrayList<Object>();
    }

    private static final class Parser {
        private final String source;
        private int index;

        private Parser(String source) {
            this.source = source == null ? "" : source.trim();
        }

        private Object parse() {
            Object value = parseValue();
            skipWhitespace();
            if (index != source.length()) {
                throw error("JSON містить зайві символи.");
            }
            return value;
        }

        private Object parseValue() {
            skipWhitespace();
            if (index >= source.length()) {
                throw error("JSON не може бути порожнім.");
            }
            char ch = source.charAt(index);
            if (ch == '{') {
                return parseObject();
            }
            if (ch == '[') {
                return parseArray();
            }
            if (ch == '"') {
                return parseString();
            }
            if (ch == 't') {
                expect("true");
                return Boolean.TRUE;
            }
            if (ch == 'f') {
                expect("false");
                return Boolean.FALSE;
            }
            if (ch == 'n') {
                expect("null");
                return null;
            }
            if (ch == '-' || Character.isDigit(ch)) {
                return parseNumber();
            }
            throw error("Некоректне JSON значення.");
        }

        private Map<String, Object> parseObject() {
            Map<String, Object> result = new LinkedHashMap<String, Object>();
            index++;
            skipWhitespace();
            if (peek('}')) {
                index++;
                return result;
            }
            while (true) {
                skipWhitespace();
                String key = parseString();
                skipWhitespace();
                require(':');
                result.put(key, parseValue());
                skipWhitespace();
                if (peek('}')) {
                    index++;
                    return result;
                }
                require(',');
            }
        }

        private List<Object> parseArray() {
            List<Object> result = new ArrayList<Object>();
            index++;
            skipWhitespace();
            if (peek(']')) {
                index++;
                return result;
            }
            while (true) {
                result.add(parseValue());
                skipWhitespace();
                if (peek(']')) {
                    index++;
                    return result;
                }
                require(',');
            }
        }

        private String parseString() {
            require('"');
            StringBuilder builder = new StringBuilder();
            while (index < source.length()) {
                char ch = source.charAt(index++);
                if (ch == '"') {
                    return builder.toString();
                }
                if (ch == '\\') {
                    if (index >= source.length()) {
                        throw error("Некоректне екранування JSON рядка.");
                    }
                    char escaped = source.charAt(index++);
                    if (escaped == '"' || escaped == '\\' || escaped == '/') {
                        builder.append(escaped);
                    } else if (escaped == 'n') {
                        builder.append('\n');
                    } else if (escaped == 'r') {
                        builder.append('\r');
                    } else if (escaped == 't') {
                        builder.append('\t');
                    } else {
                        throw error("Непідтримуване екранування JSON рядка.");
                    }
                } else {
                    builder.append(ch);
                }
            }
            throw error("JSON рядок не закрито.");
        }

        private Number parseNumber() {
            int start = index;
            if (peek('-')) {
                index++;
            }
            while (index < source.length() && Character.isDigit(source.charAt(index))) {
                index++;
            }
            if (peek('.')) {
                index++;
                while (index < source.length() && Character.isDigit(source.charAt(index))) {
                    index++;
                }
            }
            String raw = source.substring(start, index);
            try {
                if (raw.contains(".")) {
                    return new BigDecimal(raw);
                }
                return Long.valueOf(raw);
            } catch (NumberFormatException ex) {
                throw error("Некоректне число у JSON.");
            }
        }

        private void expect(String value) {
            if (!source.startsWith(value, index)) {
                throw error("Очікувалося '" + value + "'.");
            }
            index += value.length();
        }

        private boolean peek(char expected) {
            return index < source.length() && source.charAt(index) == expected;
        }

        private void require(char expected) {
            if (!peek(expected)) {
                throw error("Очікувався символ '" + expected + "'.");
            }
            index++;
        }

        private void skipWhitespace() {
            while (index < source.length() && Character.isWhitespace(source.charAt(index))) {
                index++;
            }
        }

        private ValidationException error(String message) {
            return new ValidationException(message);
        }
    }
}

