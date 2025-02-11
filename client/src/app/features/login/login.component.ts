import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import {
  ReactiveFormsModule,
  FormGroup,
  FormBuilder,
  Validators,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthenticationService } from '../../api/api/authentication.service';
import { LoginRequestDto } from '../../api/model/loginRequestDto';

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
    private authService: AuthenticationService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  async onSubmit() {
    if (this.loginForm.valid) {

      const loginRequest: LoginRequestDto = {
        email: this.loginForm.value.email,
        password: this.loginForm.value.password,
      };

      this.authService.login(loginRequest).subscribe({
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
