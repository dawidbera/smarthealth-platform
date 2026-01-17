import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { Patients } from './components/patients/patients';
import { Appointments } from './components/appointments/appointments';
import { Billing } from './components/billing/billing';

export const routes: Routes = [
  { path: 'dashboard', component: Dashboard },
  { path: 'patients', component: Patients },
  { path: 'appointments', component: Appointments },
  { path: 'billing', component: Billing },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  { path: '**', redirectTo: '/dashboard' }
];