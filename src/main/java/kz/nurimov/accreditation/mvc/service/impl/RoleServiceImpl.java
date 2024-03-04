package kz.nurimov.accreditation.mvc.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kz.nurimov.accreditation.mvc.models.Role;
import kz.nurimov.accreditation.mvc.repository.RoleRepository;
import kz.nurimov.accreditation.mvc.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
}
