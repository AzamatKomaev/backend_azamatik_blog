package ru.azamatkomaev.blog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.azamatkomaev.blog.model.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User registerUser(String username, String password) {
        User user = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .build();

        return userService.saveUser(user);
    }

    public String loginUser(User user, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
            user.getUsername(),
            password,
            null
        );
        authenticationManager.authenticate(authenticationToken);
        return jwtService.generateToken(user);
    }
}
