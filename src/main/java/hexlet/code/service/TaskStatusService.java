package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;

import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO);
    List<TaskStatusDTO> getAll();
    TaskStatusDTO updateTaskStatus(TaskStatusUpdateDTO taskStatusUpdateDTO, Long id);
    TaskStatusDTO getById(Long id);
    TaskStatusDTO deleteTaskStatus(Long id);
}
