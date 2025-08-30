package co.com.pragma.api;

import co.com.pragma.api.config.ConfigBeansTest;
import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.dto.UserInfoResponse;
import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.api.dto.UserResponse;
import co.com.pragma.api.errorhandler.GlobalErrorHandler;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.enums.DocumentType;
import co.com.pragma.usecase.user.UserUseCase;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserRouterRest.class, UserHandler.class, ConfigBeansTest.class, GlobalErrorHandler.class})
@EnableConfigurationProperties(ConfigBeansTest.class)
@WebFluxTest
class UserRouterRestTest {

    @MockitoBean
    private UserUseCase userUseCase;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private Validator validator;

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserPath userPath;

    @Test
    void shouldRegisterSuccessfully() {
        User user = userMock();
        UserRequest request = userRequestMock();
        UserResponse response = userResponseMock();

        when(validator.validate(any())).thenReturn(Set.of());
        when(userMapper.toDomain(any(UserRequest.class))).thenReturn(user);
        when(userUseCase.registerUser(any(User.class))).thenReturn(Mono.just(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(response);

        webTestClient.post()
                .uri("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponse.class)
                .value(userResponse ->
                        Assertions.assertThat(
                                userResponse.id()).isEqualTo(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"))
                );
    }

    @Test
    void shouldReturnUnprocessableEntityWhenNameIsNull() {
        UserRequest invalidUserRequest = new UserRequest(
                null,
                "Perez",
                "pepe@gmail.com",
                "DNI",
                "1234567",
                LocalDate.now(),
                "3124567890",
                "Cr 5 N° 798",
                2100000
        );

        when(validator.validate(any())).thenAnswer(invocation -> {
                    UserRequest request = invocation.getArgument(0);
                    Set<ConstraintViolation<UserRequest>> violations = new HashSet<>();

                    if (request.name() == null) {
                        ConstraintViolation<UserRequest> violation = mock(ConstraintViolation.class);
                        when(violation.getMessage()).thenReturn("El nombre es obligatorio");
                        violations.add(violation);
                    }
                    return violations;
                }
        );
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.error").isEqualTo("Fallo validacion")
                .jsonPath("$.status").isEqualTo(422)
                .jsonPath("$.detail").isEqualTo("null: El nombre es obligatorio");
    }

    @Test
    void shouldReturnUnprocessableEntityWhenBaseSalaryIsOutOfRange() {

        UserRequest invalidUserRequest = new UserRequest(
                "Pepe",
                "Perez",
                "pepe@gmail.com",
                "DNI",
                "1234567",
                LocalDate.now(),
                "3124567890",
                "Cr 5 N° 798",
                2100000000
        );

        when(validator.validate(any())).thenAnswer(invocation -> {
                    UserRequest request = invocation.getArgument(0);
                    Set<ConstraintViolation<UserRequest>> violations = new HashSet<>();

                    if (request.baseSalary() == null || request.baseSalary() < 0 || request.baseSalary() > 15000000) {
                        ConstraintViolation<UserRequest> violation = mock(ConstraintViolation.class);
                        when(violation.getMessage()).thenReturn("El salario no puede ser superior a 15000000");
                        violations.add(violation);
                    }
                    return violations;
                }
        );

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidUserRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .consumeWith(System.out::println)
                .jsonPath("$.error").isEqualTo("Fallo validacion")
                .jsonPath("$.status").isEqualTo(422)
                .jsonPath("$.detail").isEqualTo("null: El salario no puede ser superior a 15000000");
    }

    @Test
    void shouldGetUserByDocumentIdentity() {
        // Arrange
        String documentNumber = "1234567";
        User user = userMock();
        UserInfoResponse userInfoResponse = userInfoResponseMock();

        when(userUseCase.getUserByDocumentIdentity(documentNumber)).thenReturn(Mono.just(user));
        when(userMapper.toInfoResponse(any(User.class))).thenReturn(userInfoResponse);

        webTestClient.get()
                .uri("/api/v1/users/" + documentNumber)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserInfoResponse.class)
                .value(response ->
                        Assertions.assertThat(response.documentNumber()).isEqualTo(documentNumber));
    }

    @Test
    void shouldThrowableUserNotFoundException() {
        // Arrange
        String documentNumber = "2020";

        when(userUseCase.getUserByDocumentIdentity(documentNumber))
                .thenReturn(Mono.error(new UserNotFoundException("Usuario no encontrado")));

        webTestClient.get()
                .uri("/api/v1/users/" + documentNumber)
                .exchange()
                .expectStatus().isNotFound()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.error").isEqualTo("Error de negocio")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.detail").isEqualTo("Usuario no encontrado");
    }

    @Test
    void shouldLoadUserPathProperties() {
        assertEquals("/api/v1/users", userPath.getUsers());
        assertEquals("/api/v1/users/{documentNumber}", userPath.getUserByDocumentNumber());
    }

    private User userMock() {
        return User.builder()
                .id(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"))
                .name("Pepe")
                .surname("Perez")
                .email("pepe@gmail.com")
                .documentType(DocumentType.DNI)
                .documentNumber("1234567")
                .birthDate(LocalDate.now())
                .address("Cr 5 N° 798")
                .phoneNumber("3124567890")
                .baseSalary(2100000)
                .active(true)
                .createdAt(LocalDate.now())
                .roleId(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315"))
                .build();
    }

    private UserRequest userRequestMock() {
        return new UserRequest(
                "Pepe",
                "Perez",
                "pepe@gmail.com",
                "DNI",
                "1234567",
                LocalDate.now(),
                "3124567890",
                "Cr 5 N° 798",
                2100000
        );
    }

    private UserResponse userResponseMock() {
        return new UserResponse(
                UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"),
                "Pepe",
                "Perez",
                "pepe@gmail.com",
                DocumentType.DNI,
                "1234567",
                LocalDate.now(),
                "Cr 5 N° 798",
                "3124567890",
                2100000,
                true,
                LocalDate.now(),
                UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315")
        );
    }

    private UserInfoResponse userInfoResponseMock() {
        return new UserInfoResponse(
                UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"),
                "Pepe",
                "Perez",
                "pepe@gmail.com",
                DocumentType.DNI,
                "1234567",
                "Cr 5 N° 798",
                "3124567890",
                2100000
        );
    }
}
