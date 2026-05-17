import { ArrowRight, CalendarDays, MapPin, SearchX, Star, WalletCards } from 'lucide-react';
import type { Doctor } from '../../types';
import { initials } from '../../utils/format';

type DoctorListProps = {
  doctors: Doctor[];
  selectedDoctorId?: string;
  onSelect: (doctor: Doctor) => void;
};

export function DoctorList({ doctors, selectedDoctorId, onSelect }: DoctorListProps) {
  return (
    <section className="doctor-list" id="doctors" aria-label="Лікарі">
      <div className="section-heading">
        <div>
          <h2>Лікарі</h2>
          <p>Відсортовано за рейтингом</p>
        </div>
        <span>{doctors.length}</span>
      </div>

      {doctors.length === 0 ? (
        <div className="doctor-empty-state">
          <SearchX size={26} aria-hidden="true" />
          <div>
            <strong>Лікарів за цими фільтрами не знайдено</strong>
            <span>Змініть параметри пошуку або очистіть фільтр.</span>
          </div>
        </div>
      ) : (
        <div className="cards-flow">
          {doctors.map((doctor) => {
            const active = selectedDoctorId === doctor.id;
            return (
              <button className={`doctor-card ${active ? 'is-active' : ''}`} key={doctor.id} onClick={() => onSelect(doctor)}>
              <span className="doctor-card-head">
                <span className="avatar" aria-hidden="true">
                  {initials(doctor.fullName)}
                </span>
                <span className="doctor-main">
                  <span className="doctor-kicker">{doctor.specialty.name}</span>
                  <strong>{doctor.fullName}</strong>
                  <small>
                    <MapPin size={14} aria-hidden="true" />
                    {doctor.city}, {doctor.clinic}
                  </small>
                </span>
                <span className="doctor-meta">
                  <span>
                    <Star size={14} aria-hidden="true" />
                    {doctor.rating}
                  </span>
                </span>
              </span>
              <span className="doctor-tags">
                <span>
                  <CalendarDays size={15} aria-hidden="true" />
                  {doctor.availableSlots.length} слотів
                </span>
                <span>{doctor.yearsExperience} років стажу</span>
                <span>
                  <WalletCards size={15} aria-hidden="true" />
                  {doctor.consultationPrice} грн
                </span>
              </span>
              <span className="doctor-action">
                Обрати час
                <ArrowRight size={17} aria-hidden="true" />
              </span>
              </button>
            );
          })}
        </div>
      )}
    </section>
  );
}
