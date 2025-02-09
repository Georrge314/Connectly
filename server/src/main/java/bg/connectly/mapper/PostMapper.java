package bg.connectly.mapper;

import bg.connectly.dto.PostDto;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper class for converting between PostDto and Post entities.
 */
@Component
public class PostMapper {

    /**
     * Converts a PostDto to a Post entity.
     *
     * @param postDto      the data transfer object containing post details
     * @param existingUser the user who is the author of the post
     * @return the created Post entity
     */
    public Post toPost(PostDto postDto, User existingUser) {
        Post post = new Post();
        post.setContent(postDto.getContent());
        post.setMediaUrls(postDto.getMediaUrls());
        post.setTags(postDto.getTags());
        post.setVisibility(postDto.getVisibility());
        post.setLocation(postDto.getLocation());
        post.setPostType(postDto.getPostType());
        post.setAuthor(existingUser);

        //default values
        post.setCreatedAt(LocalDateTime.now());
        post.setUpdatedAt(LocalDateTime.now());
        return post;
    }

    /**
     * Updates an existing Post entity from a PostDto.
     *
     * @param postDto      the data transfer object containing updated post details
     * @param post the existing Post entity to be updated
     */
    public boolean updatePostFromDto(PostDto postDto, Post post) {
        boolean isUpdated = false;

        //Update the existing post fields if they are different from the post dto

        if (postDto.getContent() != null && !postDto.getContent().equals(post.getContent())) {
            post.setContent(postDto.getContent());
            isUpdated = true;
        }
        if (postDto.getMediaUrls() != null && !postDto.getMediaUrls().equals(post.getMediaUrls())) {
            post.setMediaUrls(postDto.getMediaUrls());
            isUpdated = true;
        }
        if (postDto.getTags() != null && !postDto.getTags().equals(post.getTags())) {
            post.setTags(postDto.getTags());
            isUpdated = true;
        }
        if (postDto.getVisibility() != null && !postDto.getVisibility().equals(post.getVisibility())) {
            post.setVisibility(postDto.getVisibility());
            isUpdated = true;
        }
        if (postDto.getLocation() != null && !postDto.getLocation().equals(post.getLocation())) {
            post.setLocation(postDto.getLocation());
            isUpdated = true;
        }
        if (postDto.getPostType() != null && !postDto.getPostType().equals(post.getPostType())) {
            post.setPostType(postDto.getPostType());
            isUpdated = true;
        }

        if (isUpdated) {
            post.setUpdatedAt(LocalDateTime.now());
        }

        return isUpdated;
    }
}
