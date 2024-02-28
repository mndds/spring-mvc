package kz.nurimov.accreditation.mvc.controllers;

import jakarta.validation.Valid;
import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.event.RegistrationCompleteEvent;
import kz.nurimov.accreditation.mvc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AuthController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;

    @Autowired
    public AuthController(UserService userService, ApplicationEventPublisher publisher) {
        this.userService = userService;
        this.publisher = publisher;
    }

    @GetMapping("/registration")
    public String showRegistrationForm(@ModelAttribute("user") RegistrationDTO registrationDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("user") @Valid RegistrationDTO registrationDTO,
                               BindingResult bindingResult,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", registrationDTO);
            return "auth/registration";
        }

        UserDTO user = userService.registerUser(registrationDTO);
        // publish verification email event ?
        publisher.publishEvent(new RegistrationCompleteEvent(user, ""));
        return "redirect:/registration?success";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }



}
