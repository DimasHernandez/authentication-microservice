package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.enums.DocumentType;
import co.com.pragma.r2dbc.entities.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    UserReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    UserReactiveRepositoryAdapter repositoryAdapter;

    @Test
    void shouldSaveUser() {
        // Arrange
        UserEntity userEntity = userEntityMock();
        User user = userMock();

        // Mock Reactive repositories
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));
        when(mapper.map(any(UserEntity.class), eq(User.class))).thenReturn(user);
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(userEntity);

        // Act
        Mono<User> result = repositoryAdapter.registerUser(user);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser ->
                        savedUser.getId().equals(UUID.fromString("cd0aa3bf-628b-4f71-ac8f-93a280176353")) &&
                                savedUser.getName().equals("Pepe") &&
                                savedUser.getSurname().equals("Perez") &&
                                savedUser.getEmail().equals("pepe@gmail.com")
                )
                .verifyComplete();
    }

    @Test
    void shouldReturnEmptyMonoWhenSaveFails() {
        // Arrange
        UserEntity userEntity = userEntityMock();
        User user = userMock();

        // Mock Reactive repositories
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.empty());
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(userEntity);

        // Act
        Mono<User> result = repositoryAdapter.registerUser(user);

        // Assert
        StepVerifier.create(result)
                .expectComplete() // is completed, but without issuing any value.
                .verify();
    }

    @Test
    void shouldThrowErrorWhenSavingFails() {
        // Arrange
        UserEntity userEntity = userEntityMock();
        User user = userMock();

        // Mock Reactive repositories
        when(repository.save(any(UserEntity.class))).thenReturn(Mono.error(new RuntimeException("Error saving to database")));
        when(mapper.map(any(User.class), eq(UserEntity.class))).thenReturn(userEntity);

        // Act
        Mono<User> result = repositoryAdapter.registerUser(user);

        // Assert
        StepVerifier.create(result)
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                                throwable.getMessage().equals("Error saving to database"))
                .verify();
    }

    @Test
    void shouldNotExistsUserWithEmailDuplicated() {
        // Arrange
        String email = "pepe@gmail.com";

        // Mock Reactive repositories
        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.just(false));

        // Act
        Mono<Boolean> result = repositoryAdapter.existsUserEmail(email);

        // Assert
        StepVerifier.create(result)
                .expectNext(false)
                .verifyComplete();
    }

    @Test
    void shouldEmailDuplicated() {
        // Arrange
        String email = "pepe@gmail.com";

        // Mock Reactive repositories
        when(repository.existsByEmail(any(String.class))).thenReturn(Mono.just(true));

        // Act
        Mono<Boolean> result = repositoryAdapter.existsUserEmail(email);

        // Assert
        StepVerifier.create(result)
                .expectNext(true)
                .verifyComplete();
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

    private UserEntity userEntityMock() {
        return UserEntity.builder()
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
                .isActive(true)
                .createdAt(LocalDate.now())
                .roleId(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315"))
                .build();
    }
}
