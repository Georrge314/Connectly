package bg.connectly.service;

import bg.connectly.dto.CreateCommentDto;
import bg.connectly.dto.CreatePostDto;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.CommentMapper;
import bg.connectly.mapper.PostMapper;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.CommentRepository;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostService.class);

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Autowired
    public PostService(PostMapper postMapper,
                       CommentMapper commentMapper,
                       PostRepository postRepository,
                       UserRepository userRepository,
                       CommentRepository commentRepository) {
        this.postMapper = postMapper;
        this.commentMapper = commentMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public Page<Post> getPostsByUsername(String username, Pageable pageable) {
        logger.info("Fetching posts for username: {}", username);
        return postRepository.findByAuthorUsername(username, pageable);
    }

    public Post createPost(@Valid CreatePostDto createPostDto, String username) {
        logger.info("Creating post for username: {}", username);
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new NotFoundException("Username "+ username + " not found"));

        Post post = postMapper.toPost(createPostDto, existingUser);

        return postRepository.save(post);
    }

    @Transactional
    public void deletePost(Long id, String username) {
        logger.info("Deleting post with id: {} for username: {}", id, username);

        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new NotFoundException("Post not found");
        }

        postRepository.delete(existingPost);
    }

    @Transactional
    public Post updatePost(Long id, String username, @Valid CreatePostDto updatePostDto) {
        logger.info("Updating post with id: {} for username: {}", id, username);
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new NotFoundException("Username "+ username + " not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new NotFoundException("Post not found");
        }

        postMapper.updatePostFromDto(updatePostDto, existingPost);
        existingPost.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(existingPost);
    }

    public List<Comment> getComments(Long postId) {
        logger.info("Fetching comments for post id: {}", postId);
        return commentRepository.findByPostId(postId);
    }

    public Page<Post> getPosts(Pageable pageable) {
        logger.info("Fetching all posts");
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    @Transactional
    public Comment createComment(Long postId, String username, @Valid CreateCommentDto createCommentDto) {
        logger.info("Creating comment for post id: {} by username: {}", postId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username "+ username + " not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Comment comment = commentMapper.toComment(createCommentDto);

        comment.setAuthor(user);
        comment.setPost(post);

        post.getComments().add(comment);
        postRepository.save(post);

        return commentRepository.save(comment);
    }

    public Comment likeComment(Long commentId) {
        logger.info("Liking comment with id: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setLikesCount(comment.getLikesCount() + 1);
        return commentRepository.save(comment);
    }


    @Transactional
    public Comment replyToComment(Long commentId, String username, @Valid CreateCommentDto createCommentDto) {
        logger.info("Replying to comment with id: {} by username: {}", commentId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username "+ username + " not found"));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        Comment reply = commentMapper.toComment(createCommentDto);
        reply.setAuthor(user);
        reply.setPost(parentComment.getPost());
        reply.setParentComment(parentComment);

        parentComment.getReplies().add(reply);
        commentRepository.save(parentComment);

        return commentRepository.save(reply);
    }
}
