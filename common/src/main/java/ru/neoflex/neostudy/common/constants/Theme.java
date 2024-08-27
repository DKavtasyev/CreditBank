package ru.neoflex.neostudy.common.constants;

import lombok.Getter;

import java.net.URI;

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
	public URI getPath() {
		return topic.getPath();
	}
	
	private interface Topic{
		String getValue();
		String getSubject();
		String getMessageText();
		String getButtonText();
		URI getPath();
	}
	
	private static class FinishRegistrationTopic implements Topic {
		private static final String VALUE = "finish-registration";
		private static final String SUBJECT = "Завершение оформления кредита";
		private static final String MESSAGE_TEXT = "Закончите ранее начатое оформление кредита.";
		private static final String BUTTON_TEXT = "Завершить оформление";
		private static final URI PATH = URI.create("https://ya.ru/search/?text=%D0%B7%D0%B0%D0%B2%D0%B5%D1%80%D1%88%D0%B8%D1%82%D1%8C+%D0%BE%D1%84%D0%BE%D1%80%D0%BC%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5+%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82%D0%B0");
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
		public URI getPath() {
			return PATH;
		}
	}
	
	@Getter
	private static class CreateDocumentsTopic implements Topic {
		public static final String VALUE = "create-documents";
		public static final String SUBJECT = "Оформление документов";
		public static final String MESSAGE_TEXT = "Перейти к оформлению документов.";
		public static final String BUTTON_TEXT = "Сформировать документы";
		public static final URI PATH = URI.create("https://ya.ru/search/?text=%D1%81%D1%84%D0%BE%D1%80%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D1%82%D1%8C+%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B+%D0%BD%D0%B0+%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82");
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
		public URI getPath() {
			return PATH;
		}
	}
	
	@Getter
	private static class SendDocumentsTopic implements Topic {
		public static final String VALUE = "send-documents";
		public static final String SUBJECT = "Документы на подпись";
		public static final String MESSAGE_TEXT = "Ваши документы на кредит.";
		public static final String BUTTON_TEXT = "Запрос на подписание документов";
		public static final URI PATH = URI.create("https://ya.ru/search/?text=%D0%B7%D0%B0%D0%BF%D1%80%D0%BE%D1%81%D0%B8%D1%82%D1%8C+%D0%BF%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%B0%D0%BD%D0%B8%D0%B5+%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%BE%D0%B2");
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
		public URI getPath() {
			return PATH;
		}
	}
	
	@Getter
	private static class SendSesTopic implements Topic {
		public static final String VALUE = "send-ses";
		public static final String SUBJECT = "Подписание документов";
		public static final String MESSAGE_TEXT = "Для получения кредита необходимо подписать документы.";
		public static final String BUTTON_TEXT = "Подписать документы";
		public static final URI PATH = URI.create("/deal/document/{statementId}/sign");
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
		public URI getPath() {
			return PATH;
		}
	}
	
	@Getter
	private static class CreditIssuedTopic implements Topic {
		public static final String VALUE = "credit-issued";
		public static final String SUBJECT = "Кредит оформлен";
		public static final String MESSAGE_TEXT = "Кредит оформлен.";
		public static final String BUTTON_TEXT = null;
		public static final URI PATH = null;
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
		public URI getPath() {
			return PATH;
		}
	}
	
	@Getter
	private static class StatementDeniedTopic implements Topic {
		public static final String VALUE = "statement-denied";
		public static final String SUBJECT = "Заявка отклонена";
		public static final String MESSAGE_TEXT = "Заявка на получение кредита отклонена.";
		public static final String BUTTON_TEXT = null;
		public static final URI PATH = null;
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
		public URI getPath() {
			return PATH;
		}
	}
}
