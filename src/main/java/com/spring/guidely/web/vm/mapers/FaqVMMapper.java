package com.spring.guidely.web.vm.mapers;

import com.spring.guidely.entities.AppUser;
import com.spring.guidely.entities.Category;
import com.spring.guidely.entities.Faq;
import com.spring.guidely.web.vm.faq.FaqCreateRequestVM;
import com.spring.guidely.web.vm.faq.FaqResponseVM;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FaqVMMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdBy", source = "createdById", qualifiedByName = "mapToAppUser")
    @Mapping(target = "category", source = "categoryId", qualifiedByName = "mapToCategory")
    Faq toEntity(FaqCreateRequestVM vm);

    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "category.id", target = "categoryId")
    FaqResponseVM toResponse(Faq faq);

    @Named("mapToAppUser")
    default AppUser mapToAppUser(java.util.UUID id) {
        if (id == null) {
            return null;
        }
        AppUser user = new AppUser();
        user.setId(id);
        return user;
    }

    @Named("mapToCategory")
    default Category mapToCategory(java.util.UUID id) {
        if (id == null) {
            return null;
        }
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
