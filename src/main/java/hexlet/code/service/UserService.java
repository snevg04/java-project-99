package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAll();
    UserDTO createUser(UserCreateDTO userCreateDTO);
    UserDTO showUser(Long id);
    UserDTO updateUser(UserUpdateDTO userUpdateDTO, Long id);
    void deleteUser(Long id);
}
