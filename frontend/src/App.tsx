import { useEffect, useMemo, useRef, useState, type FormEvent } from 'react';
import { X } from 'lucide-react';
import { createAppointment, cancelAppointment, findAppointments, getDoctorSlots, getSpecialties, searchDoctors } from './api';
import { AppointmentsPanel } from './components/appointments/AppointmentsPanel';
import { DoctorDetails } from './components/doctors/DoctorDetails';
import { DoctorList } from './components/doctors/DoctorList';
import { FiltersPanel } from './components/filters/FiltersPanel';
import { TopBar } from './components/layout/TopBar';
import { Toast } from './components/feedback/Toast';
import { useTheme } from './hooks/useTheme';
import type { Appointment, AppointmentPayload, Doctor, DoctorFilters, Slot, Specialty } from './types';
import { formatDateTime, tomorrowDateInput } from './utils/format';

function createInitialFilters(): DoctorFilters {
  return {
    specialty: '',
    city: '',
    date: tomorrowDateInput(),
    minRating: '',
    maxPrice: '',
    minExperience: '',
    sort: 'ratingDesc',
    q: ''
  };
}

const initialAppointment: AppointmentPayload = {
  doctorId: '',
  slotId: '',
  patientName: '',
  phone: '',
  email: '',
  notes: ''
};

function App() {
  const [specialties, setSpecialties] = useState<Specialty[]>([]);
  const [allDoctors, setAllDoctors] = useState<Doctor[]>([]);
  const [doctors, setDoctors] = useState<Doctor[]>([]);
  const [selectedDoctor, setSelectedDoctor] = useState<Doctor | null>(null);
  const [slots, setSlots] = useState<Slot[]>([]);
  const [filters, setFilters] = useState<DoctorFilters>(() => createInitialFilters());
  const [appointment, setAppointment] = useState<AppointmentPayload>(initialAppointment);
  const [phoneLookup, setPhoneLookup] = useState('');
  const [appointments, setAppointments] = useState<Appointment[]>([]);
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [bookingOpen, setBookingOpen] = useState(false);
  const [filtersReady, setFiltersReady] = useState(false);
  const searchRequestRef = useRef(0);
  const { theme, toggleTheme } = useTheme();

  useEffect(() => {
    void bootstrap();
  }, []);

  useEffect(() => {
    if (!filtersReady) {
      return;
    }

    const timeoutId = window.setTimeout(() => {
      void applyFilters(filters);
    }, 250);

    return () => window.clearTimeout(timeoutId);
  }, [filters, filtersReady]);

  useEffect(() => {
    if (!bookingOpen) {
      return;
    }

    const previousOverflow = document.body.style.overflow;

    function closeOnEscape(event: KeyboardEvent) {
      if (event.key === 'Escape') {
        closeBooking();
      }
    }

    document.body.style.overflow = 'hidden';
    window.addEventListener('keydown', closeOnEscape);

    return () => {
      document.body.style.overflow = previousOverflow;
      window.removeEventListener('keydown', closeOnEscape);
    };
  }, [bookingOpen]);

  async function bootstrap() {
    setError('');
    try {
      const [specialtyData, doctorData] = await Promise.all([getSpecialties(), searchDoctors(filters)]);
      setSpecialties(specialtyData);
      setAllDoctors(doctorData);
      setDoctors(doctorData);
    } catch (err) {
      showError(err);
    } finally {
      setFiltersReady(true);
    }
  }

  async function resetFilters() {
    setFilters(createInitialFilters());
  }

  async function handleSearch() {
    await applyFilters(filters);
  }

  async function applyFilters(criteria: DoctorFilters) {
    const requestId = ++searchRequestRef.current;

    setLoading(true);
    setError('');
    closeBooking();
    try {
      const result = await searchDoctors(criteria);
      if (requestId !== searchRequestRef.current) {
        return;
      }
      setDoctors(result);
    } catch (err) {
      if (requestId === searchRequestRef.current) {
        showError(err);
      }
    } finally {
      if (requestId === searchRequestRef.current) {
        setLoading(false);
      }
    }
  }

  async function refreshDoctors(criteria: DoctorFilters = filters) {
    const requestId = ++searchRequestRef.current;

    try {
      const result = await searchDoctors(criteria);
      if (requestId === searchRequestRef.current) {
        setDoctors(result);
      }
    } catch (err) {
      if (requestId === searchRequestRef.current) {
        showError(err);
      }
    } finally {
      if (requestId === searchRequestRef.current) {
        setLoading(false);
      }
    }
  }

  async function selectDoctor(doctor: Doctor, dateOverride = filters.date) {
    setSelectedDoctor(doctor);
    setSlots([]);
    setAppointment((current) => ({ ...current, doctorId: doctor.id, slotId: '' }));
    setBookingOpen(true);
    try {
      setSlots(await getDoctorSlots(doctor.id, dateOverride));
    } catch (err) {
      showError(err);
    }
  }

  function closeBooking() {
    setBookingOpen(false);
    setSelectedDoctor(null);
    setSlots([]);
    setAppointment(initialAppointment);
  }

  async function submitAppointment(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    setMessage('');
    try {
      const payload = {
        ...appointment,
        doctorId: selectedDoctor?.id ?? appointment.doctorId
      };
      const created = await createAppointment(payload);
      setMessage(`Запис ${created.id} створено на ${formatDateTime(created.startsAt)}.`);
      if (selectedDoctor) {
        setSlots(await getDoctorSlots(selectedDoctor.id, filters.date));
      }
      await refreshDoctors();
      closeBooking();
    } catch (err) {
      showError(err);
    }
  }

  async function lookupAppointments(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setError('');
    try {
      setAppointments(await findAppointments(phoneLookup));
    } catch (err) {
      showError(err);
    }
  }

  async function handleCancel(appointmentId: string) {
    setError('');
    try {
      await cancelAppointment(appointmentId);
      setAppointments((items) =>
        items.map((item) => (item.id === appointmentId ? { ...item, status: 'CANCELLED' } : item))
      );
      if (selectedDoctor) {
        setSlots(await getDoctorSlots(selectedDoctor.id, filters.date));
      }
      await refreshDoctors();
    } catch (err) {
      showError(err);
    }
  }

  function showError(err: unknown) {
    setError(err instanceof Error ? err.message : 'Невідома помилка');
  }

  const cities = useMemo(() => Array.from(new Set(allDoctors.map((doctor) => doctor.city))).sort(), [allDoctors]);

  return (
    <main className="app-shell">
      <TopBar theme={theme} onToggleTheme={toggleTheme} />

      <section className="search-layout">
        <FiltersPanel
          cities={cities}
          filters={filters}
          loading={loading}
          specialties={specialties}
          onChange={setFilters}
          onReset={() => void resetFilters()}
          onSearch={handleSearch}
        />

        <section className="workspace">
          <DoctorList doctors={doctors} selectedDoctorId={bookingOpen ? selectedDoctor?.id : undefined} onSelect={(doctor) => void selectDoctor(doctor, filters.date)} />
        </section>
      </section>

      {bookingOpen && selectedDoctor && (
        <div className="booking-backdrop" role="presentation" onClick={closeBooking}>
          <div className="booking-dialog" role="dialog" aria-modal="true" aria-label={`Запис до ${selectedDoctor.fullName}`} onClick={(event) => event.stopPropagation()}>
            <button className="booking-close icon-button" type="button" onClick={closeBooking} aria-label="Закрити вікно запису">
              <X size={19} aria-hidden="true" />
            </button>
            <DoctorDetails
              appointment={appointment}
              doctor={selectedDoctor}
              selectedDate={filters.date}
              slots={slots}
              onAppointmentChange={setAppointment}
              onSubmit={submitAppointment}
            />
          </div>
        </div>
      )}

      <AppointmentsPanel
        appointments={appointments}
        phoneLookup={phoneLookup}
        onCancel={(appointmentId) => void handleCancel(appointmentId)}
        onLookup={lookupAppointments}
        onPhoneChange={setPhoneLookup}
      />

      <Toast error={error} message={message} />
    </main>
  );
}

export default App;
