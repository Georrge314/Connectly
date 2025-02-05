package bg.connectly.service;

import bg.connectly.dto.CreatePostDto;
import bg.connectly.model.Post;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> getPostsByUsername(String username, Pageable pageable);

    Page<Post> getPosts(Pageable pageable);

    Post createPost(@Valid CreatePostDto createPostDto, String username);

    void deletePost(Long id, String username);

    Post updatePost(Long id, String username, @Valid CreatePostDto updatePostDto);
}
