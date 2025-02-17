import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { FeedComponent } from '../feed/feed.component';
import { RightSidebarComponent } from '../right-sidebar/right-sidebar.component';

interface UserProfile {
  avatarUrl: string;
  name: string;
  bio: string;
  email: string;
  location: string;
  joinedDate: string;
  posts: { title: string; content: string }[];
  friends: { name: string; avatarUrl: string }[];
}

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FeedComponent, RightSidebarComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css',
})
export class ProfileComponent {
  userProfile: UserProfile = {
    avatarUrl:
      'https://64.media.tumblr.com/fababb2954021811072b391f73db1413/91b45c92c3c50f84-c7/s1280x1920/bbdba078377976b577e5f2e5882d16d59e336613.png',
    name: 'Walter White',
    bio: 'Chemistry teacher turned meth cook.',
    email: 'walterWhite@gmail.com',
    location: 'New Mexico, USA',
    joinedDate: 'January 2024',
    posts: [
      {
        title: 'My First Post',
        content:
          'Hello everyone, this is my first post. I am excited to be here!',
      },
      {
        title: 'My First Post',
        content:
          'Hello everyone, this is my first post. I am excited to be here!',
      },
      {
        title: 'My First Post',
        content:
          'Hello everyone, this is my first post. I am excited to be here!',
      },
      {
        title: 'My First Post',
        content:
          'Hello everyone, this is my first post. I am excited to be here!',
      },
    ],
    friends: [
      {
        name: 'Jasse Pinkman',
        avatarUrl:
          'https://upload.wikimedia.org/wikipedia/en/c/c6/Jesse_Pinkman_S5B.png',
      },
      {
        name: 'Jasse Pinkman',
        avatarUrl:
          'https://upload.wikimedia.org/wikipedia/en/c/c6/Jesse_Pinkman_S5B.png',
      },
    ],
  };

  constructor(private router: Router) {}

  editProfile() {
    const profileData = {
      avatarUrl: this.userProfile.avatarUrl,
      name: this.userProfile.name,
      bio: this.userProfile.bio,
      email: this.userProfile.email,
      location: this.userProfile.location,
    }
    this.router.navigate(['/edit-profile'], {state: {profileData: profileData}});
  }
}
