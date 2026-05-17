export type Specialty = {
  id: string;
  name: string;
  description: string;
};

export type Slot = {
  id: string;
  doctorId: string;
  startsAt: string;
  endsAt: string;
};

export type Doctor = {
  id: string;
  fullName: string;
  specialty: Specialty;
  city: string;
  clinic: string;
  address: string;
  rating: number;
  yearsExperience: number;
  consultationPrice: string;
  languages: string[];
  availableSlots: Slot[];
};

export type Appointment = {
  id: string;
  doctorId: string;
  slotId: string;
  patientName: string;
  phone: string;
  email: string;
  notes: string;
  startsAt: string;
  endsAt: string;
  createdAt: string;
  status: 'SCHEDULED' | 'CANCELLED';
};

export type AppointmentPayload = {
  doctorId: string;
  slotId: string;
  patientName: string;
  phone: string;
  email: string;
  notes: string;
};

export type DoctorFilters = {
  specialty: string;
  city: string;
  date: string;
  minRating: string;
  maxPrice: string;
  minExperience: string;
  sort: string;
  q: string;
};
