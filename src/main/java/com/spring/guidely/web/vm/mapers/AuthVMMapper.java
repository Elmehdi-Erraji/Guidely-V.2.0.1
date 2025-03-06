package com.spring.guidely.web.vm.mapers;


import com.spring.guidely.entities.AppUser;
import com.spring.guidely.web.vm.auth.RegisterRequestVM;
import com.spring.guidely.web.vm.auth.RegisterResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthVMMapper {

    // Map RegisterRequestVM to AppUser, ignoring id and role (set later)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    AppUser toEntity(RegisterRequestVM vm);

    // Map AppUser to RegisterResponseVM; map role.name to role field.
    @Mapping(source = "role.name", target = "role")
    RegisterResponseVM toResponse(AppUser appUser);
}
