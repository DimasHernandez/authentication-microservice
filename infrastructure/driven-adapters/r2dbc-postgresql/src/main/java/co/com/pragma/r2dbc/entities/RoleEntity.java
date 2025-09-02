package co.com.pragma.r2dbc.entities;

import co.com.pragma.model.rol.enums.RoleType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class RoleEntity {

    @Id
    @Column("role_id")
    private UUID id;

    @Column("role_type")
    private RoleType roleType;

    private String description;
}
