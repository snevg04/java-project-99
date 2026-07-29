package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("assignee_id")
    @JsonAlias("assigneeId")
    private Long assigneeId;
    private String status;
    private List<Long> taskLabelIds = new ArrayList<>();
}
