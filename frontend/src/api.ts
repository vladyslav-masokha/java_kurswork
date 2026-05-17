import type { Appointment, AppointmentPayload, Doctor, DoctorFilters, Slot, Specialty } from './types';

const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

async function request<T>(path: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_URL}${path}`, {
    headers: {
      'Content-Type': 'application/json',
      ...(options.headers ?? {})
    },
    ...options
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    throw new Error(data?.error ?? 'Помилка запиту до сервера');
  }

  return data as T;
}

export async function getSpecialties(): Promise<Specialty[]> {
  return request<Specialty[]>('/api/specialties');
}

export async function searchDoctors(filters: DoctorFilters): Promise<Doctor[]> {
  const params = new URLSearchParams();
  Object.entries(filters).forEach(([key, value]) => {
    if (value.trim() && !(key === 'sort' && value === 'ratingDesc')) {
      params.set(key, value.trim());
    }
  });

  const query = params.toString();
  return request<Doctor[]>(`/api/doctors${query ? `?${query}` : ''}`);
}

export async function getDoctorSlots(doctorId: string, date: string): Promise<Slot[]> {
  const params = new URLSearchParams();
  if (date.trim()) {
    params.set('date', date.trim());
  }
  const query = params.toString();
  return request<Slot[]>(`/api/doctors/${encodeURIComponent(doctorId)}/slots${query ? `?${query}` : ''}`);
}

export async function createAppointment(payload: AppointmentPayload): Promise<Appointment> {
  return request<Appointment>('/api/appointments', {
    method: 'POST',
    body: JSON.stringify(payload)
  });
}

export async function findAppointments(phone: string): Promise<Appointment[]> {
  return request<Appointment[]>(`/api/appointments?phone=${encodeURIComponent(phone)}`);
}

export async function cancelAppointment(appointmentId: string): Promise<Appointment> {
  return request<Appointment>(`/api/appointments/${encodeURIComponent(appointmentId)}`, {
    method: 'DELETE'
  });
}
