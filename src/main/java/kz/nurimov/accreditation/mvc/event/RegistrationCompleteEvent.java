package kz.nurimov.accreditation.mvc.event;

import kz.nurimov.accreditation.mvc.dto.UserDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private UserDTO user;
    private String confirmationUrl;

    public RegistrationCompleteEvent(UserDTO user, String confirmationUrl) {
        super(user);
        this.user = user;
        this.confirmationUrl = confirmationUrl;
    }

}
