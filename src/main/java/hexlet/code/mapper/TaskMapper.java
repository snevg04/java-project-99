package hexlet.code.mapper;

import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "assignee.id", target = "assignee_id")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "taskLabelIds")
    TaskDTO toDTO(Task task);

    default List<Long> mapLabels(List<Label> labels) {
        return labels != null
                ? labels.stream().map(Label::getId).toList()
                : List.of();
    }
}
