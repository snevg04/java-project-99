package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskCreateDTO {
    private Integer index;

    @NotBlank
    private String name;
    private String description;
    private Long assigneeId;
    @NotNull
    private Long taskStatusId;
}
