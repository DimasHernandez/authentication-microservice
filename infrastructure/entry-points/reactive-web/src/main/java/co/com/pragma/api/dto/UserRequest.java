package co.com.pragma.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequest(
        @NotBlank(message = "El nombre es obligatorio") String name,
        @NotBlank(message = "El apellido es obligatorio") String surname,
        @NotBlank(message = "El email es obligatorio")
        @Pattern(regexp = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$", message = "Email no valido") String email,
        @NotNull(message = "El tipo_documento es obligatorio")
        @NotBlank(message = "El tipo_documento no puede ser vacio") String documentType,
        @NotBlank(message = "El numero del documento es obligatorio") String documentNumber,
        @NotNull(message = "La fecha de nacimiento es obligatorio") LocalDate birthDate,
        @NotBlank(message = "La dirección es obligatoria obligatorio") String address,
        @NotBlank(message = "El numero del telefono es obligatorio")
        @Pattern(regexp = "\\d{10}", message = "El teléfono debe tener 10 dígitos") String phoneNumber,
        @NotNull(message = "El salario es obligatorio")
        @Min(value = 0, message = "El salario no puede ser inferior a cero")
        @Max(value = 15000000, message = "El salario no puede ser superior a 15000000")
        Integer baseSalary
) {
}
