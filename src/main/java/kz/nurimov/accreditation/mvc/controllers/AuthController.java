package kz.nurimov.accreditation.mvc.controllers;

import jakarta.mail.MessagingException;
import jakarta.persistence.PostRemove;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import kz.nurimov.accreditation.mvc.dto.RegistrationDTO;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.event.RegistrationCompleteEvent;
import kz.nurimov.accreditation.mvc.event.listener.RegistrationCompleteEventListener;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.models.VerificationToken;
import kz.nurimov.accreditation.mvc.service.PasswordResetTokenService;
import kz.nurimov.accreditation.mvc.service.UserService;
import kz.nurimov.accreditation.mvc.service.VerificationTokenService;
import kz.nurimov.accreditation.mvc.util.UrlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {
    private final UserService userService;
    private final ApplicationEventPublisher publisher;
    private final VerificationTokenService verificationTokenService;
    private final PasswordResetTokenService passwordResetTokenService;
    private final RegistrationCompleteEventListener eventListener;

    @Autowired
    public AuthController(UserService userService, ApplicationEventPublisher publisher, VerificationTokenService verificationTokenService, PasswordResetTokenService passwordResetTokenService, RegistrationCompleteEventListener eventListener) {
        this.userService = userService;
        this.publisher = publisher;
        this.verificationTokenService = verificationTokenService;
        this.passwordResetTokenService = passwordResetTokenService;
        this.eventListener = eventListener;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
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

    @GetMapping("registration/forgot-password")
    public String forgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("registration/forgot-password")
    public String resetPassword(HttpServletRequest request, Model model) {
        String email = request.getParameter("email");
        UserDTO user = userService.findByEmail(email);
        if (user == null) {
            return "redirect:/registration/forgot-password?not_found";
        }
        String passwordResetToken = UUID.randomUUID().toString();
        passwordResetTokenService.createPasswordResetTokenForUser(user, passwordResetToken);
        String url = UrlUtil.getApplicationUrl(request) + "/registration/reset-password?token="+passwordResetToken;
        try {
            eventListener.sendPasswordResetVerificationEmail(url, user);
        } catch (MessagingException | UnsupportedEncodingException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/registration/forgot-password?success";
    }

    @GetMapping("/registration/reset-password")
    public String passwordResetForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "auth/password-reset";
    }

    @PostMapping("/registration/reset-password")
    public String resetPasswordHandler(HttpServletRequest request) {
        String theToken = request.getParameter("token");
        String password = request.getParameter("password");
        String tokenVerificationResult = passwordResetTokenService.validatePasswordResetToken(theToken);

        if (!tokenVerificationResult.equalsIgnoreCase("valid")) {
            return "redirect:/error?invalid_token";
        }
        UserDTO theUser =  passwordResetTokenService.findUserByPasswordResetToken(theToken);
        if (theUser != null) {
            passwordResetTokenService.resetPassword(theUser, password);
            return "redirect:/login?reset_success";
        }
        return "redirect:/error?not_found";
    }






}
