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
	
	private interface Topic{
		String getValue();
		String getSubject();
		String getMessageText();
		String getButtonText();
		UriComponentsBuilder getPath();
	}
	
	private static final String HTTPS = "https";
	private static final String HOST = "ya.ru";
	private static final String PATH = "search";
	
	
	private static class FinishRegistrationTopic implements Topic {
		private static final String VALUE = "finish-registration";
		private static final String SUBJECT = "Завершение оформления кредита";
		private static final String MESSAGE_TEXT = "Закончите ранее начатое оформление кредита.";
		private static final String BUTTON_TEXT = "Завершить оформление";
		private static final UriComponentsBuilder BUILDER = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Завершить оформление кредита");
		@Override
		public String getValue() {
			return VALUE;
		}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
	
	@Getter
	private static class CreateDocumentsTopic implements Topic {
		public static final String VALUE = "create-documents";
		public static final String SUBJECT = "Оформление документов";
		public static final String MESSAGE_TEXT = "Перейти к оформлению документов.";
		public static final String BUTTON_TEXT = "Сформировать документы";
		private static final UriComponentsBuilder BUILDER = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Оформить документы на кредит");
		@Override
		public String getValue() {
			return VALUE;
		}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
	
	@Getter
	private static class SendDocumentsTopic implements Topic {
		public static final String VALUE = "send-documents";
		public static final String SUBJECT = "Документы на подпись";
		public static final String MESSAGE_TEXT = "Ваши документы на кредит.";
		public static final String BUTTON_TEXT = "Запрос на подписание документов";
		private static final UriComponentsBuilder BUILDER = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Запросить подписание документов");
		@Override
		public String getValue() {
			return VALUE;
		}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
	
	@Getter
	private static class SendSesTopic implements Topic {
		public static final String VALUE = "send-ses";
		public static final String SUBJECT = "Подписание документов";
		public static final String MESSAGE_TEXT = "Для получения кредита необходимо подписать документы.";
		public static final String BUTTON_TEXT = "Подписать документы";
		public static final UriComponentsBuilder BUILDER = UriComponentsBuilder.fromPath("/deal/document/{statementId}/sign");
		@Override
		public String getValue() {
	return VALUE;
}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
	
	@Getter
	private static class CreditIssuedTopic implements Topic {
		public static final String VALUE = "credit-issued";
		public static final String SUBJECT = "Кредит оформлен";
		public static final String MESSAGE_TEXT = "Кредит оформлен.";
		public static final String BUTTON_TEXT = null;
		public static final UriComponentsBuilder BUILDER = null;
		@Override
		public String getValue() {
			return VALUE;
		}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
	
	@Getter
	private static class StatementDeniedTopic implements Topic {
		public static final String VALUE = "statement-denied";
		public static final String SUBJECT = "Заявка отклонена";
		public static final String MESSAGE_TEXT = "Заявка на получение кредита отклонена.";
		public static final String BUTTON_TEXT = null;
		public static final UriComponentsBuilder BUILDER = null;
		@Override
		public String getValue() {
			return VALUE;
		}
		@Override
		public String getSubject() {
			return SUBJECT;
		}
		@Override
		public String getMessageText() {
			return MESSAGE_TEXT;
		}
		@Override
		public String getButtonText() {
			return BUTTON_TEXT;
		}
		@Override
		public UriComponentsBuilder getPath() {
			return BUILDER;
		}
	}
}
