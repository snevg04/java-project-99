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
    @Setter(AccessLevel.NONE)
    private Integer index;

    @JsonIgnore
    private boolean indexUpdated;

    private String title;

    @Setter(AccessLevel.NONE)
    private String content;

    @JsonIgnore
    private boolean contentUpdated;

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

    public void setIndex(Integer index) {
        this.index = index;
        this.indexUpdated = true;
    }

    public void setContent(String content) {
        this.content = content;
        this.contentUpdated = true;
    }
}
