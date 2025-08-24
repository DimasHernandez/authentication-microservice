package co.com.pragma.api.dto;

import co.com.pragma.model.user.enums.DocumentType;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El apellido es obligatorio") String surname,
        @NotBlank(message = "El email es obligatorio") String email,
        @NotNull(message = "El typo_documento es obligatorio") DocumentType documentType,
        @NotBlank(message = "El numero del documento es obligatorio") String documentNumber,
        @NotNull(message = "La fecha de nacimiento es obligatorio") LocalDate birthDate,
        @NotBlank(message = "La dirección es obligatoria obligatorio") String address,
        @NotBlank(message = "El numero del telefono es obligatorio") String phoneNumber,
        @NotNull(message = "El salario es obligatorio") @Min(0) @Max(15000000) Integer baseSalary
) {
}
