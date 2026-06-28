package hexlet.code.app;

import hexlet.code.app.model.Label;
import hexlet.code.app.model.TaskStatus;
import hexlet.code.app.model.User;
import hexlet.code.app.repository.LabelRepository;
import hexlet.code.app.repository.TaskStatusRepository;
import hexlet.code.app.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableJpaAuditing
public class AppApplication {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    public AppApplication(UserRepository userRepository,
                          PasswordEncoder passwordEncoder, TaskStatusRepository taskStatusRepository, LabelRepository labelRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
    }

    @PostConstruct
    public void init() {
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPasswordDigest(passwordEncoder.encode("qwerty"));
            userRepository.save(admin);
        }

        if (taskStatusRepository.findBySlug("draft").isEmpty()) {
            TaskStatus defaultStatus1 = new TaskStatus();
            defaultStatus1.setName("Draft");
            defaultStatus1.setSlug("draft");
            taskStatusRepository.save(defaultStatus1);
        }

        if (taskStatusRepository.findBySlug("to_review").isEmpty()) {
            TaskStatus defaultStatus2 = new TaskStatus();
            defaultStatus2.setName("To review");
            defaultStatus2.setSlug("to_review");
            taskStatusRepository.save(defaultStatus2);
        }

        if (taskStatusRepository.findBySlug("to_be_fixed").isEmpty()) {
            TaskStatus defaultStatus3 = new TaskStatus();
            defaultStatus3.setName("To be fixed");
            defaultStatus3.setSlug("to_be_fixed");
            taskStatusRepository.save(defaultStatus3);
        }
        if (taskStatusRepository.findBySlug("to_publish").isEmpty()) {
            TaskStatus defaultStatus4 = new TaskStatus();
            defaultStatus4.setName("To publish");
            defaultStatus4.setSlug("to_publish");
            taskStatusRepository.save(defaultStatus4);
        }

        if (taskStatusRepository.findBySlug("published").isEmpty()) {
            TaskStatus defaultStatus5 = new TaskStatus();
            defaultStatus5.setName("Published");
            defaultStatus5.setSlug("published");
            taskStatusRepository.save(defaultStatus5);
        }

        if (labelRepository.findByName("feature").isEmpty()) {
            Label label = new Label();
            label.setName("feature");
            labelRepository.save(label);
        }

        if (labelRepository.findByName("bug").isEmpty()) {
            Label label = new Label();
            label.setName("bug");
            labelRepository.save(label);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
