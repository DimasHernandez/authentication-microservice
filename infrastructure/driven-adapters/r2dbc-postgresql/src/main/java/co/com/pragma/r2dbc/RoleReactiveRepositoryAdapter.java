package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.model.rol.gateways.RolRepository;
import co.com.pragma.r2dbc.entities.RoleEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class RoleReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Role,
        RoleEntity,
        UUID,
        RoleReactiveRepository> implements RolRepository {

    public RoleReactiveRepositoryAdapter(RoleReactiveRepository repository, ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Role.class));
    }

    @Override
    public Mono<Role> findRoleByName(RoleType roleType) {
        return repository.findByRoleTypeName(roleType)
                .map(super::toEntity);
    }
}
