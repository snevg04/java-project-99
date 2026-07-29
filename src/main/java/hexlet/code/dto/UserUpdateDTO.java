package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateDTO {
    @Setter(AccessLevel.NONE)
    private String firstName;

    @JsonIgnore
    private boolean firstNameUpdated;

    @Setter(AccessLevel.NONE)
    private String lastName;

    @JsonIgnore
    private boolean lastNameUpdated;

    @Email
    private String email;
    @Size(min = 3)
    private String password;

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        this.firstNameUpdated = true;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        this.lastNameUpdated = true;
    }
}
