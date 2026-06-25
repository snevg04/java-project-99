package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
public class TaskDTO {
    private Long id;
    private Integer index;
    private LocalDate createdAt;
    private String name;
    private String description;
    private Long assigneeId;
    private Long taskStatusId;
    private List<Long> labelIds;
}
