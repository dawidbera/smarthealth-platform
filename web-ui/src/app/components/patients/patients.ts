import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, Patient } from '../../services/api';

@Component({
  selector: 'app-patients',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './patients.html',
  styleUrl: './patients.scss'
})
export class Patients implements OnInit {
  patients: Patient[] = [];
  loading = true;
  error = '';

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.loadPatients();
  }

  loadPatients(): void {
    this.loading = true;
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
}