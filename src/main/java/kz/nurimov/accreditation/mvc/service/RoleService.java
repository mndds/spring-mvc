package kz.nurimov.accreditation.mvc.service;

import kz.nurimov.accreditation.mvc.models.Role;

import java.util.Optional;

public interface RoleService {
    Role findByName(String name);
}
