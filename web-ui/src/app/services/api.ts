import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Patient {
  id?: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = 'http://localhost:8080'; // Adres API Gateway

  constructor(private http: HttpClient) { }

  getPatients(): Observable<Patient[]> {
    return this.http.get<Patient[]>(`${this.baseUrl}/patient/api/patients`);
  }

  createPatient(patient: Patient): Observable<Patient> {
    return this.http.post<Patient>(`${this.baseUrl}/patient/api/patients`, patient);
  }
}