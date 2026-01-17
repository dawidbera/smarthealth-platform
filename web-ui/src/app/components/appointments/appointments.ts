import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Appointment, Patient } from '../../services/api';

@Component({
  selector: 'app-appointments',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './appointments.html',
  styleUrl: './appointments.scss'
})
export class Appointments implements OnInit {
  appointments: Appointment[] = [];
  patients: Patient[] = [];
  loading = true;
  appointmentForm: FormGroup;
  showModal = false;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.appointmentForm = this.fb.group({
      patientId: ['', Validators.required],
      doctorId: [1, Validators.required], // Default doctor ID
      appointmentTime: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    this.apiService.getAppointments().subscribe({
      next: (data) => {
        this.appointments = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching appointments', err);
        this.loading = false;
      }
    });

    this.apiService.getPatients().subscribe({
      next: (data) => this.patients = data,
      error: (err) => console.error('Error fetching patients', err)
    });
  }

  onSubmit(): void {
    if (this.appointmentForm.valid) {
      this.apiService.bookAppointment(this.appointmentForm.value).subscribe({
        next: () => {
          this.loadData();
          this.appointmentForm.reset({ doctorId: 1 });
          this.showModal = false;
        },
        error: (err) => {
          console.error('Error booking appointment', err);
          alert('Failed to book appointment. Ensure the patient exists.');
        }
      });
    }
  }

  getPatientName(patientId: number): string {
    const patient = this.patients.find(p => p.id === patientId);
    return patient ? `${patient.firstName} ${patient.lastName}` : `ID: ${patientId}`;
  }

  toggleModal(): void {
    this.showModal = !this.showModal;
  }
}