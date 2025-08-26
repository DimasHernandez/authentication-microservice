package co.com.pragma.model.rol;

import co.com.pragma.model.rol.enums.RoleType;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Role {

    private UUID id;

    private RoleType roleType;

    private String description;
}
