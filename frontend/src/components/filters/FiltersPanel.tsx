import { useState } from 'react';
import { CalendarDays, Filter, MapPin, Search, SlidersHorizontal, Star, X } from 'lucide-react';
import type { DoctorFilters, Specialty } from '../../types';
import { tomorrowDateInput } from '../../utils/format';

type FiltersPanelProps = {
  cities: string[];
  filters: DoctorFilters;
  loading: boolean;
  specialties: Specialty[];
  onChange: (filters: DoctorFilters) => void;
  onReset: () => void;
  onSearch: () => void;
};

export function FiltersPanel({ cities, filters, loading, specialties, onChange, onReset, onSearch }: FiltersPanelProps) {
  const [filtersOpen, setFiltersOpen] = useState(false);
  const minDate = tomorrowDateInput();

  function update(key: keyof DoctorFilters, value: string) {
    const nextValue = key === 'date' && value && value < minDate ? minDate : value;
    onChange({ ...filters, [key]: nextValue });
  }

  function submitSearch() {
    onSearch();
    setFiltersOpen(false);
  }

  function resetSearch() {
    onReset();
    setFiltersOpen(false);
  }

  const activeFilters = Object.entries(filters).filter(([key, value]) => {
    if (!value.trim()) {
      return false;
    }
    if (key === 'sort' && value === 'ratingDesc') {
      return false;
    }
    if (key === 'date' && value === minDate) {
      return false;
    }
    return true;
  }).length;

  return (
    <>
      <button
        className="filter-mobile-trigger"
        type="button"
        aria-controls="doctor-filter-panel"
        aria-expanded={filtersOpen}
        onClick={() => setFiltersOpen((value) => !value)}
      >
        <span className="filter-trigger-label">
          <Filter size={18} aria-hidden="true" />
          Фільтри
        </span>
        {activeFilters > 0 && <span className="filter-count">{activeFilters}</span>}
      </button>

      <aside id="doctor-filter-panel" className={`filters-panel search-panel ${filtersOpen ? 'is-open' : ''}`} aria-label="Фільтри пошуку">
        <button className="filter-close-button" type="button" aria-label="Закрити фільтри" onClick={() => setFiltersOpen(false)}>
          <X size={18} aria-hidden="true" />
        </button>

        <div className="panel-title panel-title-stacked">
          <span className="panel-icon">
            <SlidersHorizontal size={18} aria-hidden="true" />
          </span>
          <div>
            <h2>Пошук</h2>
            <p>{activeFilters > 0 ? `${activeFilters} активних фільтрів` : 'Параметри прийому'}</p>
          </div>
        </div>

        <div className="filter-grid">
          <label>
            Спеціалізація
            <select aria-label="Спеціалізація" value={filters.specialty} onChange={(event) => update('specialty', event.target.value)}>
              <option value="">Усі</option>
              {specialties.map((specialty) => (
                <option value={specialty.id} key={specialty.id}>
                  {specialty.name}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span className="label-with-icon">
              <MapPin size={15} aria-hidden="true" />
              Місто
            </span>
            <select aria-label="Місто" value={filters.city} onChange={(event) => update('city', event.target.value)}>
              <option value="">Усі міста</option>
              {cities.map((city) => (
                <option value={city} key={city}>
                  {city}
                </option>
              ))}
            </select>
          </label>

          <label>
            <span className="label-with-icon">
              <CalendarDays size={15} aria-hidden="true" />
              Дата
            </span>
            <input aria-label="Дата" type="date" min={minDate} value={filters.date} onChange={(event) => update('date', event.target.value)} />
          </label>

          <label>
            <span className="label-with-icon">
              <Star size={15} aria-hidden="true" />
              Мін. рейтинг
            </span>
            <input
              type="number"
              aria-label="Мінімальний рейтинг"
              min="0"
              max="5"
              step="0.1"
              value={filters.minRating}
              onChange={(event) => update('minRating', event.target.value)}
              placeholder="4.5"
            />
          </label>

          <label>
            Макс. ціна
            <input
              type="number"
              aria-label="Максимальна ціна"
              min="0"
              step="50"
              value={filters.maxPrice}
              onChange={(event) => update('maxPrice', event.target.value)}
              placeholder="900"
            />
          </label>
          <label>
            Стаж від
            <input
              type="number"
              aria-label="Мінімальний стаж"
              min="0"
              step="1"
              value={filters.minExperience}
              onChange={(event) => update('minExperience', event.target.value)}
              placeholder="5"
            />
          </label>

          <label>
            Сортування
            <select aria-label="Сортування" value={filters.sort} onChange={(event) => update('sort', event.target.value)}>
              <option value="ratingDesc">Найвищий рейтинг</option>
              <option value="nearest">Найближчий слот</option>
              <option value="priceAsc">Найнижча ціна</option>
              <option value="experienceDesc">Найбільший стаж</option>
            </select>
          </label>

          <label className="filter-query">
            <span className="label-with-icon">
              <Search size={15} aria-hidden="true" />
              Запит
            </span>
            <input aria-label="Запит" value={filters.q} onChange={(event) => update('q', event.target.value)} placeholder="лікар або клініка" />
          </label>

          <div className="filter-actions">
            <button className="primary-button" type="button" onClick={submitSearch} disabled={loading}>
              <Search size={18} aria-hidden="true" />
              {loading ? 'Оновлення...' : 'Оновити'}
            </button>
            <button className="ghost-button" type="button" onClick={resetSearch} disabled={loading || activeFilters === 0}>
              Очистити
            </button>
          </div>
        </div>
      </aside>
    </>
  );
}
