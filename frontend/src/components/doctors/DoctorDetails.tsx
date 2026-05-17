import type { FormEvent } from 'react';
import { CalendarDays, Clock3, Languages, MapPin, Star, UserRound, WalletCards } from 'lucide-react';
import type { AppointmentPayload, Doctor, Slot } from '../../types';
import { formatDateShort, formatDateTime, formatDayLabel, formatTime } from '../../utils/format';

type DoctorDetailsProps = {
  appointment: AppointmentPayload;
  doctor: Doctor | null;
  selectedDate: string;
  slots: Slot[];
  onAppointmentChange: (appointment: AppointmentPayload) => void;
  onSubmit: (event: FormEvent<HTMLFormElement>) => void;
};

export function DoctorDetails({ appointment, doctor, selectedDate, slots, onAppointmentChange, onSubmit }: DoctorDetailsProps) {
  if (!doctor) {
    return (
      <section className="details-panel" id="appointment" aria-label="Запис">
        <div className="empty-state">Немає лікарів за вибраними параметрами.</div>
      </section>
    );
  }

  function update(key: keyof AppointmentPayload, value: string) {
    onAppointmentChange({ ...appointment, [key]: value });
  }

  const slotDates = Array.from(new Set(slots.map((slot) => formatDayLabel(slot.startsAt))));
  const slotsDateLabel = selectedDate
    ? formatDayLabel(selectedDate)
    : slotDates.length === 1
      ? slotDates[0]
      : 'найближчі доступні дати';

  return (
    <section className="details-panel" id="appointment" aria-label="Запис">
      <div className="doctor-profile">
        <div className="profile-avatar" aria-hidden="true">
          {doctor.fullName
            .split(' ')
            .map((part) => part[0])
            .join('')
            .slice(0, 2)}
        </div>
        <div className="profile-heading">
          <p>{doctor.specialty.name}</p>
          <h2>{doctor.fullName}</h2>
          <span>{doctor.clinic}</span>
        </div>
        <div className="rating-badge" title="Рейтинг лікаря">
          <Star size={16} aria-hidden="true" />
          {doctor.rating}
        </div>
      </div>

      <div className="profile-grid">
        <span>
          <MapPin size={16} aria-hidden="true" />
          {doctor.address}
        </span>
        <span>
          <UserRound size={16} aria-hidden="true" />
          Стаж роботи: {doctor.yearsExperience} років
        </span>
        <span>
          <CalendarDays size={16} aria-hidden="true" />
          Доступно слотів: {slots.length}
        </span>
        <span>
          <WalletCards size={16} aria-hidden="true" />
          Вартість консультації: {doctor.consultationPrice} грн
        </span>
        <span>
          <Languages size={16} aria-hidden="true" />
          Мови консультації: {doctor.languages.join(', ')}
        </span>
      </div>

      <div className="slots-heading">
        <div>
          <h3>Доступний час</h3>
          <p>{slotsDateLabel}</p>
        </div>
        <span>{slots.length}</span>
      </div>
      <div className="slots">
        {slots.map((slot) => (
          <button
            className={appointment.slotId === slot.id ? 'slot is-selected' : 'slot'}
            key={slot.id}
            onClick={() => onAppointmentChange({ ...appointment, doctorId: doctor.id, slotId: slot.id })}
            title={formatDateTime(slot.startsAt)}
          >
            <Clock3 size={16} aria-hidden="true" />
            <span>
              <strong>{formatTime(slot.startsAt)}</strong>
              <small>{formatDateShort(slot.startsAt)}</small>
            </span>
          </button>
        ))}
      </div>

      <form className="appointment-form" onSubmit={onSubmit}>
        <div className="form-heading">
          <h3>Дані пацієнта</h3>
        </div>
        <div className="form-grid">
          <label>
            Ім'я пацієнта
            <input value={appointment.patientName} onChange={(event) => update('patientName', event.target.value)} required />
          </label>
          <label>
            Телефон
            <input
              value={appointment.phone}
              onChange={(event) => update('phone', event.target.value)}
              placeholder="+380501112233"
              required
            />
          </label>
          <label>
            Email
            <input type="email" value={appointment.email} onChange={(event) => update('email', event.target.value)} />
          </label>
          <label className="is-wide">
            Примітка
            <textarea value={appointment.notes} onChange={(event) => update('notes', event.target.value)} />
          </label>
        </div>
        <button className="primary-button" type="submit" disabled={!appointment.slotId}>
          <CalendarDays size={18} aria-hidden="true" />
          Записати
        </button>
      </form>
    </section>
  );
}
