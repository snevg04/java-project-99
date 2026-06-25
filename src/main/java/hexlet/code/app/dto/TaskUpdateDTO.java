package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;
    private String name;
    private String description;
    private Long assigneeId;
    private Long taskStatusId;
    private List<Long> labelIds;
}
