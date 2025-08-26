package co.com.pragma.api.dto;

import co.com.pragma.model.user.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String name,
        String surname,
        String email,
        DocumentType documentType,
        String documentNumber,
        LocalDate birthDate,
        String address,
        String phoneNumber,
        Integer baseSalary,
        boolean active,
        LocalDate createdAt,
        UUID roleId
) {
}
