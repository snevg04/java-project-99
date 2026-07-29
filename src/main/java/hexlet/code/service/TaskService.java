package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;

import java.util.List;

public interface TaskService {
    List<TaskDTO> getAllTasks(TaskParamsDTO params);
    TaskDTO createTask(TaskCreateDTO taskCreateDTO);
    TaskDTO getTaskById(Long id);
    TaskDTO updateTask(TaskUpdateDTO taskUpdateDTO, Long id);
    TaskDTO deleteTask(Long id);
}
