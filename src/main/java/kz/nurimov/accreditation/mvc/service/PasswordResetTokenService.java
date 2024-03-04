package kz.nurimov.accreditation.mvc.service;

import kz.nurimov.accreditation.mvc.dto.UserDTO;

import java.util.Optional;

public interface PasswordResetTokenService {
    String validatePasswordResetToken(String theToken);

    UserDTO findUserByPasswordResetToken(String theToken);

    void resetPassword(UserDTO theUser, String password);

    void createPasswordResetTokenForUser(UserDTO user, String passwordResetToken);
}
