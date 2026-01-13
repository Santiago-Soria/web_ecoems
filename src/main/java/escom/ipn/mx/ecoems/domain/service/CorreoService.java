package escom.ipn.mx.ecoems.domain.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;

@Service
public class CorreoService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remitente;

    // 1. Método simple (solo texto)
    public void enviarCorreoSimple(String destinatario, String asunto, String contenido) {
        // Log de inicio para saber que el hilo arrancó
        System.out.println(">>> Intentando enviar correo a: " + destinatario);
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            helper.setFrom(remitente);
            helper.setTo(destinatario);
            helper.setSubject(asunto);
            helper.setText(contenido, true);

            mailSender.send(message);
            System.out.println("Correo enviado exitosamente a: " + destinatario);

        } catch (MessagingException e) {
            // Error al construir el mensaje (dirección mal formada, encoding, etc.)
            System.err.println("Error de Mensajería: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Error GENERAL (Autenticación fallida, timeout, bloqueo de Google, etc.)
            // Esto capturará MailAuthenticationException que antes se escapaba
            System.err.println("Error CRÍTICO al enviar correo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
