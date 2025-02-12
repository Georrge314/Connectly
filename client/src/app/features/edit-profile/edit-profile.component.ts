import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-edit-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './edit-profile.component.html',
  styleUrl: './edit-profile.component.css',
})
export class EditProfileComponent {
  editProfileForm: FormGroup;
  avatarUrl: string = '';

  constructor(private fb: FormBuilder, private router: Router) {
    this.editProfileForm = this.fb.group({
      avatarUrl: ['', Validators.required],
      name: ['', [Validators.required, Validators.minLength(2)]],
      bio: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      location: ['', Validators.required],
    });

    // Load existing profile data from router state
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras.state as { profileData: any };
    if (state && state.profileData) {
      this.loadProfileData(state.profileData);
    }

    this.editProfileForm.get('avatarUrl')?.valueChanges.subscribe(value => {
      this.avatarUrl = value;
    });
  }

  loadProfileData(profileData: any) {
    this.editProfileForm.patchValue(profileData);
    this.avatarUrl = profileData.avatarUrl;
  }

  onSubmit() {
    if (this.editProfileForm.valid) {
      // Save profile data (this should be replaced with actual save logic)
      console.log('Profile updated:', this.editProfileForm.value);
      this.router.navigate(['/profile']);
    }
  }
}
