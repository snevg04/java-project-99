package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;
    private String title;
    private String content;
    // CHECKSTYLE:OFF
    private Long assignee_id;
    // CHECKSTYLE:ON
    private String status;
    private List<Long> taskLabelIds;
}
