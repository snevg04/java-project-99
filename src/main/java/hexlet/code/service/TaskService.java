package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification specBuilder;

    public List<TaskDTO> getAllTasks(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var tasks = taskRepository.findAll(spec);
        return tasks.stream().map(taskMapper::toDTO).toList();
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
