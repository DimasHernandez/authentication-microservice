package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserInfoResponse;
import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.api.dto.UserResponse;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.enums.DocumentType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "documentType", source = "documentType", qualifiedByName = "stringToRolType")
    User toDomain(UserRequest userRequest);

    UserResponse toResponse(User user);

    UserInfoResponse toInfoResponse(User user);

    @Named("stringToRolType")
    default DocumentType mapStringToDocumentType(String documentType) {
        try {
            return DocumentType.valueOf(documentType.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("El tipo_documento no valido: " + documentType);
        }
    }
}
