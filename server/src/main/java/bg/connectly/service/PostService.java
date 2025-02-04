package bg.connectly.service;

import bg.connectly.dto.CreatePostDto;
import bg.connectly.exception.UserNotFoundException;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import bg.connectly.repository.CommentRepository;
import bg.connectly.repository.PostRepository;
import bg.connectly.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;

    public Page<Post> getPostsByUsername(String username, Pageable pageable) {
        return postRepository.findByAuthorUsername(username, pageable);
    }

    public Post createPost(CreatePostDto createPostDto, String username) {
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));

        return null;
    }

    public void deletePost(Long id, String username) {
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new UserNotFoundException("Post not found");
        }

        postRepository.delete(existingPost);
    }

    public Post updatePost(Long id, String username, @Valid CreatePostDto updatePostDto) {
        User existingUser = userRepository
                .findByUsername(username).orElseThrow(() -> new UserNotFoundException("Username not found"));

        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Post not found"));

        if (!existingPost.getAuthor().equals(existingUser)) {
            throw new UserNotFoundException("Post not found");
        }

        existingPost.setContent(updatePostDto.getContent());
        existingPost.setMediaUrls(updatePostDto.getMediaUrls());
        existingPost.setTags(updatePostDto.getTags());
        existingPost.setVisibility(updatePostDto.getVisibility());
        existingPost.setLocation(updatePostDto.getLocation());
        existingPost.setPostType(updatePostDto.getPostType());
        existingPost.setUpdatedAt(LocalDateTime.now());

        return postRepository.save(existingPost);
    }

    public List<Comment> getComments(Long postId) {
        return commentRepository.findByPostId(postId);
    }
}
