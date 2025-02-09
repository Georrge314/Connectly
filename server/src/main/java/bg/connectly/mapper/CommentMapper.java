package bg.connectly.mapper;

import bg.connectly.dto.CommentDto;
import bg.connectly.model.Comment;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Mapper class for converting between CommentDto and Comment entities.
 */
@Component
public class CommentMapper {

    /**
     * Converts a CommentDto to a Comment entity.
     *
     * @param commentDto    the data transfer object containing comment details
     * @param user          the user who is the author of the comment
     * @param post          the post to which the comment belongs
     * @param parentComment the parent comment if the comment is a reply, null otherwise
     * @return the created Comment entity
     */
    public Comment toComment(CommentDto commentDto, User user, Post post, Comment parentComment) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setAuthor(user);
        comment.setPost(post);

        if (parentComment != null) {
            comment.setParentComment(parentComment);
        }

        //default values
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());
        return comment;
    }
}
