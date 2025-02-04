package bg.connectly.mapper;

import bg.connectly.dto.CreatePostDto;
import bg.connectly.model.Post;
import bg.connectly.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "likesCount", expression = "java(0)")
    @Mapping(target = "commentsCount", expression = "java(0)")
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "tags", source = "createPostDto.tags")
    @Mapping(target = "visibility", source = "createPostDto.visibility")
    @Mapping(target = "location", source = "createPostDto.location")
    @Mapping(target = "postType", source = "createPostDto.postType")
    @Mapping(target = "content", source = "createPostDto.content")
    @Mapping(target = "mediaUrls", source = "createPostDto.mediaUrls")
    @Mapping(target = "author", source = "author")
    Post toPost(CreatePostDto createPostDto, User author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likesCount", ignore = true)
    @Mapping(target = "commentsCount", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "author", ignore = true)
    void updatePostFromDto(CreatePostDto dto, @MappingTarget Post post);
}

