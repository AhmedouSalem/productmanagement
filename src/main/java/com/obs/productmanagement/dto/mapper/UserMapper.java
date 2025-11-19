package com.obs.productmanagement.dto.mapper;

import com.obs.productmanagement.dto.UserCreateRequest;
import com.obs.productmanagement.dto.UserResponse;
import com.obs.productmanagement.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // DTO -> Entity
    User toEntity(UserCreateRequest dto);

    // Entity -> DTO
    UserResponse toResponse(User user);
}
