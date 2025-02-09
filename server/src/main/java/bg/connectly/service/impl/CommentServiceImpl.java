package bg.connectly.service.impl;

import bg.connectly.dto.CommentDto;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.CommentMapper;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.CommentRepository;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.CommentService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing comment-related operations.
 * This class provides methods for creating, updating, deleting, and fetching comments.
 * It interacts with the CommentRepository, UserRepository, and PostRepository to perform database operations.
 */
@Service
public class CommentServiceImpl implements CommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Autowired
    public CommentServiceImpl(CommentMapper commentMapper,
                              CommentRepository commentRepository,
                              UserRepository userRepository,
                              PostRepository postRepository) {
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    /**
     * Fetches comments for a given post ID.
     *
     * @param postId the ID of the post
     * @return a list of comments for the given post ID
     */
    @Override
    public List<Comment> getComments(Long postId) {
        logger.info("Fetching comments for post id: {}", postId);
        return commentRepository.findByPostId(postId);
    }

    /**
     * Creates a new comment for a given post ID and username.
     *
     * @param postId           the ID of the post
     * @param username         the username of the author
     * @param commentDto the data transfer object containing comment details
     * @return the created comment
     */
    @Override
    @Transactional
    public Comment createComment(Long postId, String username, @Valid CommentDto commentDto) {
        logger.info("Creating comment for post id: {} by username: {}", postId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username " + username + " not found"));
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Comment comment = commentMapper.toComment(commentDto, user, post, null);
        postRepository.save(post);

        return commentRepository.save(comment);
    }

    /**
     * Increments the like count of a comment by its ID.
     *
     * @param commentId the ID of the comment to like
     * @return the updated comment with incremented like count
     */
    @Override
    public Comment likeComment(Long commentId) {
        logger.info("Liking comment with id: {}", commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
        comment.setLikesCount(comment.getLikesCount() + 1);
        return commentRepository.save(comment);
    }

    /**
     * Creates a reply to a comment by its ID and the username of the author.
     *
     * @param commentId        the ID of the comment to reply to
     * @param username         the username of the author
     * @param commentDto the data transfer object containing reply details
     * @return the created reply comment
     */
    @Override
    @Transactional
    public Comment replyToComment(Long commentId, String username, @Valid CommentDto commentDto) {
        logger.info("Replying to comment with id: {} by username: {}", commentId, username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        Comment parentComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        Comment reply = commentMapper.toComment(commentDto, user, parentComment.getPost(), parentComment);

        if (parentComment.getReplies() == null) {
            parentComment.setReplies(List.of(reply));
        } else {
            parentComment.getReplies().add(reply);
        }
        commentRepository.save(parentComment);

        return commentRepository.save(reply);
    }
}
