package hexlet.code.mapper;


import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusMapper {
    public TaskStatusDTO toDTO(TaskStatus taskStatus) {
        var taskStatusDto = new TaskStatusDTO();
        taskStatusDto.setId(taskStatus.getId());
        taskStatusDto.setName(taskStatus.getName());
        taskStatusDto.setSlug(taskStatus.getSlug());
        taskStatusDto.setCreatedAt(taskStatus.getCreatedAt());
        return taskStatusDto;
    }

    public TaskStatus toEntity(TaskStatusCreateDTO taskStatusCreateDTO) {
        var taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusCreateDTO.getName());
        taskStatus.setSlug(taskStatusCreateDTO.getSlug());
        return taskStatus;
    }

    public void updateEntity(TaskStatusUpdateDTO taskStatusUpdateDTO, TaskStatus taskStatus) {

        if (taskStatusUpdateDTO.getName() != null) {
            taskStatus.setName(taskStatusUpdateDTO.getName());
        }

        if (taskStatusUpdateDTO.getSlug() != null) {
            taskStatus.setSlug(taskStatusUpdateDTO.getSlug());
        }
    }
}
