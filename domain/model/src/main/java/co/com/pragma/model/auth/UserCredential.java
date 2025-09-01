package co.com.pragma.model.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserCredential {

    private String email;

    private String password;

}
