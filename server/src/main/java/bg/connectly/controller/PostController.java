package bg.connectly.controller;

import bg.connectly.configuration.JwtUtil;
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

    @PostMapping("/add")
    public ResponseEntity<Post> createPost(@Valid @RequestBody CreatePostDto createPostDto,
                                           @RequestHeader("Authorization") String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        Post post = postService.createPost(createPostDto, username);
        return ResponseEntity.ok(post);
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

    @GetMapping("/{postid}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = postService.getComments(postId);
        return ResponseEntity.ok(comments);
    }
}
