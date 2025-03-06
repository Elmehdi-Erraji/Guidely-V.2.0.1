package com.spring.guidely.web.vm.mapers;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.web.vm.users.UserCreateRequestVM;
import com.spring.guidely.web.vm.users.UserResponseVM;
import com.spring.guidely.web.vm.users.UserUpdateRequestVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserVMMapper {

    // Map from create request VM to AppUser entity.
    // We assume that role and department will be set separately in the service.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "department", ignore = true)
    AppUser toEntity(UserCreateRequestVM vm);

    // Map from update request VM to AppUser entity.
    // This is similar to create, but the password might be null if not updating.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateEntity(UserUpdateRequestVM vm, @MappingTarget AppUser user);

    // Map AppUser entity to response VM.
    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "department.name", target = "departmentName")
    UserResponseVM toResponse(AppUser user);
}