import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css'],
})
export class NavbarComponent {
  constructor(private router: Router) {}

  isAuthenticated(): boolean {
    return typeof localStorage !== 'undefined' && !!localStorage.getItem('token');
  }

  logout() {
    if (typeof localStorage !== 'undefined') {
      localStorage.clear();
    }
    this.router.navigate(['/login']);
  }

  goToProfile() {
    this.router.navigate(['/profile']);
  }

  onSearch(query: string) {
    // Implement search functionality here
    console.log('Search query:', query);
    // Navigate to search results page or perform search logic
    // this.router.navigate(['/search'], { queryParams: { q: query } });
  }
}
