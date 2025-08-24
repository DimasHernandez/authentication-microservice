package co.com.pragma.r2dbc.entities;

import co.com.pragma.model.user.enums.DocumentType;


import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.util.UUID;

@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserEntity {

    @Id
    @Column("user_id")
    private UUID id;

    private String name;

    private String surname;

    private String email;

    @Column("document_type")
    private DocumentType documentType;

    @Column("document_number")
    private String documentNumber;

    @Column("birth_date")
    private LocalDate birthDate;

    private String address;

    @Column("phone_number")
    private String phoneNumber;

    @Column("base_salary")
    private Integer baseSalary;

    @Column("is_active")
    private boolean isActive;

//    @CreatedDate
    @Column("created_at")
    private LocalDate createdAt;

    @Column("role_id")
    private UUID roleId;
}
