package bg.connectly.service;

import bg.connectly.dto.PostDto;
import bg.connectly.model.Post;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostService {
    Page<Post> getPostsByEmail(String email, Pageable pageable);

    Page<Post> getPosts(Pageable pageable);

    Post createPost(@Valid PostDto postDto, String email);

    void deletePost(Long id, String email);

    Post updatePost(Long id, String email, @Valid PostDto updatePostDto);
}
