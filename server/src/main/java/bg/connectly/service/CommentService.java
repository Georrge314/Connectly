package bg.connectly.service;

import bg.connectly.dto.CommentDto;
import bg.connectly.model.Comment;
import jakarta.validation.Valid;

import java.util.List;

public interface CommentService {
    Comment createComment(Long postId, String email, @Valid CommentDto commentDto);

    Comment likeComment(Long commentId);

    Comment replyToComment(Long commentId, String email, @Valid CommentDto commentDto);

    List<Comment> getComments(Long postId);
}
