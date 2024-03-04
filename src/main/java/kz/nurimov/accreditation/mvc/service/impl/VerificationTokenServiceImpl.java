package kz.nurimov.accreditation.mvc.service.impl;

import jakarta.persistence.EntityNotFoundException;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.mapper.UserMapper;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import kz.nurimov.accreditation.mvc.repository.UserRepository;
import kz.nurimov.accreditation.mvc.repository.VerificationTokenRepository;
import kz.nurimov.accreditation.mvc.service.RoleService;
import kz.nurimov.accreditation.mvc.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class VerificationTokenServiceImpl implements VerificationTokenService {

    private final VerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public VerificationTokenServiceImpl(VerificationTokenRepository tokenRepository, UserRepository userRepository, UserMapper userMapper) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    @Override
    public String validateToken(String token) {
        Optional<VerificationToken> verificationToken = tokenRepository.findByToken(token);

        VerificationToken myToken = verificationToken.get();

        if (!verificationToken.isPresent()) return "Invalid";
        if (myToken.getExpirationTime().before(new Date())) return "Expired";

        User user = myToken.getUser();
        user.setEnabled(true);
        userRepository.save(user);
        return "Valid";
    }

    @Transactional
    @Override
    public void saveVerificationTokenForUser(UserDTO userDTO, String token) {
        User user = userMapper.userDTOToUser(userDTO);
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public VerificationToken findByToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException("findByToken method error: token can not find"));
    }
}
