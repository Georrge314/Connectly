package bg.connectly.mapper;

import bg.connectly.dto.CreateCommentDto;
import bg.connectly.model.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    CommentMapper INSTANCE = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "likesCount", expression = "java(0)")
    @Mapping(target = "replies", ignore = true)
    @Mapping(target = "parentComment", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "post", ignore = true)
    @Mapping(target = "content", source = "createCommentDto.content")
    Comment toComment(CreateCommentDto createCommentDto);
}
