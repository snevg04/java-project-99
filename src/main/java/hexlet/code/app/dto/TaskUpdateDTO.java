package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;
    private String name;
    private String description;
    private Long assigneeId;
    private Long taskStatusId;
}
