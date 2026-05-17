import { Stethoscope } from 'lucide-react';
import type { Theme } from '../../hooks/useTheme';
import { ThemeToggle } from './ThemeToggle';

type TopBarProps = {
  theme: Theme;
  onToggleTheme: () => void;
};

export function TopBar({ theme, onToggleTheme }: TopBarProps) {
  return (
    <header className="topbar">
      <div className="brand">
        <span className="brand-mark">
          <Stethoscope size={24} aria-hidden="true" />
        </span>
        <div>
          <p>МедНавігатор</p>
          <h1>Знайдіть лікаря та оберіть зручний час</h1>
          <span>Агрегатор медичних послуг з онлайн-записом</span>
        </div>
      </div>

      <div className="topbar-actions">
        <ThemeToggle theme={theme} onToggle={onToggleTheme} />
      </div>
    </header>
  );
}
