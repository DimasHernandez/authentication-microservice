package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserRequest;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "roleId", ignore = true)
    User toDomain(UserRequest userRequest);
}
