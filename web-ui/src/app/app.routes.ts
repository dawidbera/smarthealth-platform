import { Routes } from '@angular/router';
import { DashboardComponent } from './components/dashboard/dashboard';
import { PatientsComponent } from './components/patients/patients';
import { AppointmentsComponent } from './components/appointments/appointments';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'patients', component: PatientsComponent },
  { path: 'appointments', component: AppointmentsComponent },
  { path: '**', redirectTo: 'dashboard' }
];