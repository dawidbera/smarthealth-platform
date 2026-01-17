import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api';
import { interval, Subscription } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.scss'
})
export class Dashboard implements OnInit, OnDestroy {
  latestHeartRate: number | null = null;
  deviceStatus: string = 'Offline';
  private subscription: Subscription | null = null;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    // Poll telemetry every 5 seconds
    this.subscription = interval(5000).subscribe(() => {
      this.fetchTelemetry();
    });
    this.fetchTelemetry();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  fetchTelemetry(): void {
    this.apiService.getLatestTelemetry('HR-MON-001').subscribe({
      next: (data) => {
        if (data) {
          this.latestHeartRate = data.value;
          this.deviceStatus = 'Online';
        } else {
          this.deviceStatus = 'Waiting for sensor...';
          this.latestHeartRate = null;
        }
      },
      error: () => {
        this.deviceStatus = 'Connection Error';
      }
    });
  }
}