package hexlet.code.mapper;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TaskMapper {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private LabelRepository labelRepository;

    public TaskDTO toDTO(Task task) {
        var taskDto = new TaskDTO();
        taskDto.setId(task.getId());
        taskDto.setIndex(task.getIndex());
        taskDto.setCreatedAt(task.getCreatedAt());
        taskDto.setTitle(task.getTitle());
        taskDto.setContent(task.getContent());
        if (task.getAssignee() != null) {
            taskDto.setAssignee_id(task.getAssignee().getId());
        }
        if (task.getTaskStatus() != null) {
            taskDto.setStatus(task.getTaskStatus().getSlug());
        }
        taskDto.setTaskLabelIds(
                task.getLabels() != null
                        ? task.getLabels().stream().map(Label::getId).toList()
                        : List.of()
        );
        return taskDto;
    }

    public Task toEntity(TaskCreateDTO taskCreateDTO) {
        var task = new Task();
        task.setIndex(taskCreateDTO.getIndex());
        task.setTitle(taskCreateDTO.getTitle());
        task.setContent(taskCreateDTO.getContent());
        if (taskCreateDTO.getAssignee_id() != null) {
            task.setAssignee(userRepository.findById(taskCreateDTO.getAssignee_id())
                            .orElseThrow(() -> new ResourceNotFoundException("Assignee not found!")));
        }

        task.setTaskStatus(taskStatusRepository.findBySlug(taskCreateDTO.getStatus())
                        .orElseThrow(() -> new ResourceNotFoundException("Task status not found!")));

        if (taskCreateDTO.getTaskLabelIds() != null) {

            var labels = labelRepository.findAllById(taskCreateDTO.getTaskLabelIds());

            if (labels.size() != taskCreateDTO.getTaskLabelIds().size()) {
                throw new ResourceNotFoundException("One or more labels not found!");
            }
            task.setLabels(labels);
        } else {
            task.setLabels(new ArrayList<>());
        }

        return task;
    }

    public void updateEntity(TaskUpdateDTO taskUpdateDTO, Task task) {


        if (taskUpdateDTO.getIndex() != null) {
            task.setIndex(taskUpdateDTO.getIndex());
        }

        if (taskUpdateDTO.getTitle() != null) {
            task.setTitle(taskUpdateDTO.getTitle());
        }

        if (taskUpdateDTO.getContent() != null) {
            task.setContent(taskUpdateDTO.getContent());
        }

        if (taskUpdateDTO.getAssignee_id() != null) {
            task.setAssignee(userRepository.findById(taskUpdateDTO.getAssignee_id())
                    .orElseThrow(() -> new ResourceNotFoundException("Assignee not found!")));
        } else {
            task.setAssignee(null);
        }

        if (taskUpdateDTO.getStatus() != null) {
            task.setTaskStatus(taskStatusRepository.findBySlug(taskUpdateDTO.getStatus())
                    .orElseThrow(() -> new ResourceNotFoundException("Task status not found!")));
        }

        var taskLabelIds = taskUpdateDTO.getTaskLabelIds();

        if (taskLabelIds != null && !taskLabelIds.isEmpty()) {
            var labels = labelRepository.findAllById(taskLabelIds);

            if (labels.size() != taskLabelIds.size()) {
                throw new ResourceNotFoundException("One or more labels not found!");
            }

            task.setLabels(labels);
        } else {
            task.setLabels(new ArrayList<>());
        }
    }
}
