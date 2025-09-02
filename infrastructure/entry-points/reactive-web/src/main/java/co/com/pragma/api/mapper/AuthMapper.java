package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.LoginRequest;
import co.com.pragma.api.dto.LoginResponse;
import co.com.pragma.model.auth.AccessToken;
import co.com.pragma.model.auth.UserCredential;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthMapper {

    UserCredential toDomain(LoginRequest loginRequest);

    @Mapping(target = "accessToken", source = "token")
    LoginResponse toResponse(AccessToken accessToken);
}
