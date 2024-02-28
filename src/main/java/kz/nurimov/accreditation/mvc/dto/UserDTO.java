package kz.nurimov.accreditation.mvc.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.NaturalId;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotEmpty(message = "Firstname cannot be empty")
    private String firstname;

    @NotEmpty(message = "Lastname cannot be empty")
    private String lastname;

    @Email(message = "Email should be valid")
    private String email;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotEmpty(message = "Role cannot be empty")
    private List<String> roleNames;
}
