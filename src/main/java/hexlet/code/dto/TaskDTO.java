package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class TaskDTO {
    private Long id;
    private Integer index;
    private LocalDate createdAt;
    private String title;
    private String content;
    // CHECKSTYLE:OFF
    private Long assignee_id;
    // CHECKSTYLE:ON
    private String status;
    private List<Long> taskLabelIds = new ArrayList<>();
}
