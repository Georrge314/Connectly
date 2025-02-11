package bg.connectly.controller;

import bg.connectly.dto.CommentDto;
import bg.connectly.dto.PostDto;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.service.AuthService;
import bg.connectly.service.CommentService;
import bg.connectly.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class for managing post-related operations.
 * This class provides endpoints for creating, updating, deleting, and retrieving posts and comments.
 */
@RestController
@RequestMapping("/api/post")
@Tag(name = "Post", description = "Endpoints for managing posts and comments")
public class PostController {
    private final PostService postService;
    private final CommentService commentService;
    private final AuthService authService;

    @Autowired
    public PostController(PostService postService,
                          CommentService commentService,
                          AuthService authService) {
        this.postService = postService;
        this.commentService = commentService;
        this.authService = authService;
    }

    /**
     * Endpoint for retrieving posts by email.
     *
     * @param email the email of the author
     * @param pageable the pagination information
     * @return a page of posts by the given email
     */
    @Operation(summary = "Get posts by email")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully"),
    })
    @GetMapping("/user")
    public ResponseEntity<Page<Post>> getUserPosts(@RequestParam String email, Pageable pageable) {
        Page<Post> posts = postService.getPostsByEmail(email, pageable);
        return ResponseEntity.ok(posts);
    }


    /**
     * Endpoint for retrieving all posts.
     *
     * @param pageable the pagination information
     * @return a page of all posts
     */
    @Operation(summary = "Get all posts")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Posts retrieved successfully")
    })
    @GetMapping("/get")
    public ResponseEntity<Page<Post>> getPosts(Pageable pageable) {
        Page<Post> posts = postService.getPosts(pageable);
        return ResponseEntity.ok(posts);
    }


    /**
     * Endpoint for creating a new post.
     *
     * @param postDto the data transfer object containing post details
     * @param token the authorization token
     * @return the created post
     */
    @Operation(summary = "Create a new post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post created successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/add")
    public ResponseEntity<Post> createPost(@Valid @RequestBody PostDto postDto,
                                           @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        Post post = postService.createPost(postDto, email);
        return ResponseEntity.ok(post);
    }


    /**
     * Endpoint for deleting a post.
     *
     * @param id the ID of the post to delete
     * @param token the authorization token
     * @return a response entity with no content
     */
    @Operation(summary = "Delete a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Post or User not found")
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        String email = authService.getEmailFromToken(token);
        postService.deletePost(id, email);
        return ResponseEntity.ok().build();
    }


    /**
     * Endpoint for updating a post.
     *
     * @param id the ID of the post to update
     * @param token the authorization token
     * @param updatePostDto the data transfer object containing updated post details
     * @return the updated post
     */
    @Operation(summary = "Update a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Post updated successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Post or User not found")
    })
    @PutMapping("/update/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id,
                                           @RequestHeader("Authorization") String token,
                                           @Valid @RequestBody PostDto updatePostDto) {
        String email = authService.getEmailFromToken(token);
        Post updatedPost = postService.updatePost(id, email, updatePostDto);
        return ResponseEntity.ok(updatedPost);
    }


    /**
     * Endpoint for adding a comment to a post.
     *
     * @param postId the ID of the post to comment on
     * @param token the authorization token
     * @param commentDto the data transfer object containing comment details
     * @return the created comment
     */
    @Operation(summary = "Add a comment to a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Post or User not found")
    })
    @PostMapping("/{postId}/comment")
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestHeader("Authorization") String token,
                                              @Valid @RequestBody CommentDto commentDto) {
        String email = authService.getEmailFromToken(token);
        Comment comment = commentService.createComment(postId, email, commentDto);
        return ResponseEntity.ok(comment);
    }


    /**
     * Endpoint for retrieving comments for a post.
     *
     * @param postId the ID of the post
     * @return a list of comments for the given post
     */
    @Operation(summary = "Get comments for a post")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comments retrieved successfully"),
    })
    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getComments(postId);
        return ResponseEntity.ok(comments);
    }


    /**
     * Endpoint for liking a comment.
     *
     * @param commentId the ID of the comment to like
     * @return the liked comment
     */
    @Operation(summary = "Like a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Comment liked successfully"),
            @ApiResponse(responseCode = "404", description = "Comment not found")
    })
    @PostMapping("comment/{commentId}/like")
    public ResponseEntity<Comment> likeComment(@PathVariable Long commentId) {
        Comment comment = commentService.likeComment(commentId);
        return ResponseEntity.ok(comment);
    }

    /**
     * Endpoint for replying to a comment.
     *
     * @param commentId the ID of the comment to reply to
     * @param token the authorization token
     * @param commentDto the data transfer object containing reply details
     * @return the created reply comment
     */
    @Operation(summary = "Reply to a comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reply added successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "404", description = "Comment or User not found")
    })
    @PostMapping("/comment/{commentId}/reply")
    public ResponseEntity<Comment> replyToComment(@PathVariable Long commentId,
                                                  @RequestHeader("Authorization") String token,
                                                  @Valid @RequestBody CommentDto commentDto) {
        String email = authService.getEmailFromToken(token);
        Comment comment = commentService.replyToComment(commentId, email, commentDto);
        return ResponseEntity.ok(comment);
    }
}
