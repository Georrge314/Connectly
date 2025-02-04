package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
import bg.connectly.dto.CreateCommentDto;
import bg.connectly.dto.CreatePostDto;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.service.PostService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/post")
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/user")
    public ResponseEntity<Page<Post>> getUserPosts(@RequestParam String username, Pageable pageable) {
        Page<Post> posts = postService.getPostsByUsername(username, pageable);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/get")
    public ResponseEntity<Page<Post>> getPosts(Pageable pageable) {
        Page<Post> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }

    @PostMapping("/add")
    public ResponseEntity<Post> createPost(@Valid @RequestBody CreatePostDto createPostDto,
                                           @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Post post = postService.createPost(createPostDto, username);
        return ResponseEntity.ok(post);
    }

    @PostMapping("/{postId}/comment")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                                 @RequestHeader("Authorization") String token,
                                                 @Valid @RequestBody CreateCommentDto createCommentDto) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Comment comment = postService.createComment(postId, username, createCommentDto);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("comment/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable Long commentId) {
        Comment comment = postService.likeComment(commentId);
        return ResponseEntity.ok(comment);
    }

    @PostMapping("/comment/{commentId}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable Long commentId,
                                                 @RequestHeader("Authorization") String token,
                                                 @Valid @RequestBody CreateCommentDto createCommentDto) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Comment comment = postService.replyToComment(commentId, username, createCommentDto);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        postService.deletePost(id, username);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token,
                                           @Valid @RequestBody CreatePostDto updatePostDto) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Post updatedPost = postService.updatePost(id, username, updatePostDto);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = postService.getComments(postId);
        return ResponseEntity.ok(comments);
    }
}
