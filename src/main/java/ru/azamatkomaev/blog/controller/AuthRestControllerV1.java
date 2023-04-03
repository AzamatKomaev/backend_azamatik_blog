package ru.azamatkomaev.blog.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.azamatkomaev.blog.model.User;
import ru.azamatkomaev.blog.request.LoginRequest;
import ru.azamatkomaev.blog.request.RegisterRequest;
import ru.azamatkomaev.blog.response.LoginResponse;
import ru.azamatkomaev.blog.response.UserResponse;
import ru.azamatkomaev.blog.service.AuthService;
import ru.azamatkomaev.blog.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthRestControllerV1 {
    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMe(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        return ResponseEntity.ok(UserResponse.toUserResponse(user));
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserResponse register(@RequestBody @Valid RegisterRequest request) {
        User registeredUser = authService.registerUser(request.getUsername(), request.getPassword());
        return UserResponse.toUserResponse(registeredUser);
    }

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.CREATED)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        User user = userService.getUserByUsername(request.getUsername());
        return LoginResponse.builder()
            .token(authService.loginUser(user, request.getPassword()))
            .build();
    }
}
