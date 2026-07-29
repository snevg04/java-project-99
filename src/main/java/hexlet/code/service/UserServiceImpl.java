package hexlet.code.service;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    public List<UserDTO> getAll() {
        var users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        var user = userMapper.toEntity(userCreateDTO);
        user.setPasswordDigest(
                passwordEncoder.encode(userCreateDTO.getPassword())
        );

        var savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO showUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        return userMapper.toDTO(user);
    }

    @Override
    @Transactional
    public UserDTO updateUser(UserUpdateDTO userUpdateDTO, Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        userMapper.updateEntity(userUpdateDTO, user);

        if (userUpdateDTO.isFirstNameUpdated()) {
            user.setFirstName(userUpdateDTO.getFirstName());
        }
        if (userUpdateDTO.isLastNameUpdated()) {
            user.setLastName(userUpdateDTO.getLastName());
        }

        if (userUpdateDTO.getPassword() != null) {
            user.setPasswordDigest(passwordEncoder.encode(userUpdateDTO.getPassword()));
        }

        var savedUser = userRepository.save(user);
        return userMapper.toDTO(savedUser);
    }

    @Override
    @Transactional
    public UserDTO deleteUser(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found!"));
        var userDTO = userMapper.toDTO(user);
        userRepository.delete(user);
        return userDTO;
    }
}
