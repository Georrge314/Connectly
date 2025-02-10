import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { firstValueFrom } from 'rxjs';
import { AuthenticationService } from '../../api/api/authentication.service';
import { LoginRequestDto } from '../../api/model/loginRequestDto';
import { JwtResponse } from '../../api';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
})
export class LoginComponent {
  loginForm: FormGroup;
  responseMessage: string | null = null;

  constructor(
    private fb: FormBuilder,
    private authService: AuthenticationService
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required]], // TODO Validators.email
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  async onSubmit() {
    if (this.loginForm.valid) {
      const loginRequest: LoginRequestDto = {
        username: this.loginForm.value.email,
        password: this.loginForm.value.password,
      };

      this.authService.login(loginRequest).subscribe({
        next: (response: JwtResponse) => {
          console.log('Response:', response); // Access the full
          console.log('Token:', response.token); // Access the JWT token
        },
        error: (error) => {
          console.error('Login error:', error);
          this.responseMessage = 'Login failed. Please check your credentials.';
        },
      });
    }
  }


}
