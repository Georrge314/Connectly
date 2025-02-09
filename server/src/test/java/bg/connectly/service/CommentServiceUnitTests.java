package bg.connectly.service;

import bg.connectly.dto.CommentDto;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.CommentMapper;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.CommentRepository;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceUnitTests {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private CommentServiceImpl commentService;

    private User user;
    private Post post;
    private Comment comment;
    private CommentDto commentDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("testuser");

        post = new Post();
        post.setId(1L);

        comment = new Comment();
        comment.setId(1L);
        comment.setAuthor(user);
        comment.setPost(post);

        commentDto = new CommentDto();
        commentDto.setContent("Test content");
    }

    @Test
    void getCommentsSuccess() {
        when(commentRepository.findByPostId(anyLong())).thenReturn(Collections.singletonList(comment));

        List<Comment> result = commentService.getComments(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(commentRepository).findByPostId(anyLong());
    }

    @Test
    void createCommentSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(post));
        when(commentMapper.toComment(eq(commentDto), eq(user), eq(post), isNull())).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.createComment(1L, "testuser", commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void createCommentUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.createComment(1L, "testuser", commentDto));
    }

    @Test
    void createCommentPostNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.createComment(1L, "testuser", commentDto));
    }

    @Test
    void likeCommentSuccess() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Comment result = commentService.likeComment(1L);

        assertNotNull(result);
        assertEquals(1, result.getLikesCount());
        verify(commentRepository).save(any(Comment.class));
    }

    @Test
    void likeCommentNotFound() {
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.likeComment(1L));
    }

    @Test
    void replyToCommentSuccess() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));
        when(commentMapper.toComment(any(CommentDto.class), any(User.class), any(Post.class), any(Comment.class))).thenReturn(comment);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment, comment);

        Comment result = commentService.replyToComment(1L, "testuser", commentDto);

        assertNotNull(result);
        assertEquals(comment.getId(), result.getId());
    }

    @Test
    void replyToCommentUserNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.replyToComment(1L, "testuser", commentDto));
    }

    @Test
    void replyToCommentNotFound() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(commentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> commentService.replyToComment(1L, "testuser", commentDto));
    }
}