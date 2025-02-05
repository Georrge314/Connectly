package bg.connectly.service.impl;

import bg.connectly.dto.CreatePostDto;
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

import java.time.LocalDateTime;

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
     * Fetches posts by the given username.
     *
     * @param username the username of the author
     * @param pageable the pagination information
     * @return a page of posts by the given username
     */
    @Override
    public Page<Post> getPostsByUsername(String username, Pageable pageable) {
        logger.info("Fetching posts for username: {}", username);
        return postRepository.findByAuthorUsername(username, pageable);
    }

    /**
     * Creates a new post for the given username.
     *
     * @param createPostDto the data transfer object containing post details
     * @param username      the username of the author
     * @return the created post
     */
    @Override
    public Post createPost(@Valid CreatePostDto createPostDto, String username) {
        logger.info("Creating post for username: {}", username);
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        Post post = postMapper.toPost(createPostDto, existingUser);
        return postRepository.save(post);
    }

    /**
     * Deletes a post by its ID and the username of the author.
     *
     * @param id       the ID of the post to delete
     * @param username the username of the author
     */
    @Override
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

    /**
     * Updates a post by its ID and the username of the author.
     *
     * @param id            the ID of the post to update
     * @param username      the username of the author
     * @param updatePostDto the data transfer object containing updated post details
     * @return the updated post
     */
    @Override
    @Transactional
    public Post updatePost(Long id, String username, @Valid CreatePostDto updatePostDto) {
        logger.info("Updating post with id: {} for username: {}", id, username);
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new NotFoundException("Username " + username + " not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new NotFoundException("Post not found");
        }

        postMapper.updatePostFromDto(updatePostDto, existingPost);
        existingPost.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(existingPost);
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
