package hexlet.code.app.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class UserCreateDTO {
    private String firstName;
    private String lastName;
    private String email;
}
