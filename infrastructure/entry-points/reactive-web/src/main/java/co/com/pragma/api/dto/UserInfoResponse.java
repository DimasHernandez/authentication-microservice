package co.com.pragma.api.dto;

import co.com.pragma.model.user.enums.DocumentType;

import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        String name,
        String surname,
        String email,
        DocumentType documentType,
        String documentNumber,
        String address,
        String phoneNumber,
        Integer baseSalary
) {
}
