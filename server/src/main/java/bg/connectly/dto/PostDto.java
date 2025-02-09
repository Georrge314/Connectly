package bg.connectly.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class PostDto {
    @NotBlank(message = "Content cannot be empty")
    @Size(max = 1000, message = "Content cannot exceed 1000 characters")
    private String content;

    private List<String> mediaUrls;
    private Set<String> tags;

    private String visibility;

    private String location;

    private String postType;
}
