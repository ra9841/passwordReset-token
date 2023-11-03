package com.rabin.securityproject.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtils {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendTokenEmail(String email, String emailVerificationsToken) throws MessagingException {
        String verifyEmailUrl = "http://localhost:8080/security/verifyEmail?token=" + emailVerificationsToken;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Verify email");

        String emailContent = """
        <div>
            <p>Thank you for registering with us.</p>
            <p>Please, follow the link below to complete your registration.</p>
            <a href="%s">Verify your email to activate your account</a>
            <p> Thank you <br> Users Registration Portal Service</p>
        </div>
    """.formatted(verifyEmailUrl);  //We use String.format to insert the verifyEmailUrl into the HTML content.

        mimeMessageHelper.setText(emailContent, true);

        javaMailSender.send(mimeMessage);
    }


    public void sendTokenForPasswordReset(String email, String emailVerificationsToken) throws MessagingException {
        String verifyEmailUrl = "http://localhost:8080/security/password-reset?token=" + emailVerificationsToken;

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("Set Password");

        String emailContent = """
        <div>
            <p>Thank you for registering with us.</p>
            <p>Please, follow the link below to complete your password reset.</p>
            <a href="%s">Click link to set password</a>
            <p> Thank you <br> Users Registration Portal Service</p>
        </div>
    """.formatted(verifyEmailUrl);  //We use String.format to insert the verifyEmailUrl into the HTML content.

        mimeMessageHelper.setText(emailContent, true);

        javaMailSender.send(mimeMessage);
    }

}
