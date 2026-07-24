package hexlet.code.app.dto;

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
    // CHECKSTYLE:OFF
    private Long assignee_id;
    // CHECKSTYLE:ON
    @NotBlank
    private String status;
    private List<Long> taskLabelIds;
}
