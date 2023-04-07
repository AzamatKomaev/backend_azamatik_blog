package ru.azamatkomaev.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.azamatkomaev.blog.exception.NotFoundException;
import ru.azamatkomaev.blog.model.User;
import ru.azamatkomaev.blog.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("Cannot find any user with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository
            .findByUsername(username)
            .orElseThrow(() -> new NotFoundException("Cannot find any user with username: " + username));
    }

    public User saveUser(String username, String password) {
        User user = User.builder()
            .username(username)
            .password(password)
            .build();

        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
