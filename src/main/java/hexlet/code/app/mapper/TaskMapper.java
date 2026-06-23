package hexlet.code.app.mapper;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.model.Task;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    public TaskDTO toDTO(Task task) {
        var taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setIndex(task.getIndex());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setName(task.getName());
        taskDto.setDescription(task.getDescription());
        taskDto.setAssigneeId(task.getAssignee().getId());
        taskDto.setTaskStatusId(task.getTaskStatus().getId());
        return taskDto;
    }

    public Task toEntity(TaskCreateDTO taskCreateDTO) {
        var task = new Task();
        task.setIndex(taskCreateDTO.getIndex());
        task.setName(taskCreateDTO.getName());
        task.setDescription(taskCreateDTO.getDescription());
        task.setAssignee(userRepository.findById(taskCreateDTO.getAssigneeId())
                .orElseThrow(() -> new ResourceNotFoundException("Assignee not found!")));
        task.setTaskStatus(taskStatusRepository.findById(taskCreateDTO.getTaskStatusId())
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!")));
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

        if (taskUpdateDTO.getAssigneeId() != null) {
            task.setAssignee(userRepository.findById(taskUpdateDTO.getAssigneeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found!")));
        }

        if (taskUpdateDTO.getTaskStatusId() != null) {
            task.setTaskStatus(taskStatusRepository.findById(taskUpdateDTO.getTaskStatusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found!")));
        }
    }
}
