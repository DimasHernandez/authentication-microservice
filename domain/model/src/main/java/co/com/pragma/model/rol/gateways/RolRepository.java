package co.com.pragma.model.rol.gateways;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RolRepository {

    Mono<Role> findRoleById(UUID id);

    Mono<Role> findRoleByName(RoleType name);
}
