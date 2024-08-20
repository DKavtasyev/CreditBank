package ru.neoflex.neostudy.common.constants;

import lombok.Getter;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Значения тем, по которым необходимо направить письмо на почту Клиенту.
 */
public enum Theme {
	FINISH_REGISTRATION(new FinishRegistrationTopic()),
	CREATE_DOCUMENTS(new CreateDocumentsTopic()),
	SEND_DOCUMENTS(new SendDocumentsTopic()),
	SEND_SES(new SendSesTopic()),
	CREDIT_ISSUED(new CreditIssuedTopic()),
	STATEMENT_DENIED(new StatementDeniedTopic());
	
	private final Topic topic;
	
	Theme(Topic topic) {
		this.topic = topic;
	}
	
	public String getTopicName() {
		return topic.getValue();
	}
	public String getSubject() {
		return topic.getSubject();
	}
	public String getMessageText() {
		return topic.getMessageText();
	}
	public String getButtonText() {
		return topic.getButtonText();
	}
	public UriComponentsBuilder getPath() {
		return topic.getPath();
	}
	
	private abstract static class Topic{
		protected String value;
		protected String subject;
		protected String messageText;
		protected String buttonText;
		protected UriComponentsBuilder uriComponentsBuilder;
		
		String getValue(){
			return value;
		}
		String getSubject() {
			return subject;
		}
		String getMessageText() {
			return messageText;
		}
		String getButtonText() {
			return buttonText;
		}
		UriComponentsBuilder getPath() {
			return uriComponentsBuilder;
		}
	}
	
	private static final String HTTPS = "https";
	private static final String HOST = "ya.ru";
	private static final String PATH = "search";
	
	
	private static class FinishRegistrationTopic extends Topic {
		public FinishRegistrationTopic() {
			value = "finish-registration";
			subject = "Завершение оформления кредита";
			messageText = "Закончите ранее начатое оформление кредита.";
			buttonText = "Завершить оформление";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Завершить оформление кредита");
		}
	}
	
	@Getter
	private static class CreateDocumentsTopic extends Topic {
		public CreateDocumentsTopic() {
			value = "create-documents";
			subject = "Оформление документов";
			messageText = "Перейти к оформлению документов.";
			buttonText = "Сформировать документы";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Оформить документы на кредит");
		}
	}
	
	@Getter
	private static class SendDocumentsTopic extends Topic {
		public SendDocumentsTopic() {
			value = "send-documents";
			subject = "Документы на подпись";
			messageText = "Ваши документы на кредит.";
			buttonText = "Запрос на подписание документов";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Запросить подписание документов");
		}
	}
	
	@Getter
	private static class SendSesTopic extends Topic {
		public SendSesTopic() {
			value = "send-ses";
			subject = "Подписание документов";
			messageText = "Для получения кредита необходимо подписать документы.";
			buttonText = "Подписать документы";
			uriComponentsBuilder = UriComponentsBuilder.fromPath("/deal/document/{statementId}/sign");
		}
	}
	
	@Getter
	private static class CreditIssuedTopic extends Topic {
		public CreditIssuedTopic() {
			value = "credit-issued";
			subject = "Кредит оформлен";
			messageText = "Кредит оформлен.";
			buttonText = null;
			uriComponentsBuilder = null;
		}
	}
	
	@Getter
	private static class StatementDeniedTopic extends Topic {
		public StatementDeniedTopic() {
			value = "statement-denied";
			subject = "Заявка отклонена";
			messageText = "Заявка на получение кредита отклонена.";
			buttonText = null;
			uriComponentsBuilder = null;
		}
	}
}
