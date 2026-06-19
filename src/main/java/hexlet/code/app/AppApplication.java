package hexlet.code.app;

import hexlet.code.app.model.User;
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

    public AppApplication(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setEmail("hexlet@example.com");
            admin.setPassword(
                    passwordEncoder.encode("querty")
            );
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }

}
