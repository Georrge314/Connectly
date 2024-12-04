import { Component } from '@angular/core';
import { FeedComponent } from '../feed/feed.component';
import { LeftSidebarComponent } from '../left-sidebar/left-sidebar.component';
import { RightSidebarComponent } from '../right-sidebar/right-sidebar.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [FeedComponent, LeftSidebarComponent, RightSidebarComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent {

}
