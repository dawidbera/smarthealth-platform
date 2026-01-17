import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Patient {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  nationalId: string;
}

export interface Appointment {
  id?: number;
  patientId: number;
  doctorId: number;
  appointmentTime: string;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080';

  constructor(private http: HttpClient) { }

  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/patient`);
  }

  createPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(`${this.baseUrl}/patient`, patient);
  }

  getAppointments(): Observable<Appointment[]> {
    return this.http.get<Appointment[]>(`${this.baseUrl}/appointment`);
  }

  bookAppointment(appointment: Appointment): Observable<Appointment> {
    return this.http.post<Appointment>(`${this.baseUrl}/appointment`, appointment);
  }
}
