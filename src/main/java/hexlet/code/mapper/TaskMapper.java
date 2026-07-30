package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "labels", target = "taskLabelIds")
    TaskDTO toDTO(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "labels", ignore = true)
    Task toEntity(TaskCreateDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "index", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "taskStatus", ignore = true)
    @Mapping(target = "labels", ignore = true)
    void updateEntity(TaskUpdateDTO dto, @MappingTarget Task task);

    default List<Long> mapLabels(Set<Label> labels) {
        return labels != null
                ? labels.stream().map(Label::getId).toList()
                : List.of();
    }
}
