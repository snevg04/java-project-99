package hexlet.code.app.service;

import hexlet.code.app.dto.TaskStatusCreateDTO;
import hexlet.code.app.dto.TaskStatusDTO;
import hexlet.code.app.dto.TaskStatusUpdateDTO;
import hexlet.code.app.exception.ConflictException;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.TaskStatusMapper;
import hexlet.code.app.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    TaskStatusRepository taskStatusRepository;

    @Autowired
    TaskStatusMapper taskStatusMapper;

    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        if (
                taskStatusRepository.existsBySlug(taskStatusCreateDTO.getSlug())
                        || taskStatusRepository.existsByName(taskStatusCreateDTO.getName())
        ) {
            throw new ConflictException("TaskStatus with given slug or name already exists!");
        }
        var taskStatus = taskStatusMapper.toEntity(taskStatusCreateDTO);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(savedTaskStatus);
    }

    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::toDTO)
                .toList();
    }

    public TaskStatusDTO getById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!"));
        return taskStatusMapper.toDTO(taskStatus);
    }

    public TaskStatusDTO updateTaskStatus(TaskStatusUpdateDTO taskStatusUpdateDTO, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!"));
        taskStatusMapper.updateEntity(taskStatusUpdateDTO, taskStatus);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(savedTaskStatus);
    }

    public void deleteTaskStatus(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
