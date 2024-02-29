package kz.nurimov.accreditation.mvc.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.event.RegistrationCompleteEvent;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import kz.nurimov.accreditation.mvc.service.UserService;
import kz.nurimov.accreditation.mvc.service.VerificationTokenService;
import kz.nurimov.accreditation.mvc.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenService verificationTokenService;

    @Autowired
    public AuthController(UserService userService, ApplicationEventPublisher publisher, VerificationTokenService verificationTokenService) {
        this.userService = userService;
        this.publisher = publisher;
        this.verificationTokenService = verificationTokenService;
    }

    @GetMapping("/registration")
    public String showRegistrationForm(@ModelAttribute("user") RegistrationDTO registrationDTO) {
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registerUser(@ModelAttribute("user") @Valid RegistrationDTO registrationDTO,
                               BindingResult bindingResult,
                               HttpServletRequest request,
                               Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("user", registrationDTO);
            return "auth/registration";
        }

        UserDTO user = userService.registerUser(registrationDTO);

        publisher.publishEvent(new RegistrationCompleteEvent(user, UrlUtil.getApplicationUrl(request)));
        return "redirect:/registration?success";
    }

    @GetMapping("/registration/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token) {
        VerificationToken theToken = verificationTokenService.findByToken(token);

        if (theToken.getUser().isEnabled()) {
            return "redirect:/login?verified";
        }

        String verificationResult = verificationTokenService.validateToken(String.valueOf(token));
        return switch (verificationResult.toLowerCase()) {
            case "expired" -> "redirect:/error?expired";
            case "invalid" -> "redirect:/error?invalid";
            case "valid" -> "redirect:/login?valid";
            default -> "redirect:/error?invalid";
        };

    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }



}
