package ru.neoflex.neostudy.dossier.service.mail;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static jakarta.mail.internet.MimeBodyPart.INLINE;

/**
 * Класс для отправки пользователю электронных сообщений. Реализован по шаблону проектирования builder, отправка
 * сообщения возможна только после инициализации и построения объекта {@code Mail}.
 */
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Mail {
	private JavaMailSender mailSender;
	private MimeMessage mimeMessage;
	private Multipart multipart;
	private String toEmail;
	private String subject;
	private String message;
	public static final String FAILED_TO_SEND_EMAIL = "Failed to send email";
	
	/**
	 * Отправляет сообщение, сформированное с помощью {@code MailBuilder}.
	 * @throws SendingEmailException при ошибке в результате добавления email-контента к объекту сообщения электронной
	 * почты {@code MimeMessage}.
	 */
	public void sendMessage() throws SendingEmailException {
		try {
			mimeMessage.setContent(multipart);
			mailSender.send(mimeMessage);
			log.info("Email sent to {}, subject: {} message: {}", toEmail, subject, message);
		}
		catch (MessagingException e) {
			throw new SendingEmailException(FAILED_TO_SEND_EMAIL, e);
		}
	}
	
	/**
	 * Вложенный класс-builder для внешнего класса {@code Mail}. Объекты данного класса являются stateful, поэтому, для
	 * изоляции данных между разными письмами и обеспечения уникальности писем имеет scope = prototype.
	 */
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
		
		/**
		 * Инициализирует объект сообщения электронной почты {@code MimeMessage} и контейнер частей тела сообщения
		 * {@code MimeMultiPart}, заполняет минимально необходимые параметры для отправки письма с помощью
		 * вспомогательного класса {@code MimeMessageHelper}:
		 * <ul>
		 *     <li>Email отправителя.</li>
		 *     <li>Тема письма.</li>
		 *     <li>Email получателя.</li>
		 * </ul>
		 * @param toEmail электронная почта получателя.
		 * @param params {@code Map<String, Object>} с параметрами, используемыми в электронном письме.
		 * @return {@code this} {@code mailBuilder}.
		 * @throws SendingEmailException в случае ошибок при настройке электронного сообщения.
		 */
		public MailBuilder initializeMail(@NonNull String toEmail, Map<String, Object> params) throws SendingEmailException {
			this.toEmail = toEmail;
			this.subject = (String) params.get(ParamsService.KEY_SUBJECT);
			this.message = (String) params.get(ParamsService.KEY_MESSAGE_TEXT);
			mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(this.mimeMessage, "UTF-8");
			try {
				mimeMessageHelper.setFrom(fromEmail, bankOfficialName);
				mimeMessageHelper.setSubject(subject);
				mimeMessageHelper.setTo(toEmail);
			}
			catch (MessagingException e) {
				throw new SendingEmailException(FAILED_TO_SEND_EMAIL, e);
			}
			catch (UnsupportedEncodingException e) {
				throw new SendingEmailException("Unsupported encoding: " + e.getMessage(), e);
			}
			this.multipart = new MimeMultipart();
			return this;
		}
		
		/**
		 * Создаёт и добавляет к контейнеру {@code Multipart} содержание письма в виде html-текста. Получает на вход
		 * путь к шаблону thymeleaf письма в формате {@code String} и карту {@code Map} с параметрами, создаёт
		 * html-текст в виде строки {@code String} и добавляет её в контейнер {@code Multipart}.
		 * @param template относительный путь к шаблону thymeleaf в формате {@code String}.
		 * @param params {@code Map<String, Object>} с параметрами, используемыми для формирования элемента письма html.
		 * @return {@code this} {@code mailBuilder}.
		 * @throws SendingEmailException в случае некорректного содержимого добавляемой части или ошибки при добавлении
		 * части письма.
		 */
		public MailBuilder addHtmlPart(@NonNull String template, Map<String, Object> params) throws SendingEmailException {
			String content = getContent(template, params);
			MimeBodyPart htmlPart = new MimeBodyPart();
			try {
				htmlPart.setContent(content, "text/html; charset=UTF-8");
				this.multipart.addBodyPart(htmlPart);
			}
			catch (MessagingException e) {
				throw new SendingEmailException(FAILED_TO_SEND_EMAIL, e);
			}
			return this;
		}
		
		/**
		 * Создаёт объект части тела письма {@code MimeBodyPart}, добавляет в него файл изображения, считанный по
		 * переданному в параметрах метода адресу {@code pathToImg}, назначает ему переданный идентификатор контента
		 * {@code contentId}. Добавляет сформированный объект с изображением в контейнер {@code Multipart}.
		 * @param pathToImg путь к изображению в формате {@code String}.
		 * @param contentId идентификатор изображения в формате {@code String}.
		 * @return {@code this} {@code mailBuilder}.
		 * @throws InternalMicroserviceException в следующих случаях:
		 * <ul>
		 *     <li>Файл не найден.</li>
		 *     <li>Указан некорректный путь к файлу.</li>
		 *     <li>Ошибка при чтении файла.</li>
		 *     <li>Ошибка при настройке вложения.</li>
		 * </ul>
		 */
		public MailBuilder addImgPart(String pathToImg, String contentId) throws InternalMicroserviceException {
			MimeBodyPart imagePart = new MimeBodyPart();
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			try (InputStream imageStream = loader.getResourceAsStream(pathToImg)) {
				if(imageStream == null) {
					throw new InternalMicroserviceException("Img not found");
				}
				try (BufferedInputStream bufferedImageStream = new BufferedInputStream(imageStream, 2048)) {
					byte[] imageBytes = bufferedImageStream.readAllBytes();
					DataSource dataSource = new ByteArrayDataSource(imageBytes, "image/png");
					imagePart.setDataHandler(new DataHandler(dataSource));
					imagePart.setContentID(contentId);
					imagePart.setDisposition(INLINE);
					multipart.addBodyPart(imagePart);
				}
			}
			catch (IOException e) {
				throw new InternalMicroserviceException("Error loading image", e);
			}
			catch (MessagingException e) {
				throw new SendingEmailException(FAILED_TO_SEND_EMAIL, e);
			}
			return this;
		}
		
		/**
		 * Создаёт объект части тела письма {@code MimeBodyPart}, декодирует и добавляет в него переданный в параметрах
		 * метода документ {@code documentAsString}, назначает имя вложения. Добавляет сформированный объект с
		 * вложением в контейнер {@code Multipart}.
		 * @param documentAsString прикрепляемый документ, закодированный в строку по Base64.
		 * @param fileName имя вложения в письме.
		 * @return {@code this} {@code mailBuilder}.
		 * @throws SendingEmailException в случае некорректного содержимого добавляемой части или ошибки при добавлении
		 * части письма.
		 * @throws UserDocumentException в случае, если файл повреждён, имеет некорректную кодировку Base64, или равен
		 * {@code null}.
		 */
		public MailBuilder addAttachmentPart(String documentAsString, String fileName) throws SendingEmailException, UserDocumentException {
			try {
				Objects.requireNonNull(documentAsString);
				byte[] documentAsBytes = Base64.getDecoder().decode(documentAsString);
				ByteArrayDataSource dataSource = new ByteArrayDataSource(documentAsBytes, "application/octet-stream");
				MimeBodyPart attachmentBodyPart = new MimeBodyPart();
				attachmentBodyPart.setDataHandler(new DataHandler(dataSource));
				attachmentBodyPart.setFileName(fileName);
				multipart.addBodyPart(attachmentBodyPart);
			}
			catch (NullPointerException | IllegalArgumentException e) {
				throw new UserDocumentException("Document is corrupted or missing.", e);
			}
			catch (MessagingException e) {
				throw new SendingEmailException(FAILED_TO_SEND_EMAIL, e);
			}
			return this;
		}
		
		/**
		 * Возвращает объект, построенный с помощью {@code MailBuilder}.
		 * @return построенный объект {@code Mail}.
		 */
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
		
		/**
		 * Обрабатывает шаблон thymeleaf с использованием параметров. Путь к шаблону и {@code Map} с параметрами
		 * передаются в метод в качестве параметров метода. Возвращает результат обработки указанного шаблона в виде
		 * строки {@code String}.
		 * @param template относительный путь к файлу шаблона thymeleaf.
		 * @param params {@code Map<String, Object>} с параметрами, используемыми для формирования элемента письма html.
		 * @return строка {@code String}, содержащая результат оценки указанного шаблона с предоставленным контекстом.
		 */
		private String getContent(String template, Map<String, Object> params) {
			Context context = new Context(LOCALE_RU, params);
			return templateEngine.process(template, context);
		}
	}
}
