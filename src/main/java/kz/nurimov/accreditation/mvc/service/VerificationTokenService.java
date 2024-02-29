package kz.nurimov.accreditation.mvc.service;

import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import kz.nurimov.accreditation.mvc.repository.VerificationTokenRepository;

public interface VerificationTokenService {
    String validateToken(String token);
    void saveVerificationTokenForUser(UserDTO userDTO, String token);
    VerificationToken findByToken(String token);
}
