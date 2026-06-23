package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {
    public TaskDTO toDTO(Task task) {
        var taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setIndex(task.getIndex());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setAssignee(task.getAssignee());
        taskDto.setTaskStatus(task.getTaskStatus());
        return taskDto;
    }

    public Task toEntity(TaskCreateDTO taskCreateDTO) {
        var task = new Task();
        task.setIndex(taskCreateDTO.getIndex());
        task.setName(taskCreateDTO.getName());
        task.setDescription(taskCreateDTO.getDescription());
        task.setAssignee(taskCreateDTO.getAssignee());
        task.setTaskStatus(taskCreateDTO.getTaskStatus());
        return task;
    }

    public void updateEntity(TaskUpdateDTO taskUpdateDTO, Task task) {


        if (taskUpdateDTO.getIndex() != null) {
            task.setIndex(taskUpdateDTO.getIndex());
        }

        if (taskUpdateDTO.getName() != null) {
            task.setName(taskUpdateDTO.getName());
        }

        if (taskUpdateDTO.getDescription() != null) {
            task.setDescription(taskUpdateDTO.getDescription());
        }

        if (taskUpdateDTO.getAssignee() != null) {
            task.setAssignee(taskUpdateDTO.getAssignee());
        }

        if (taskUpdateDTO.getTaskStatus() != null) {
            task.setTaskStatus(taskUpdateDTO.getTaskStatus());
        }
    }
}