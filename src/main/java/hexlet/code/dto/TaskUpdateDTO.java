package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {
    private Integer index;
    private String title;
    private String content;

    @Setter(AccessLevel.NONE)
    @JsonProperty("assignee_id")
    @JsonAlias("assigneeId")
    private Long assigneeId;

    @JsonIgnore
    private boolean assigneeIdUpdated;

    private String status;
    private List<Long> taskLabelIds;

    @JsonProperty("assignee_id")
    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
        this.assigneeIdUpdated = true;
    }
}
