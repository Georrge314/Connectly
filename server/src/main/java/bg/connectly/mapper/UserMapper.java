package bg.connectly.mapper;

import bg.connectly.dto.RegisterRequest;
import bg.connectly.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastLogin", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "roles", expression = "java(new java.util.HashSet<>(java.util.Collections.singletonList(\"USER\")))")
    @Mapping(target = "followersCount", expression = "java(0)")
    @Mapping(target = "followingCount", expression = "java(0)")
    @Mapping(target = "isActive", expression = "java(true)")
    @Mapping(target = "isVerified", expression = "java(false)")
    @Mapping(target = "isPublic", expression = "java(true)")
    @Mapping(target = "profilePicture", source = "registerRequest.profilePicture")
    @Mapping(target = "bio", source = "registerRequest.bio")
    @Mapping(target = "dateOfBirth", source = "registerRequest.dateOfBirth")
    @Mapping(target = "firstName", source = "registerRequest.firstName")
    @Mapping(target = "lastName", source = "registerRequest.lastName")
    @Mapping(target = "username", source = "registerRequest.username")
    @Mapping(target = "email", source = "registerRequest.email")
    User toUser(RegisterRequest registerRequest);
}
