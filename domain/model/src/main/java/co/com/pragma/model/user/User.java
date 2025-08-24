package co.com.pragma.model.user;

import co.com.pragma.model.user.enums.DocumentType;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    private UUID id;

    private String name;

    private String surname;

    private String email;

    private DocumentType documentType;

    private String documentNumber;

    private LocalDate birthDate;

    private String address;

    private String phoneNumber;

    private Integer baseSalary;

    private boolean isActive;

    private LocalDate createdAt;

    private UUID roleId;

    public void activate() {
        this.isActive = true;
    }

    public void markCreatedNow() {
        this.createdAt = LocalDate.now();
    }
}
