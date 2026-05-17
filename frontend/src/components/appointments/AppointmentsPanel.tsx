import type { FormEvent } from 'react';
import { SearchCheck, Trash2 } from 'lucide-react';
import type { Appointment } from '../../types';
import { formatDateTime, isPastDateTime } from '../../utils/format';

type AppointmentsPanelProps = {
  appointments: Appointment[];
  phoneLookup: string;
  onCancel: (appointmentId: string) => void;
  onLookup: (event: FormEvent<HTMLFormElement>) => void;
  onPhoneChange: (phone: string) => void;
};

export function AppointmentsPanel({ appointments, phoneLookup, onCancel, onLookup, onPhoneChange }: AppointmentsPanelProps) {
  return (
    <section className="appointments-panel" id="appointments">
      <div className="appointments-heading">
        <div>
          <h2>Перевірити запис</h2>
          <p>Знайти бронювання пацієнта за телефоном</p>
        </div>
        {appointments.length > 0 && <span>{appointments.length}</span>}
      </div>
      <form className="lookup-form" onSubmit={onLookup}>
        <label>
          Телефон пацієнта
          <input value={phoneLookup} onChange={(event) => onPhoneChange(event.target.value)} placeholder="+380501112233" />
        </label>
        <button className="secondary-button" type="submit">
          <SearchCheck size={18} aria-hidden="true" />
          Перевірити
        </button>
      </form>

      <div className="appointment-list">
        {appointments.map((item) => {
          const isCancelled = item.status === 'CANCELLED';
          const isCompleted = !isCancelled && isPastDateTime(item.endsAt || item.startsAt);
          const statusClass = isCancelled ? 'cancelled' : isCompleted ? 'completed' : 'scheduled';
          const statusLabel = isCancelled ? 'Скасований' : isCompleted ? 'Завершений' : 'Активний';
          const canCancel = !isCancelled && !isCompleted;

          return (
            <article className="appointment-card" key={item.id}>
              <div>
                <strong>{item.patientName}</strong>
                <span>{formatDateTime(item.startsAt)}</span>
              </div>
              <span className={`status ${statusClass}`}>{statusLabel}</span>
              <button
                className="icon-button"
                onClick={() => onCancel(item.id)}
                disabled={!canCancel}
                title={canCancel ? 'Скасувати запис' : 'Скасування недоступне'}
                aria-label={canCancel ? 'Скасувати запис' : 'Скасування недоступне'}
              >
                <Trash2 size={18} aria-hidden="true" />
              </button>
            </article>
          );
        })}
      </div>
    </section>
  );
}
