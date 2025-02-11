package bg.connectly.service;

import bg.connectly.dto.PostDto;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.PostMapper;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceUnitTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostMapper postMapper;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private PostDto postDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setEmail("testuser@abv.bg");

        post = new Post();
        post.setId(1L);
        post.setAuthor(user);

        postDto = new PostDto();
        postDto.setContent("Test content");
    }

    @Test
    void getPostsByEmailSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findByAuthorEmail(anyString(), any(Pageable.class))).thenReturn(postPage);

        Page<Post> result = postService.getPostsByEmail("testuser@abv.bg", pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository).findByAuthorEmail(anyString(), any(Pageable.class));
    }

    @Test
    void createPostSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postMapper.toPost(any(PostDto.class), any(User.class))).thenReturn(post);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.createPost(postDto, "testuser@abv.bg");

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void createPostUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.createPost(postDto, "testuser@abv.bg"));
    }

    @Test
    void deletePostSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));

        postService.deletePost(1L, "testuser@abv.bg");

        verify(postRepository).delete(any(Post.class));
    }

    @Test
    void deletePostUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.deletePost(1L, "testuser@abv.bg"));
    }

    @Test
    void deletePostNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.deletePost(1L, "testuser@abv.bg"));
    }

    @Test
    void updatePostSuccess() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(postMapper.updatePostFromDto(any(PostDto.class), any(Post.class))).thenReturn(true);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        Post result = postService.updatePost(1L, "testuser@abv.bg", postDto);

        assertNotNull(result);
        assertEquals(post.getId(), result.getId());
        verify(postRepository).save(any(Post.class));
    }

    @Test
    void updatePostUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.updatePost(1L, "testuser@abv.bg", postDto));
    }

    @Test
    void updatePostNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> postService.updatePost(1L, "testuser@abv.bg", postDto));
    }

    @Test
    void getPostsSuccess() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Post> postPage = new PageImpl<>(Collections.singletonList(post));
        when(postRepository.findAllByOrderByCreatedAtDesc(any(Pageable.class))).thenReturn(postPage);

        Page<Post> result = postService.getPosts(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(postRepository).findAllByOrderByCreatedAtDesc(any(Pageable.class));
    }
}