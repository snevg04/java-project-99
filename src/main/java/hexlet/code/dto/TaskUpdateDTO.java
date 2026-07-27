package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;
    private String title;
    private String content;
    private Long assigneeId;
    private String status;
    private List<Long> taskLabelIds;
}
