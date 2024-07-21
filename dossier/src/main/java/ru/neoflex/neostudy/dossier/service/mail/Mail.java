package ru.neoflex.neostudy.dossier.service.mail;

import jakarta.activation.DataHandler;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.SendingEmailException;
import ru.neoflex.neostudy.common.exception.UserDocumentException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mail {
	private JavaMailSender mailSender;
	private MimeMessage mimeMessage;
	private Multipart multipart;
	private String toEmail;
	private String subject;
	private String message;
	
	public void sendMessage() throws SendingEmailException {
		try {
			mimeMessage.setContent(multipart);
			mailSender.send(mimeMessage);
			log.info("Email sent to {}, subject: {} message: {}", toEmail, subject, message);
		}
		catch (MessagingException e) {
			throw new SendingEmailException("Failed to send email", e);
		}
	}
	
	@Service
	@Scope("prototype")
	@RequiredArgsConstructor
	public static class MailBuilder {
		private final JavaMailSender mailSender;
		private final TemplateEngine templateEngine;
		
		@Value("${spring.mail.username}")
		private String fromEmail;
		@Value("${app.bank.official-name}")
		private String bankOfficialName;
		private static final Locale LOCALE_RU = Locale.forLanguageTag("ru");
		
		private Multipart multipart;
		private MimeMessage mimeMessage;
		private String toEmail;
		private String subject;
		private String message;
		
		public MailBuilder initializeMail(@NonNull String toEmail, Map<String, Object> params) throws SendingEmailException {
			this.toEmail = toEmail;
			subject = (String) params.get(ParamsService.KEY_SUBJECT);
			this.message = (String) params.get(ParamsService.KEY_MESSAGE_TEXT);
			mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
			try {
				message.setFrom(fromEmail, bankOfficialName);
				message.setSubject(subject);
				message.setTo(toEmail);
			}
			catch (MessagingException e) {
				throw new SendingEmailException("Failed to send email", e);
			}
			catch (UnsupportedEncodingException e) {
				throw new SendingEmailException("Unsupported encoding: " + e.getMessage(), e);
			}
			multipart = new MimeMultipart();
			return this;
		}
		
		public MailBuilder addHtmlPart(@NonNull String template, Map<String, Object> params) throws SendingEmailException {
			String content = getContent(template, params);
			MimeBodyPart htmlPart = new MimeBodyPart();
			try {
				htmlPart.setContent(content, "text/html; charset=UTF-8");
				multipart.addBodyPart(htmlPart);
			}
			catch (MessagingException e) {
				throw new SendingEmailException("Failed to send email", e);
			}
			return this;
		}
		
		public MailBuilder addImgPart() throws InternalMicroserviceException {
			MimeBodyPart imagePart = new MimeBodyPart();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource("static/img/logo.png");
			try {
				File img = new File(Objects.requireNonNull(url).toURI());
				imagePart.attachFile(img);
				imagePart.setContentID("<logo@dossier>");
				imagePart.setDisposition(MimeBodyPart.INLINE);
				multipart.addBodyPart(imagePart);
			}
			catch (URISyntaxException | IOException | NullPointerException e) {
				throw new InternalMicroserviceException("Mail img not found", e);
			}
			catch (MessagingException e) {
				throw new SendingEmailException("Failed to send email", e);
			}
			return this;
		}
		
		public MailBuilder addAttachmentPart(String documentAsString) throws SendingEmailException, UserDocumentException {
			try {
				byte[] documentAsBytes = Base64.getDecoder().decode(documentAsString);
				ByteArrayDataSource dataSource = new ByteArrayDataSource(documentAsBytes, "application/octet-stream");
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
				attachmentBodyPart.setFileName("document.pdf");
				multipart.addBodyPart(attachmentBodyPart);
			}
			catch (NullPointerException | IllegalArgumentException e) {
				throw new UserDocumentException("Document is corrupted or missing.", e);
			}
			catch (MessagingException e) {
				throw new SendingEmailException("Failed to send email", e);
			}
			return this;
		}
		
		public Mail build() {
			Mail mail = new Mail();
			mail.mailSender = mailSender;
			mail.mimeMessage = mimeMessage;
			mail.multipart = multipart;
			mail.toEmail = toEmail;
			mail.subject = subject;
			mail.message = message;
			return mail;
		}
		private String getContent(String template, Map<String, Object> params) {
			Context context = new Context(LOCALE_RU, params);
			return templateEngine.process(template, context);
		}
		
	}
	
	
}
