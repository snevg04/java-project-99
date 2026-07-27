package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.ConflictException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    @Override
    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        if (
                taskStatusRepository.existsBySlug(taskStatusCreateDTO.getSlug())
                        || taskStatusRepository.existsByName(taskStatusCreateDTO.getName())
        ) {
            throw new ConflictException("Task status with given slug or name already exists!");
        }
        var taskStatus = taskStatusMapper.toEntity(taskStatusCreateDTO);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(savedTaskStatus);
    }

    @Override
    public List<TaskStatusDTO> getAll() {
        var taskStatuses = taskStatusRepository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::toDTO)
                .toList();
    }

    @Override
    public TaskStatusDTO getById(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!"));
        return taskStatusMapper.toDTO(taskStatus);
    }

    @Override
    public TaskStatusDTO updateTaskStatus(TaskStatusUpdateDTO taskStatusUpdateDTO, Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!"));
        taskStatusMapper.updateEntity(taskStatusUpdateDTO, taskStatus);
        var savedTaskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.toDTO(savedTaskStatus);
    }

    @Override
    public void deleteTaskStatus(Long id) {
        var taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status not found!"));
        taskStatusRepository.delete(taskStatus);
    }
}
