package co.com.pragma.api.dto;

public record UserBasicInfo(

        String name,
        String surname,
        String email,
        String documentNumber,
        Integer baseSalary
) {
}
