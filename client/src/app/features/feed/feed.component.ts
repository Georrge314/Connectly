import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

interface Comment {
  userAvatar: string;
  userName: string;
  timeAgo: string;
  content: string;
  likes: number;
  comments?: Comment[];
}

interface Post {
  userAvatar: string;
  userName: string;
  userEmail: string; // Add userEmail property
  timeAgo: string;
  location?: string;
  tags?: string[];
  content?: string;
  postImage?: string[];
  postMedia?: string[];
  likes: number;
  shares: number;
  comments: Comment[];
  showComments?: boolean;
}

@Component({
  selector: 'app-feed',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css'],
})
export class FeedComponent {
  @Input() email?: string;

  posts: Post[] = [
    {
      userAvatar:
        'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
      userName: 'John Doe',
      userEmail: 'john.doe@example.com',
      timeAgo: '2 hours ago',
      content:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.',
      postImage: [
        'https://hips.hearstapps.com/hmg-prod/images/champagne-beach-espiritu-santo-island-vanuatu-royalty-free-image-1655672510.jpg',
      ],
      likes: 10,
      shares: 2,
      comments: [
        {
          userAvatar:
            'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
          userName: 'Jane Smith',
          timeAgo: '1 hour ago',
          content: 'Great post!',
          likes: 5,
          comments: [
            {
              userAvatar:
                'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
              userName: 'John Doe',
              timeAgo: '30 minutes ago',
              content: 'Thank you!',
              likes: 2,
              comments: [],
            },
          ],
        },
      ],
      showComments: false, // Initialize the property
    },
    {
      userAvatar:
        'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
      userName: 'John Doe',
      userEmail: 'john.doe@example.com',
      timeAgo: '2 hours ago',
      content:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.',
      postImage: [
        'https://hips.hearstapps.com/hmg-prod/images/champagne-beach-espiritu-santo-island-vanuatu-royalty-free-image-1655672510.jpg',
      ],
      likes: 10,
      shares: 2,
      comments: [
        {
          userAvatar:
            'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
          userName: 'Jane Smith',
          timeAgo: '1 hour ago',
          content: 'Great post!',
          likes: 5,
          comments: [
            {
              userAvatar:
                'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
              userName: 'John Doe',
              timeAgo: '30 minutes ago',
              content: 'Thank you!',
              likes: 2,
              comments: [],
            },
          ],
        },
      ],
      showComments: false, // Initialize the property
    },
    {
      userAvatar:
        'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
      userName: 'John Doe',
      userEmail: 'john.doe@example.com',
      timeAgo: '2 hours ago',
      content:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.',
      postImage: [
        'https://hips.hearstapps.com/hmg-prod/images/champagne-beach-espiritu-santo-island-vanuatu-royalty-free-image-1655672510.jpg',
      ],
      likes: 10,
      shares: 2,
      comments: [
        {
          userAvatar:
            'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
          userName: 'Jane Smith',
          timeAgo: '1 hour ago',
          content: 'Great post!',
          likes: 5,
          comments: [
            {
              userAvatar:
                'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
              userName: 'John Doe',
              timeAgo: '30 minutes ago',
              content: 'Thank you!',
              likes: 2,
              comments: [],
            },
          ],
        },
      ],
      showComments: false, // Initialize the property
    },
    {
      userAvatar:
        'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
      userName: 'John Doe',
      userEmail: 'john.doe@example.com',
      timeAgo: '2 hours ago',
      content:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.',
      postImage: [
        'https://hips.hearstapps.com/hmg-prod/images/champagne-beach-espiritu-santo-island-vanuatu-royalty-free-image-1655672510.jpg',
      ],
      likes: 10,
      shares: 2,
      comments: [
        {
          userAvatar:
            'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
          userName: 'Jane Smith',
          timeAgo: '1 hour ago',
          content: 'Great post!',
          likes: 5,
          comments: [
            {
              userAvatar:
                'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
              userName: 'John Doe',
              timeAgo: '30 minutes ago',
              content: 'Thank you!',
              likes: 2,
              comments: [],
            },
          ],
        },
      ],
      showComments: false, // Initialize the property
    },
    {
      userAvatar:
        'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
      userName: 'John Doe',
      userEmail: 'john.doe@example.com',
      timeAgo: '2 hours ago',
      content:
        'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus lacinia odio vitae vestibulum vestibulum.',
      postImage: [
        'https://hips.hearstapps.com/hmg-prod/images/champagne-beach-espiritu-santo-island-vanuatu-royalty-free-image-1655672510.jpg',
      ],
      likes: 10,
      shares: 2,
      comments: [
        {
          userAvatar:
            'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
          userName: 'Jane Smith',
          timeAgo: '1 hour ago',
          content: 'Great post!',
          likes: 5,
          comments: [
            {
              userAvatar:
                'https://hips.hearstapps.com/hmg-prod/images/will-smith-attends-varietys-creative-impact-awards-and-10-directors-to-watch-brunch-at-the-parker-palm-springs-on-january-3-2016-in-palm-springs-california-photo-by-jerod-harrisgetty-images.jpg?crop=1xw:1.0xh;center,top&resize=640:*',
              userName: 'John Doe',
              timeAgo: '30 minutes ago',
              content: 'Thank you!',
              likes: 2,
              comments: [],
            },
          ],
        },
      ],
      showComments: false, // Initialize the property
    },
    // Add more posts as needed
  ];

  
  postForm: FormGroup;

  constructor(
    private fb: FormBuilder,
  ) {
    this.postForm = this.fb.group({
      content: ['', [Validators.required]],
      imageUrls: [],
      videoUrl: [],
      location: [],
    });
  }

  toggleComments(post: Post) {
    post.showComments = !post.showComments;
  }

  createPost() {}
}
