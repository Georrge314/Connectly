package bg.connectly.service;

import bg.connectly.dto.CreateCommentDto;
import bg.connectly.model.Comment;
import jakarta.validation.Valid;

import java.util.List;

public interface CommentService {
    Comment createComment(Long postId, String username, @Valid CreateCommentDto createCommentDto);

    Comment likeComment(Long commentId);

    Comment replyToComment(Long commentId, String username, @Valid CreateCommentDto createCommentDto);

    List<Comment> getComments(Long postId);
}
