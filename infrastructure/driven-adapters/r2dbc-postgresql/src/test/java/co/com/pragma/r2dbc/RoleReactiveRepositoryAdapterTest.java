package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.r2dbc.entities.RoleEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleReactiveRepositoryAdapterTest {

    @Mock
    RoleReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    @InjectMocks
    RoleReactiveRepositoryAdapter repositoryAdapter;

    @Test
    void shouldGetRoleById() {
        // Arrange
        RoleType roleType = RoleType.APPLICANT;
        Role role = roleMock();
        RoleEntity roleEntity = roleEntityMock();

        // Mock Reactive repositories
        when(repository.findByRoleTypeName(any(RoleType.class))).thenReturn(Mono.just(roleEntity));
        when(mapper.map(any(RoleEntity.class), eq(Role.class))).thenReturn(role);

        // Act
        Mono<Role> result = repositoryAdapter.findRoleByName(roleType);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(roleDb ->
                        roleDb.getId().equals(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315")) &&
                                roleDb.getRoleType().equals(RoleType.APPLICANT))
                .verifyComplete();
    }

    @Test
    void shouldReturnMonoEmptyWhenRoleByIdFails() {
        // Arrange
        RoleType roleType = RoleType.APPLICANT;

        // Mock Reactive repositories
        when(repository.findByRoleTypeName(any(RoleType.class))).thenReturn(Mono.empty());

        // Act
        Mono<Role> result = repositoryAdapter.findRoleByName(roleType);

        // Assert
        StepVerifier.create(result)
                .expectComplete()
                .verify();
    }

    private Role roleMock() {
        return Role.builder()
                .id(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315"))
                .roleType(RoleType.APPLICANT)
                .description("Rol APPLICANT")
                .build();
    }

    private RoleEntity roleEntityMock() {
        return RoleEntity.builder()
                .id(UUID.fromString("65c9bbc9-d240-4ed0-a2f7-91d7297c1315"))
                .roleType(RoleType.APPLICANT)
                .description("Rol APPLICANT")
                .build();
    }

}