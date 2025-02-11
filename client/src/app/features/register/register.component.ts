import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';

import { AuthenticationService } from '../../api/api/authentication.service';
import { passwordMatchValidator } from './password-match.validator';
import { CommonModule } from '@angular/common';
import { RegisterRequestDto } from '../../api/model/registerRequestDto';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule, CommonModule],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css',
})
export class RegisterComponent {
  registerForm: FormGroup;
  responseMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required],
    });

    this.registerForm.setValidators(passwordMatchValidator);
  }

  onSubmit() {
    if (this.registerForm.valid) {
      const registerRequest: RegisterRequestDto = {
        email: this.registerForm.value.email,
        password: this.registerForm.value.password,
        firstName: this.registerForm.value.firstName,
        lastName: this.registerForm.value.lastName,
      };

      this.authService.register(registerRequest).subscribe({
        next: async (response: any) => {
          const text = await response.text();
          const json = JSON.parse(text);
          
          localStorage.setItem('token', json.token);
          this.router.navigate(['/']);
        },
        error: async (error) => {
          const errorMessage = await error.error.text();
          this.responseMessage = errorMessage
        },
      });
    }
  }
}
