package ru.azamatkomaev.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import ru.azamatkomaev.blog.model.User;
import ru.azamatkomaev.blog.request.RegisterRequest;
import ru.azamatkomaev.blog.service.UserService;

import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthRestControllerV1Test {

    @MockBean
    private UserService userService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper mapper = new ObjectMapper();

    private final String GET_ME_ENDPOINT = "/api/v1/auth/me";
    private final String REGISTER_ENDPOINT = "/api/v1/auth/register";
    private final String LOGIN_ENDPOINT = "/api/v1/auth/login";

    private static Stream<Arguments> providePasswordSizeLessThanEIGHTUserData() {
        return Stream.of(
            Arguments.of("Azamat1", "s"),
            Arguments.of("Azamat2", "small"),
            Arguments.of("Azamat3", "small_p")
        );
    }

    private static Stream<Arguments> provideValidUserData() {
        return Stream.of(
            Arguments.of("Azamat", "azamat12345")
        );
    }

    @Test
    public void testRegisterWithEmptyBody() throws Exception {
        when(userService.saveUser(anyString(), anyString())).thenReturn(null);

        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content("");
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.body", is("Required request body is missing")));

        verify(userService, never()).saveUser(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("providePasswordSizeLessThanEIGHTUserData")
    public void testRegisterWithPasswordSizeLessThanEIGHT(String username, String password) throws Exception {
        when(userService.saveUser(anyString(), anyString())).thenReturn(null);

        RegisterRequest request = RegisterRequest.builder()
            .username(username)
            .password(password) // length of the string less than 8
            .build();

        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(400))
            .andExpect(jsonPath("$.[*]", hasSize(1)))
            .andExpect(jsonPath("$.password", is("password should contain more than 8 symbols")));

        verify(userService, never()).saveUser(anyString(), anyString());
    }

    @ParameterizedTest
    @MethodSource("provideValidUserData")
    public void testSuccessfullyRegisterUser(String username, String password) throws Exception {
        User user = User.builder()
            .id(1L)
            .username(username)
            .password(passwordEncoder.encode(password))
            .build();

        when(userService.saveUser(username, password)).thenReturn(user);

        RegisterRequest request = RegisterRequest.builder()
            .username(username)
            .password(password)
            .build();

        RequestBuilder requestBuilder = post(REGISTER_ENDPOINT)
            .contentType(MediaType.APPLICATION_JSON)
            .content(mapper.writeValueAsString(request));
        mockMvc.perform(requestBuilder)
            .andExpect(status().is(201))
            .andExpect(jsonPath("$.[*]", hasSize(3)))
            .andExpect(jsonPath("$.id").isNumber())
            .andExpect(jsonPath("$.username", is(username)));

        verify(userService, times(1)).saveUser(username, password);
    }
}
