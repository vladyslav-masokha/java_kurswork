export function initials(name: string) {
  return name
    .split(' ')
    .map((part) => part[0])
    .join('')
    .slice(0, 2)
    .toUpperCase();
}

function toDate(value: string) {
  return new Date(value.includes('T') ? value : `${value}T00:00:00`);
}

export function dateInputValue(date: Date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
}

export function tomorrowDateInput() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  return dateInputValue(tomorrow);
}

export function isPastDateTime(value: string) {
  return toDate(value).getTime() < Date.now();
}

export function formatDateTime(value: string) {
  return new Intl.DateTimeFormat('uk-UA', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  }).format(toDate(value));
}

export function formatDayLabel(value: string) {
  return new Intl.DateTimeFormat('uk-UA', {
    day: '2-digit',
    month: 'long',
    year: 'numeric'
  }).format(toDate(value));
}

export function formatDateShort(value: string) {
  return new Intl.DateTimeFormat('uk-UA', {
    day: '2-digit',
    month: '2-digit'
  }).format(toDate(value));
}

export function formatTime(value: string) {
  return new Intl.DateTimeFormat('uk-UA', {
    hour: '2-digit',
    minute: '2-digit'
  }).format(toDate(value));
}
