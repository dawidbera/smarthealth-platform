import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Patient } from '../../services/api';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: './patients.html',
  styleUrl: './patients.scss'
})
export class Patients implements OnInit {
  patients: Patient[] = [];
  loading = true;
  error = '';
  patientForm: FormGroup;
  showModal = false;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.patientForm = this.fb.group({
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      nationalId: ['', [Validators.required, Validators.minLength(1)]]
    });
  }

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading = true;
    this.error = '';
    this.apiService.getPatients().subscribe({
      next: (data) => {
        this.patients = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching patients', err);
        this.error = 'Could not load patients. Please check if API Gateway is running.';
        this.loading = false;
      }
    });
  }

  onSubmit(): void {
    if (this.patientForm.valid) {
      this.apiService.createPatient(this.patientForm.value).subscribe({
        next: () => {
          this.loadPatients();
          this.patientForm.reset();
          this.showModal = false;
        },
        error: (err) => {
          console.error('Error creating patient', err);
          alert('Failed to add patient.');
        }
      });
    }
  }

  toggleModal(): void {
    this.showModal = !this.showModal;
  }
}