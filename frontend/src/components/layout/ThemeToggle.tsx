import { Moon, Sun } from 'lucide-react';
import type { Theme } from '../../hooks/useTheme';

type ThemeToggleProps = {
  theme: Theme;
  onToggle: () => void;
};

export function ThemeToggle({ theme, onToggle }: ThemeToggleProps) {
  const isDark = theme === 'dark';

  return (
    <button className="theme-toggle" onClick={onToggle} title="Змінити тему" aria-label="Змінити тему">
      <span className={isDark ? '' : 'is-active'}>
        <Sun size={17} aria-hidden="true" />
      </span>
      <span className={isDark ? 'is-active' : ''}>
        <Moon size={17} aria-hidden="true" />
      </span>
    </button>
  );
}

