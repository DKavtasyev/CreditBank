package ru.neoflex.neostudy.dossier.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Log4j2
public class MailService {
	private final JavaMailSender mailSender;
	private final TemplateEngine templateEngine;
	
	@Value("${spring.mail.username}")
	private String email;
	private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");
	public void sendAdvancedEmail(@NonNull String toEmail, String template, Map<String, Object> params) {
		try {
			String subject = (String) params.get("subject");
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			message.setFrom(email, "CreditBank");
			String content = getContent(template, params);
			message.setText(content, true);
			message.setSubject(subject);
			message.setTo(toEmail);
			mailSender.send(mimeMessage);
			log.info("Email sent to {}, message: {}", toEmail, params.get("messageText"));
			
			/**/
		}
		catch (MessagingException | UnsupportedEncodingException e) {
			log.error("Sending email to {} failed: \n{}", toEmail, e.getMessage());
		}
	}
	
	private String getContent(String template, Map<String, Object> params) {
		Context context = new Context(LOCALE_RU, params);
		return templateEngine.process(template, context);
	}
}
