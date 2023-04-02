package ru.azamatkomaev.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
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
        // TODO: throw NotFoundException if user not found
        return userRepository
            .findById(id)
            .orElse(null);
    }

    public User getUserByUsername(String username) {
        // TODO: throw NotFoundException if user not found
        return userRepository
            .findByUsername(username)
            .orElse(null);
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}
