package co.com.pragma.model.rol.gateways;

import co.com.pragma.model.rol.Role;
import co.com.pragma.model.rol.enums.RoleType;
import reactor.core.publisher.Mono;

public interface RolRepository {

    Mono<Role> findRoleByName(RoleType name);
}
