import { useEffect, useState } from 'react';

export type Theme = 'light' | 'dark';

const STORAGE_KEY = 'mednavigator-theme-v2';
const phoneQuery = '(max-width: 760px)';
const darkQuery = '(prefers-color-scheme: dark)';

export function useTheme() {
  const [theme, setTheme] = useState<Theme>(() => resolveInitialTheme());

  useEffect(() => {
    document.documentElement.dataset.theme = theme;
  }, [theme]);

  useEffect(() => {
    const phone = window.matchMedia(phoneQuery);
    const dark = window.matchMedia(darkQuery);

    function syncWithDevice() {
      if (localStorage.getItem(STORAGE_KEY)) {
        return;
      }
      setTheme(phone.matches ? (dark.matches ? 'dark' : 'light') : 'dark');
    }

    phone.addEventListener('change', syncWithDevice);
    dark.addEventListener('change', syncWithDevice);

    return () => {
      phone.removeEventListener('change', syncWithDevice);
      dark.removeEventListener('change', syncWithDevice);
    };
  }, []);

  function toggleTheme() {
    setTheme((current) => {
      const next = current === 'light' ? 'dark' : 'light';
      localStorage.setItem(STORAGE_KEY, next);
      return next;
    });
  }

  return { theme, toggleTheme };
}

function resolveInitialTheme(): Theme {
  if (typeof window === 'undefined') {
    return 'light';
  }

  const stored = localStorage.getItem(STORAGE_KEY);
  if (stored === 'dark' || stored === 'light') {
    return stored;
  }

  const isPhone = window.matchMedia(phoneQuery).matches;
  const deviceDark = window.matchMedia(darkQuery).matches;
  return isPhone ? (deviceDark ? 'dark' : 'light') : 'dark';
}
