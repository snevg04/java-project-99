package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskCreateDTO {
    private Integer index;

    @NotBlank
    private String title;
    private String content;
    private Long assignee_id;

    @NotBlank
    private String status;
    private List<Long> taskLabelIds;
}
