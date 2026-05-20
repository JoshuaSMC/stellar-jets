package com.stellarjets.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Async
    public void sendWelcomeEmail(String toEmail, String firstName, String lastName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(toEmail);
            helper.setSubject("¡Bienvenido a Stellar Jets, " + firstName + "!");
            helper.setText(buildEmailBody(firstName, lastName, toEmail), true);

            mailSender.send(message);
            log.info("Welcome email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage());
        }
    }

    private String buildEmailBody(String firstName, String lastName, String email) {
        return """
            <!DOCTYPE html>
            <html lang="es">
            <head><meta charset="UTF-8"></head>
            <body style="margin:0;padding:0;background:#F4F7FB;font-family:Arial,sans-serif;">
              <table width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding:40px 20px;">
                  <table width="560" cellpadding="0" cellspacing="0" style="background:#ffffff;border-radius:16px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">

                    <!-- Header -->
                    <tr>
                      <td style="background:#060E1A;padding:36px 40px;text-align:center;">
                        <p style="margin:0 0 8px;font-size:10px;letter-spacing:4px;color:rgba(255,255,255,0.4);text-transform:uppercase;">Bienvenido a</p>
                        <h1 style="margin:0;font-size:22px;letter-spacing:6px;color:#D4AF37;font-family:Georgia,serif;">STELLAR JETS</h1>
                        <p style="margin:10px 0 0;font-size:13px;color:rgba(255,255,255,0.5);">Premium Aviation</p>
                      </td>
                    </tr>

                    <!-- Body -->
                    <tr>
                      <td style="padding:40px 40px 32px;">
                        <p style="margin:0 0 16px;font-size:22px;font-weight:700;color:#0A1428;">¡Tu registro fue exitoso!</p>
                        <p style="margin:0 0 24px;font-size:15px;color:#4B5563;line-height:1.6;">
                          Hola <strong>%s %s</strong>, ya sos parte de Stellar Jets.<br>
                          Tu cuenta fue creada con el correo <strong>%s</strong>.
                        </p>

                        <!-- Info box -->
                        <table width="100%%" cellpadding="0" cellspacing="0" style="background:#F4F7FB;border-radius:12px;margin:0 0 28px;">
                          <tr>
                            <td style="padding:20px 24px;">
                              <p style="margin:0 0 8px;font-size:11px;text-transform:uppercase;letter-spacing:2px;color:#9CA3AF;">Tu información</p>
                              <p style="margin:0 0 4px;font-size:14px;color:#1F2937;"><strong>Nombre:</strong> %s %s</p>
                              <p style="margin:0;font-size:14px;color:#1F2937;"><strong>Email:</strong> %s</p>
                            </td>
                          </tr>
                        </table>

                        <!-- CTA -->
                        <table cellpadding="0" cellspacing="0">
                          <tr>
                            <td style="background:#D4AF37;border-radius:10px;">
                              <a href="%s/login" style="display:inline-block;padding:14px 32px;font-size:14px;font-weight:700;color:#060E1A;text-decoration:none;letter-spacing:0.5px;">
                                Iniciar sesión →
                              </a>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>

                    <!-- Footer -->
                    <tr>
                      <td style="background:#F9FAFB;padding:20px 40px;border-top:1px solid #F3F4F6;text-align:center;">
                        <p style="margin:0;font-size:12px;color:#9CA3AF;">
                          Si no creaste esta cuenta, podés ignorar este correo.<br>
                          © 2026 Stellar Jets · Premium Aviation
                        </p>
                      </td>
                    </tr>

                  </table>
                </td></tr>
              </table>
            </body>
            </html>
            """.formatted(firstName, lastName, email, firstName, lastName, email, frontendUrl);
    }
}
