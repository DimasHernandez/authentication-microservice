package co.com.pragma.r2dbc;

import co.com.pragma.model.rol.enums.RoleType;
import co.com.pragma.r2dbc.entities.RoleEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RoleReactiveRepository extends ReactiveCrudRepository<RoleEntity, String>, ReactiveQueryByExampleExecutor<RoleEntity> {

    @Query("SELECT * FROM roles WHERE role_type = :name")
    Mono<RoleEntity> findByRoleTypeName(RoleType name);
}
