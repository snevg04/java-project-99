package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.List;

@Setter
@Getter
public class TaskUpdateDTO {
    private JsonNullable<Integer> index;

    private String title;

    private JsonNullable<String> content;

    @JsonProperty("assignee_id")
    @JsonAlias("assigneeId")
    private JsonNullable<Long> assigneeId;

    private String status;
    private List<Long> taskLabelIds;
}
