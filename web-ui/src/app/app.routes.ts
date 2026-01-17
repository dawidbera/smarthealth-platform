import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { Patients } from './components/patients/patients';
import { Appointments } from './components/appointments/appointments';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'dashboard', component: Dashboard },
  { path: 'patients', component: Patients },
  { path: 'appointments', component: Appointments },
  { path: '**', redirectTo: 'dashboard' }
];
