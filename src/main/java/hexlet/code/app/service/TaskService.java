package hexlet.code.app.service;

import hexlet.code.app.dto.TaskCreateDTO;
import hexlet.code.app.dto.TaskDTO;
import hexlet.code.app.dto.TaskUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskMapper;
import hexlet.code.app.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    public List<TaskDTO> getAllTasks() {
        var tasks = taskRepository.findAll();
        return tasks.stream()
                .map(taskMapper::toDTO)
                .toList();
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        var task = taskMapper.toEntity(taskCreateDTO);
        var savedTask = taskRepository.save(task);
        return taskMapper.toDTO(savedTask);
    }

    public TaskDTO getTaskById(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
        return taskMapper.toDTO(task);
    }

    public TaskDTO updateTask(TaskUpdateDTO taskUpdateDTO, Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
        taskMapper.updateEntity(taskUpdateDTO, task);
        var updatedTask = taskRepository.save(task);
        return taskMapper.toDTO(updatedTask);

    }

    public void deleteTask(Long id) {
        var task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found!"));
        taskRepository.delete(task);
    }
}
