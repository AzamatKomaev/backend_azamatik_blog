package ru.azamatkomaev.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.web.context.WebApplicationContext;
import ru.azamatkomaev.blog.model.User;
import ru.azamatkomaev.blog.repository.UserRepository;
import ru.azamatkomaev.blog.request.LoginRequest;
import ru.azamatkomaev.blog.request.RegisterRequest;

import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRestControllerV1Test {

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String GET_ME_ENDPOINT = "/api/v1/auth/me";
    private final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    private static Stream<RegisterRequest> providePasswordSizeLessThanEIGHTRegisterData() {
        return Stream.of(
            RegisterRequest.builder().username("Azamat1").password("s").build(),
            RegisterRequest.builder().username("Azamat2").password("small").build(),
            RegisterRequest.builder().username("Azamat3").password("small_p").build()
        );
    }

    private static Stream<RegisterRequest> provideValidRegisterData() {
        return Stream.of(
            RegisterRequest.builder().username("Azamat").password("azamat12345").build()
        );
    }

    private static Stream<LoginRequest> provideNonExistingUsernameLoginData() {
        return Stream.of(
            LoginRequest.builder().username("UnknownUsername").password("azamat12345").build()
        );
    }

    private static Stream<LoginRequest> provideInvalidPasswordLoginData() {
        return Stream.of(
            LoginRequest.builder().username("Azamat").password("some_password").build()
        );
    }

    @Test
    public void testRegisterWithEmptyBody() throws Exception {
        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content("");
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.body", is("Required request body is missing")));
    }

    @ParameterizedTest
    @MethodSource("providePasswordSizeLessThanEIGHTRegisterData")
    public void testRegisterWithPasswordSizeLessThanEIGHT(RegisterRequest registerRequest) throws Exception {
        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(registerRequest));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.password", is("password should contain more than 8 symbols")));
    }

    @ParameterizedTest
    @MethodSource("provideValidRegisterData")
    public void testSuccessfullyRegisterUser(RegisterRequest registerRequest) throws Exception {
        User.UserBuilder userBuilder = User.builder()
            .username(registerRequest.getUsername())
            .password(passwordEncoder.encode(registerRequest.getPassword()));

        User user = userBuilder.build();
        User userWithId = userBuilder.id(1L).build();

        when(userRepository.save(user)).thenReturn(userWithId);

        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(registerRequest));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.[*]", hasSize(3)))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.username", is(user.getUsername())));

        verify(userRepository, times(1)).save(user);
    }

    @Test
    public void testLoginWithEmptyBody() throws Exception {
        RequestBuilder requestBuilder = post(LOGIN_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content("");
        mockMvc.perform(requestBuilder)
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.body", is("Required request body is missing")));
    }

    @ParameterizedTest
    @MethodSource("provideNonExistingUsernameLoginData")
    public void testLoginWithNonExistingUsername(LoginRequest loginRequest) throws Exception {
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.empty());

        RequestBuilder requestBuilder = post(LOGIN_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(404))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.message", is("Cannot find any user with username: " + loginRequest.getUsername())));

        verify(userRepository, times(1)).findByUsername(loginRequest.getUsername());
    }

    @ParameterizedTest
    @MethodSource("provideInvalidPasswordLoginData")
    public void testLoginWithInvalidPassword(LoginRequest loginRequest) throws Exception {
        User userToReturn = User.builder()
                .id(1L)
                .username(loginRequest.getUsername())
                .password(passwordEncoder.encode("not_valid_password"))
                .build();
        when(userRepository.findByUsername(loginRequest.getUsername())).thenReturn(Optional.of(userToReturn));

        RequestBuilder requestBuilder = post(LOGIN_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(loginRequest));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.message", is("Bad credentials")));
    }
}
