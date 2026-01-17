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

export interface Invoice {
  id?: number;
  appointmentId: number;
  amount: number;
  status: string;
  createdAt: string;
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

  getInvoices(): Observable<Invoice[]> {
    return this.http.get<Invoice[]>(`${this.baseUrl}/billing/invoices`);
  }

  getLatestTelemetry(serialNumber: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/device/telemetry/${serialNumber}/latest`);
  }

  getTelemetryHistory(serialNumber: string, limit: number = 10): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/analytics/device/${serialNumber}/history?limit=${limit}`);
  }

  getTelemetryStats(serialNumber: string): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/analytics/device/${serialNumber}/stats`);
  }
}
