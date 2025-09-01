package co.com.pragma.usecase.auth;

import co.com.pragma.model.auth.AccessToken;
import co.com.pragma.model.auth.UserCredential;
import co.com.pragma.model.auth.gateways.JwtGateway;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.enums.DocumentType;
import co.com.pragma.model.user.gateways.LoggerRepository;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private PasswordEncoderGateway passwordEncoder;

    @Mock
    private JwtGateway jwtGateway;

    @Mock
    private LoggerRepository logger;

    @InjectMocks
    private AuthUseCase authUseCase;

    @Test
    void shouldGenerateJwtTokenSuccessfully() {
        // Arrange
        UserCredential userCredential = userCredentialMock();
        User user = userMock();
        Role role = roleMock();
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJwZXBlQGdtYWlsLmNvbSIsImlzcyI6ImF1dGhlbnRpY2F0aW9uLW1zdmMiLCJpYX" +
                "QiOjE3NTY3NDIwODYsImV4cCI6MTc1Njc0Mjk4NiwidXNlcklkIjoiNWIwZDBhNmItOWVjMS00YzM4LTliYTQtYTY4NzJjNmY4MTk2I" +
                "iwiZG9jdW1lbnROdW1iZXIiOiIxMjM0NTY3ODkiLCJlbWFpbCI6InBlcGVAZ21haWwuY29tIiwibmFtZSI6IlBlcGUgUGVyZXoiLCJyb" +
                "2xlIjoiQVBQTElDQU5UIn0.w6nNNCgQXPPouuGJUEdyv72zy2ouctiK-C0vaRWzD1s";

        // When reactive repositories
        when(userRepository.getUserByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matchesPassword(any(String.class), eq(user.getPassword()))).thenReturn(true);
        when(rolRepository.findRoleById(any(UUID.class))).thenReturn(Mono.just(role));
        when(jwtGateway.generateToken(any(User.class), any(String.class))).thenReturn(token);

        // Act
        Mono<AccessToken> result = authUseCase.login(userCredential);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(accessToken -> accessToken.getToken().equals(token))
                .verifyComplete();
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenEmailNotExist() {
        // Arrange
        UserCredential userCredential = userCredentialMock();

        // When reactive repositories
        when(userRepository.getUserByEmail(any(String.class))).thenReturn(Mono.empty());

        // Act
        Mono<AccessToken> result = authUseCase.login(userCredential);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidCredentialsException &&
                                throwable.getMessage().equals("Correo electronico o contraseña incorrectos"))
                .verify();
    }

    @Test
    void shouldThrowInvalidCredentialsExceptionWhenPasswordNotMatch() {
        // Arrange
        UserCredential userCredential = userCredentialMock();
        User user = userMock();

        // When reactive repositories
        when(userRepository.getUserByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matchesPassword(any(String.class), eq(user.getPassword()))).thenReturn(false);

        // Act
        Mono<AccessToken> result = authUseCase.login(userCredential);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof InvalidCredentialsException &&
                                throwable.getMessage().equals("Correo electronico o contraseña incorrectos"))
                .verify();
    }

    @Test
    void shouldThrowRoleNotFoundExceptionWhenRoleNotExists() {
        // Arrange
        UserCredential userCredential = userCredentialMock();
        User user = userMock();

        // When reactive repositories
        when(userRepository.getUserByEmail(any(String.class))).thenReturn(Mono.just(user));
        when(passwordEncoder.matchesPassword(any(String.class), eq(user.getPassword()))).thenReturn(true);
        when(rolRepository.findRoleById(any(UUID.class))).thenReturn(Mono.empty());

        // Act
        Mono<AccessToken> result = authUseCase.login(userCredential);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RoleNotFoundException &&
                                throwable.getMessage().equals("Role no encontrado"))
                .verify();
    }

    private UserCredential userCredentialMock() {
        return new UserCredential("pepe@gmail.com", "test1234");
    }

    private User userMock() {
        return User.builder()
                .id(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"))
                .name("Pepe")
                .surname("Perez")
                .email("pepe@gmail.com")
                // rawPassword -> test1234
                .password("$2a$12$Y0nugDEdZNGYwMPKd6Vke.zhP4A4jaj3exQlUZirsU1Z5NtT9lSHG")
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

    private Role roleMock() {
        return Role.builder()
                .id(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315"))
                .roleType(RoleType.APPLICANT)
                .description("Rol APPLICANT")
                .build();
    }

}