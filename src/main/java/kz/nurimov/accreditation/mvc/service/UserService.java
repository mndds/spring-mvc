package kz.nurimov.accreditation.mvc.service;

import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO findUserById(Long userId);
    UserDTO registerUser(RegistrationDTO registrationDTO);
    UserDTO findByEmail(String email);
    boolean isUserExists(RegistrationDTO registrationDTO);
}
