package co.com.pragma.usecase.user;

import co.com.pragma.model.exceptions.EmailAlreadyRegisteredException;
import co.com.pragma.model.exceptions.RoleNotFoundException;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.enums.DocumentType;
import co.com.pragma.model.user.gateways.LoggerRepository;
import co.com.pragma.model.user.gateways.PasswordEncoderGateway;
import co.com.pragma.model.user.gateways.TransactionalWrapper;
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
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserUseCaseTest {

    @Mock
    private RolRepository rolRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalWrapper transactionalWrapper;

    @Mock
    private LoggerRepository loggerRepository;

    @Mock
    private PasswordEncoderGateway passwordEncoderGateway;

    @InjectMocks
    private UserUseCase userUseCase;

    @Test
    void shouldSaveAndReturnUser() {
        // Arrange
        User user = userMock();
        Role role = roleMock();
        String hashedPassword = "$2a$10$qJThGZUTzMb5Axv6kO3Uae99YmE6pC1qfU2puYe3LxXk3uLojSRgu";

        // Mock reactive repositories
        when(rolRepository.findRoleByName(any(RoleType.class))).thenReturn(Mono.just(role));
        when(userRepository.existsUserEmail(any(String.class))).thenReturn(Mono.just(false));
        when(userRepository.registerUser(any(User.class))).thenReturn(Mono.just(user));
        when(transactionalWrapper.transactional(any(Mono.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        when(passwordEncoderGateway.hashPassword(any(String.class))).thenReturn(hashedPassword);

        // Act
        Mono<User> result = userUseCase.registerUser(user);

        // Assert with StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(savedUser ->
                        savedUser.getId().equals(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353")) &&
                                savedUser.getName().equals("Pepe") &&
                                savedUser.getSurname().equals("Perez"))
                .verifyComplete();
    }

    @Test
    void shouldReturnErrorWhenRoleNotFound() {
        // Arrange
        User user = userMock();
        String hashedPassword = "$2a$10$qJThGZUTzMb5Axv6kO3Uae99YmE6pC1qfU2puYe3LxXk3uLojSRgu";

        // Mock reactive repositories
        when(rolRepository.findRoleByName(any(RoleType.class))).thenReturn(Mono.empty());
        when(userRepository.existsUserEmail(any(String.class))).thenReturn(Mono.just(false));
        when(transactionalWrapper.transactional(any(Mono.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);
        when(passwordEncoderGateway.hashPassword(any(String.class))).thenReturn(hashedPassword);

        // Act
        Mono<User> result = userUseCase.registerUser(user);

        // Assert with StepVerifier
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RoleNotFoundException &&
                                throwable.getMessage().equals("Rol no encontrado"))
                .verify();
    }

    @Test
    void shouldReturnErrorWhenExistsUserEmail() {
        // Arrange
        User user = userMock();

        // Mock reactive repositories
        when(userRepository.existsUserEmail(any(String.class))).thenReturn(Mono.just(true));
        when(transactionalWrapper.transactional(any(Mono.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        // Act
        Mono<User> result = userUseCase.registerUser(user);

        // Assert with StepVerifier
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof EmailAlreadyRegisteredException &&
                                throwable.getMessage().equals("La direccion del correo electronico ya esta registrada."))
                .verify();
    }

    @Test
    void shouldReturnUserByDocumentIdentity() {
        // Arrangle
        String documentNumber = "1765420";
        User user = userMock();

        // Mock reactive repository
        when(userRepository.getUserByDocumentIdentity(any(String.class))).thenReturn(Mono.just(user));

        // Act
        Mono<User> result = userUseCase.getUserByDocumentIdentity(documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(userFound ->
                        userFound.getId().equals(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353")) &&
                                userFound.getName().equals("Pepe") &&
                                userFound.getEmail().equals("pepe@gmail.com"))
                .verifyComplete();
    }

    @Test
    void shouldReturnUserNotFoundExceptionFails() {
        // Arrange
        String documentNumber = "555";

        // Mock reactive repository
        when(userRepository.getUserByDocumentIdentity(any(String.class))).thenReturn(Mono.empty());

        // Act
        Mono<User> result = userUseCase.getUserByDocumentIdentity(documentNumber);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof UserNotFoundException &&
                                throwable.getMessage().equals("Usuario no encontrado")
                ).verify();
    }

    private User userMock() {
        return User.builder()
                .id(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353"))
                .name("Pepe")
                .surname("Perez")
                .email("pepe@gmail.com")
                .password("test1234")
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