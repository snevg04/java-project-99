package hexlet.code.app.repository;

import hexlet.code.app.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.scheduling.config.Task;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {
    public Optional<TaskStatus> findBySlug(String slug);
    boolean existsBySlug(String slug);
    boolean existsByName(String name);
}
