package bg.connectly.service.impl;

import bg.connectly.dto.PostDto;
import bg.connectly.exception.NotFoundException;
import bg.connectly.mapper.PostMapper;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import bg.connectly.service.PostService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service class for managing post-related operations.
 * This class provides methods for creating, updating, deleting, and fetching posts and comments.
 * It interacts with the PostRepository, UserRepository, and CommentRepository to perform database operations.
 */
@Service
public class PostServiceImpl implements PostService {
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);

    private final PostMapper postMapper;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    @Autowired
    public PostServiceImpl(PostMapper postMapper,
                           PostRepository postRepository,
                           UserRepository userRepository) {
        this.postMapper = postMapper;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    /**
     * Fetches posts by the given email.
     *
     * @param email the email of the author
     * @param pageable the pagination information
     * @return a page of posts by the given email
     */
    @Override
    public Page<Post> getPostsByEmail(String email, Pageable pageable) {
        logger.info("Fetching posts for email: {}", email);
        return postRepository.findByAuthorEmail(email, pageable);
    }

    /**
     * Creates a new post for the given email.
     *
     * @param postDto the data transfer object containing post details
     * @param email      the email of the author
     * @return the created post
     */
    @Override
    public Post createPost(@Valid PostDto postDto, String email) {
        logger.info("Creating post for email: {}", email);
        User existingUser = userRepository
                .findByEmail(email).orElseThrow(() -> new NotFoundException("Email " + email + " not found"));

        Post post = postMapper.toPost(postDto, existingUser);
        return postRepository.save(post);
    }

    /**
     * Deletes a post by its ID and the email of the author.
     *
     * @param id       the ID of the post to delete
     * @param email the email of the author
     */
    @Override
    @Transactional
    public void deletePost(Long id, String email) {
        logger.info("Deleting post with id: {} for email: {}", id, email);

        User existingUser = userRepository
                .findByEmail(email).orElseThrow(() -> new NotFoundException("Email " + email + " not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new NotFoundException("Post not found");
        }

        postRepository.delete(existingPost);
    }

    /**
     * Updates a post by its ID and the email of the author.
     *
     * @param id            the ID of the post to update
     * @param email      the email of the author
     * @param updatePostDto the data transfer object containing updated post details
     * @return the updated post
     */
    @Override
    @Transactional
    public Post updatePost(Long id, String email, @Valid PostDto updatePostDto) {
        logger.info("Updating post with id: {} for email: {}", id, email);
        User existingUser = userRepository
                .findByEmail(email).orElseThrow(() -> new NotFoundException("Email " + email + " not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new NotFoundException("Post not found");
        }

        boolean isUpdated = postMapper.updatePostFromDto(updatePostDto, existingPost);
        if (isUpdated) {
            logger.info("Post updated successfully: {}", id);
            return postRepository.save(existingPost);
        }
        return existingPost;
    }

    /**
     * Fetches all posts with pagination.
     *
     * @param pageable the pagination information
     * @return a page of all posts
     */
    @Override
    public Page<Post> getPosts(Pageable pageable) {
        logger.info("Fetching all posts");
        return postRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

}
