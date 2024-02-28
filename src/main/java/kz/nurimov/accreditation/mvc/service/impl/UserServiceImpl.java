package kz.nurimov.accreditation.mvc.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.mapper.UserMapper;
import kz.nurimov.accreditation.mvc.models.Role;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.repository.RoleRepository;
import kz.nurimov.accreditation.mvc.repository.UserRepository;
import kz.nurimov.accreditation.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    // Исправить все исключения
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper.INSTANCE::userToUserDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO findUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    @Override
    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User nor found"));

        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    @Transactional
    @Override
    public UserDTO registerUser(RegistrationDTO registrationDTO) {

        if (userRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            //throw new UserAlreadyExistAuthenticationException
            throw new EntityNotFoundException();
        }

        User user = UserMapper.INSTANCE.registrationDTOToUser(registrationDTO);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new EntityNotFoundException("Not found user role while creating"));
        user.setRoles(Collections.singletonList(role));

        User savedUser = userRepository.save(user);
        return UserMapper.INSTANCE.userToUserDTO(savedUser);
    }

    @Override
    public boolean isUserExists(RegistrationDTO registrationDTO) {
        User user = userRepository.findByEmail(registrationDTO.getEmail()).orElse(null);
        return user != null && user.getEmail() != null && !user.getEmail().isEmpty();
    }


}
