package com.financial.app.infrastructure.adapters.out.email;

import com.financial.app.application.ports.out.EmailPort;
import com.resend.*;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ResendEmailAdapter implements EmailPort {

    private final Resend resend;
    private final String fromEmail;
    private final String frontendUrl;

    public ResendEmailAdapter(
            @Value("${RESEND_API_KEY:re_default}") String apiKey,
            @Value("${RESEND_FROM_EMAIL:onboarding@resend.dev}") String fromEmail,
            @Value("${FRONTEND_URL:http://localhost:3000}") String frontendUrl) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordReset(String toEmail, String resetToken) {
        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;
        
        String htmlBody = String.format("""
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="margin: 0; padding: 0; background-color: #0F1115; font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;">
                <table width="100%%" border="0" cellspacing="0" cellpadding="0" style="background-color: #0F1115; padding: 40px 20px;">
                    <tr>
                        <td align="center">
                            <table width="100%%" max-width="600" border="0" cellspacing="0" cellpadding="0" style="max-width: 600px; background-color: #17191E; border-radius: 24px; overflow: hidden; border: 1px solid #26292F;">
                                <!-- Header -->
                                <tr>
                                    <td style="padding: 40px 32px; text-align: center;">
                                        <div style="display: inline-block; background-color: #FF6B00; width: 48px; height: 48px; border-radius: 12px; margin-bottom: 16px;">
                                            <span style="color: white; font-size: 24px; line-height: 48px; font-weight: bold;">F</span>
                                        </div>
                                        <h1 style="color: #ffffff; margin: 0; font-size: 24px; font-weight: 700; letter-spacing: -0.5px;">FinStreak</h1>
                                    </td>
                                </tr>
                                
                                <!-- Corpo do Email -->
                                <tr>
                                    <td style="padding: 0 40px 40px 40px;">
                                        <h2 style="color: #ffffff; margin: 0 0 16px 0; font-size: 20px; font-weight: 600;">Recuperação de Senha</h2>
                                        <p style="color: #9CA3AF; line-height: 1.6; margin: 0 0 32px 0; font-size: 16px;">
                                            Olá! Recebemos uma solicitação para redefinir a sua senha no <strong>FinStreak</strong>. 
                                            Clique no botão abaixo para escolher uma nova credencial.
                                        </p>
                                        
                                        <!-- Botão de Ação -->
                                        <div style="text-align: center; margin: 32px 0;">
                                            <a href="%s" style="background-color: #FF6B00; color: #ffffff; padding: 18px 36px; text-decoration: none; border-radius: 16px; font-weight: 700; font-size: 16px; display: inline-block;">
                                                Redefinir Senha
                                            </a>
                                        </div>
                                        
                                        <div style="background-color: #1C1F26; border-radius: 16px; padding: 20px; border: 1px solid #26292F;">
                                            <p style="color: #9CA3AF; font-size: 14px; line-height: 1.5; margin: 0;">
                                                <strong>Segurança:</strong> Este link é válido por <strong>15 minutos</strong>. 
                                                Se não foi você quem solicitou, ignore este e-mail. Sua senha atual permanecerá segura.
                                            </p>
                                        </div>
                                    </td>
                                </tr>
                                
                                <!-- Rodapé -->
                                <tr>
                                    <td style="padding: 32px; text-align: center; border-top: 1px solid #26292F;">
                                        <p style="color: #6B7280; font-size: 12px; margin: 0;">
                                            FinStreak App &bull; Domine suas finanças.
                                        </p>
                                        <p style="color: #4B5563; font-size: 11px; margin: 8px 0 0 0;">
                                            Você recebeu este e-mail porque uma solicitação de redefinição de senha foi feita para sua conta.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, resetLink);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(fromEmail)
                .to(toEmail)
                .subject("🔐 Redefinição de Senha - FinStreak")
                .html(htmlBody)
                .build();

        try {
            CreateEmailResponse data = resend.emails().send(params);
            log.info("Password reset email sent successfully to: {}. ID: {}", toEmail, data.getId());
        } catch (ResendException e) {
            log.error("Failed to send password reset email to: {}. Error: {}", toEmail, e.getMessage());
        }
    }
}
