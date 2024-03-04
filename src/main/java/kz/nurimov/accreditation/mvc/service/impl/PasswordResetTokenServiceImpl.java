package kz.nurimov.accreditation.mvc.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.mapper.UserMapper;
import kz.nurimov.accreditation.mvc.models.PasswordResetToken;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import kz.nurimov.accreditation.mvc.repository.PasswordResetTokenRepository;
import kz.nurimov.accreditation.mvc.repository.UserRepository;
import kz.nurimov.accreditation.mvc.service.PasswordResetTokenService;
import kz.nurimov.accreditation.mvc.service.RoleService;
import kz.nurimov.accreditation.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public PasswordResetTokenServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String validatePasswordResetToken(String theToken) {
        Optional<PasswordResetToken> passwordResetToken = passwordResetTokenRepository.findByToken(theToken);

        if (!passwordResetToken.isPresent()) return "Invalid";

        PasswordResetToken myToken = passwordResetToken.get();
        if (myToken.getExpirationTime().before(new Date())) return "Expired";

        return "valid";
    }

    @Override
    public UserDTO findUserByPasswordResetToken(String theToken) {
        PasswordResetToken foundToken =  passwordResetTokenRepository.findByToken(theToken)
                .orElseThrow(() -> new EntityNotFoundException("Not found"));
        User user = foundToken.getUser();

        return UserMapper.INSTANCE.userToUserDTO(user);
    }

    @Transactional
    @Override
    public void resetPassword(UserDTO theUser, String password) {
        User userEntity = userRepository.findByEmail(theUser.getEmail())
                        .orElseThrow(() -> new EntityNotFoundException("Not found user"));
        userEntity.setPassword(passwordEncoder.encode(password));
        userEntity.setUpdatedAt(LocalDateTime.now());
        userRepository.save(userEntity);
    }

    @Transactional
    @Override
    public void createPasswordResetTokenForUser(UserDTO user, String passwordResetToken) {
        User userEntity = userRepository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + user.getEmail()));

        // Пытаемся найти существующий токен сброса пароля для пользователя
        Optional<PasswordResetToken> existingTokenOpt = passwordResetTokenRepository.findByUser(userEntity);

        // Если существующий токен найден, удаляем его
        existingTokenOpt.ifPresent(token -> passwordResetTokenRepository.deleteByUser(userEntity));

        // После удаления существующего токена (если он был) создаем новый токен сброса пароля
        PasswordResetToken resetToken = new PasswordResetToken(passwordResetToken, userEntity);

        // Сохраняем новый токен сброса пароля в базе данных
        passwordResetTokenRepository.save(resetToken);
    }
}
