package ru.azamatkomaev.blog.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest {
    @NotEmpty(message = "username field cannot be empty")
    private String username;

    @NotEmpty(message = "password field cannot be empty")
    @Size(min = 8, message = "password should contain more that 8 symbols")
    private String password;
}
