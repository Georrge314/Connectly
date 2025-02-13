import { Routes } from '@angular/router';
import { AuthGuard } from './guards/auth.guard';

export const routes: Routes = [
    {
        path: '', 
        loadComponent: () => import('./features/home/home.component').then(mod => mod.HomeComponent),
        canActivate: [AuthGuard]
    },
    {
        path: 'profile', 
        loadComponent: () => import('./features/profile/profile.component').then(mod => mod.ProfileComponent),
        canActivate: [AuthGuard]
    },
    {
        path: 'login', 
        loadComponent: () => import('./features/login/login.component').then(mod => mod.LoginComponent)
    },
    {
        path: 'register', 
        loadComponent: () => import('./features/register/register.component').then(mod => mod.RegisterComponent)
    },
    {
        path: 'edit-profile',
        loadComponent: () => import('./features/edit-profile/edit-profile.component').then(mod => mod.EditProfileComponent),
        canActivate: [AuthGuard]
    },
    {
        path: '**',
        redirectTo: '/login'
    }
];
