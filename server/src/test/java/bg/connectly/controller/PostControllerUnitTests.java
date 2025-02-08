package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.CreateCommentDto;
import bg.connectly.dto.CreatePostDto;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.service.AuthService;
import bg.connectly.service.CommentService;
import bg.connectly.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PostControllerUnitTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @TestConfiguration
    static class TestSecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
                    .authorizeHttpRequests(auth -> auth
                            .anyRequest().permitAll()// unrestricted access to all endpoints
                    )
                    .sessionManagement(session -> session
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Stateless session for JWT
                    );

            return http.build();
        }
    }

    @Test
    @Order(1)
    void getUserPostsReturnsPosts() throws Exception {
        Post post = new Post();
        post.setContent("Test Post");
        Page<Post> postsPage = new PageImpl<>(Collections.singletonList(post));
        Pageable pageable = PageRequest.of(0, 10);

        when(postService.getPostsByUsername(anyString(), any(Pageable.class))).thenReturn(postsPage);

        mockMvc.perform(get("/api/post/user")
                        .param("username", "some user")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].content").value("Test Post"));
    }

    @Test
    @Order(2)
    void createPostReturnsCreatedPost() throws Exception {
        CreatePostDto createPostDto = new CreatePostDto();
        createPostDto.setContent("New Post");
        Post post = new Post();
        post.setContent("New Post");

        when(authService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(postService.createPost(any(CreatePostDto.class), anyString())).thenReturn(post);

        mockMvc.perform(post("/api/post/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(createPostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New Post"));
    }


    @Test
    @Order(3)
    void deletePostReturnsOk() throws Exception {
        when(authService.getUsernameFromToken(anyString())).thenReturn("testuser");

        mockMvc.perform(delete("/api/post/delete/{id}", 1L)
                        .header("Authorization", "Bearer valid-token"))
                .andExpect(status().isOk());
    }

    @Test
    @Order(4)
    void updatePostReturnsUpdatedPost() throws Exception {
        CreatePostDto updatePostDto = new CreatePostDto();
        updatePostDto.setContent("Updated Post");
        Post updatedPost = new Post();
        updatedPost.setContent("Updated Post");

        when(authService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(postService.updatePost(anyLong(), anyString(), any(CreatePostDto.class))).thenReturn(updatedPost);

        mockMvc.perform(put("/api/post/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(updatePostDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Updated Post"));
    }

    @Test
    @Order(5)
    void addCommentReturnsCreatedComment() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setContent("New Comment");
        Comment comment = new Comment();
        comment.setContent("New Comment");

        when(authService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(commentService.createComment(anyLong(), anyString(), any(CreateCommentDto.class))).thenReturn(comment);

        mockMvc.perform(post("/api/post/{postId}/comment", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("New Comment"));
    }

    @Test
    @Order(6)
    void getCommentsReturnsComments() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Test Comment");
        List<Comment> comments = Collections.singletonList(comment);

        when(commentService.getComments(anyLong())).thenReturn(comments);

        mockMvc.perform(get("/api/post/{postId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Test Comment"));
    }

    @Test
    @Order(7)
    void likeCommentReturnsLikedComment() throws Exception {
        Comment comment = new Comment();
        comment.setContent("Liked Comment");

        when(commentService.likeComment(anyLong())).thenReturn(comment);

        mockMvc.perform(post("/api/post/comment/{commentId}/like", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Liked Comment"));
    }

    @Test
    @Order(8)
    void replyToCommentReturnsCreatedReply() throws Exception {
        CreateCommentDto createCommentDto = new CreateCommentDto();
        createCommentDto.setContent("Reply Comment");
        Comment comment = new Comment();
        comment.setContent("Reply Comment");

        when(authService.getUsernameFromToken(anyString())).thenReturn("testuser");
        when(commentService.replyToComment(anyLong(), anyString(), any(CreateCommentDto.class))).thenReturn(comment);

        mockMvc.perform(post("/api/post/comment/{commentId}/reply", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer valid-token")
                        .content(objectMapper.writeValueAsString(createCommentDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("Reply Comment"));
    }

}
