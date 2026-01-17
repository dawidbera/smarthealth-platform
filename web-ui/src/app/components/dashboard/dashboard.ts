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
  history: any[] = [];
  stats: any = null;
  private subscription: Subscription | null = null;
  private statsSubscription: Subscription | null = null;

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    // Poll telemetry every 5 seconds
    this.subscription = interval(5000).subscribe(() => {
      this.fetchTelemetry();
    });

    // Poll stats and history every 10 seconds
    this.statsSubscription = interval(10000).subscribe(() => {
      this.fetchAnalytics();
    });

    this.fetchTelemetry();
    this.fetchAnalytics();
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.statsSubscription) {
      this.statsSubscription.unsubscribe();
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

  fetchAnalytics(): void {
    const sn = 'HR-MON-001';
    this.apiService.getTelemetryHistory(sn, 5).subscribe(data => {
      this.history = data;
    });
    this.apiService.getTelemetryStats(sn).subscribe(data => {
      this.stats = data;
    });
  }
}