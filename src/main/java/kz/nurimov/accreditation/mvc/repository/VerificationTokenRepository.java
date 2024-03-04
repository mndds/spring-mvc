package kz.nurimov.accreditation.mvc.repository;

import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByToken(String token);

}
