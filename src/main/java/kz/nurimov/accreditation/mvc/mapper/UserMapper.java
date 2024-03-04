package kz.nurimov.accreditation.mvc.mapper;

import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.models.Role;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.service.RoleService;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "roleNames", source = "roles", qualifiedByName = "rolesToRoleNames")
    @Mapping(target = "isEnabled", expression = "java(user.isEnabled())")
    UserDTO userToUserDTO(User user);

    @Mapping(target = "roles", ignore = true)
    User registrationDTOToUser(RegistrationDTO registrationDTO);

    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "password", ignore = true)
    User userDTOToUser(UserDTO userDto);

    @Named("rolesToRoleNames")
    static List<String> rolesToRoleNames(List<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
