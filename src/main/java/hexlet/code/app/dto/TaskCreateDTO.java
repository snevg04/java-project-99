package hexlet.code.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
    private List<Long> labelIds;
}
