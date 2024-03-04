package kz.nurimov.accreditation.mvc.event.listener;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kz.nurimov.accreditation.mvc.dto.UserDTO;
import kz.nurimov.accreditation.mvc.event.RegistrationCompleteEvent;
import kz.nurimov.accreditation.mvc.models.User;
import kz.nurimov.accreditation.mvc.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import kz.nurimov.accreditation.mvc.models.User;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class RegistrationCompleteEventListener implements ApplicationListener<RegistrationCompleteEvent>{
    private final VerificationTokenService tokenService;
    private final JavaMailSender mailSender;
    private UserDTO user;

    @Value("${spring.mail.username}")
    private String mailUsername;

    @Autowired
    public RegistrationCompleteEventListener(VerificationTokenService tokenService, JavaMailSender mailSender) {
        this.tokenService = tokenService;
        this.mailSender = mailSender;
    }

    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
        user = event.getUser();
        String vToken = UUID.randomUUID().toString();
        tokenService.saveVerificationTokenForUser(user,vToken);

        String url = event.getConfirmationUrl()+"/registration/verifyEmail?token="+vToken;

        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject = "Email Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p> Hi, " + user.getFirstname() + ", </p>"+
                "<p>Thank you for registering with us, " +
                "Please, follow the link below to complete your registration. </p>" +
                "<a href=\"" + url + "\"> Verify your email to activate your account</a>" +
                "<p> Thank you <br>";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    public void sendPasswordResetVerificationEmail(String url, UserDTO user) throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request Verification";
        String senderName = "Users Verification Service";
        String mailContent = "<p> Hi, " + user.getFirstname() + ", </p>"+
                "<p><b>You recently requested to reset your password</b> " +
                "Please, follow the link below to complete your registration. </p>" +
                "<a href=\"" + url + "\"> Verify your email to activate your account</a>" +
                "<p> Thank you <br>";
        emailMessage(subject, senderName, mailContent, mailSender, user);
    }

    private void emailMessage(String subject, String senderName,
                              String mailContent, JavaMailSender mailSender, UserDTO user) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom(mailUsername, senderName);
        messageHelper.setTo(user.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        mailSender.send(message);
    }


}
