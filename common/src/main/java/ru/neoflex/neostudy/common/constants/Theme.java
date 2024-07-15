package ru.neoflex.neostudy.common.constants;

import lombok.Getter;
import lombok.Setter;

public enum Theme {
	FINISH_REGISTRATION(new FinishRegistrationTopic()),
	CREATE_DOCUMENTS(new CreateDocumentsTopic()),
	SEND_DOCUMENTS(new SendDocumentsTopic()),
	SEND_SES(new SendSesTopic()),
	CREDIT_ISSUED(new CreditIssuedTopic()),
	STATEMENT_DENIED(new StatementDeniedTopic());
	
	private final Topic topic;
	
	@Getter
	@Setter
	private String sesCode;
	
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
	public String getPath() {
		return topic.getPath();
	}
	
	private interface Topic{
		String getValue();
		String getSubject();
		String getMessageText();
		String getButtonText();
		String getPath();
	}
	
	@Getter
	private static class FinishRegistrationTopic implements Topic {
		public static final String value = "finish-registration";
		public final String subject = "Завершение оформления кредита";
		public final String messageText = "Завершите оформление.";
		public final String buttonText = "Завершить оформление";
		public final String path = "https://ya.ru/search/?text=%D0%B7%D0%B0%D0%B2%D0%B5%D1%80%D1%88%D0%B8%D1%82%D1%8C+%D0%BE%D1%84%D0%BE%D1%80%D0%BC%D0%BB%D0%B5%D0%BD%D0%B8%D0%B5+%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82%D0%B0";
		@Override
		public String getValue() {
			return value;
		}
	}
	
	@Getter
	private static class CreateDocumentsTopic implements Topic {
		public static final String value = "create-documents";
		public final String subject = "Оформление документов";
		public final String messageText = "Перейти к оформлению документов.";
		public final String buttonText = "Сформировать документы";
		public final String path = "https://ya.ru/search/?text=%D1%81%D1%84%D0%BE%D1%80%D0%BC%D0%B8%D1%80%D0%BE%D0%B2%D0%B0%D1%82%D1%8C+%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B+%D0%BD%D0%B0+%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82";
//		public final String path = "/deal/document/{statementId}/send";
		
		@Override
		public String getValue() {
			return value;
		}
	}
	
	@Getter
	private static class SendDocumentsTopic implements Topic {
		public static final String value = "send-documents";
		public final String subject = "Документы на подпись";
		public final String messageText = "Ваши документы на кредит.";
		public final String buttonText = "Запрос на подписание документов";
		public final String path = "https://ya.ru/search/?text=%D0%B7%D0%B0%D0%BF%D1%80%D0%BE%D1%81%D0%B8%D1%82%D1%8C+%D0%BF%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%B0%D0%BD%D0%B8%D0%B5+%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D0%BE%D0%B2";
//		public final String path = "/deal/document/{statementId}/sign";
		
		@Override
		public String getValue() {
			return value;
		}
	}
	
	@Getter
	private static class SendSesTopic implements Topic {
		public static final String value = "send-ses";
		public final String subject = "Подписание документов";
		public final String messageText = "Для получения кредита необходимо подписать документы.";
		public final String buttonText = "Подписать документы";
		public final String path = "https://ya.ru/search/?text=%D0%BF%D0%BE%D0%B4%D0%BF%D0%B8%D1%81%D0%B0%D1%82%D1%8C+%D0%B4%D0%BE%D0%BA%D1%83%D0%BC%D0%B5%D0%BD%D1%82%D1%8B+%D0%BD%D0%B0+%D0%BA%D1%80%D0%B5%D0%B4%D0%B8%D1%82+%D1%81+sescode";
		
		@Override
		public String getValue() {
			return value;
		}
	}
	
	@Getter
	private static class CreditIssuedTopic implements Topic {
		public static final String value = "credit-issued";
		public final String subject = "Кредит оформлен";
		public final String messageText = "Кредит оформлен.";
		public final String buttonText = null;
		public final String path = null;
		
		@Override
		public String getValue() {
			return value;
		}
	}
	
	@Getter
	private static class StatementDeniedTopic implements Topic {
		public static final String value = "statement-denied";
		public final String subject = "Заявка отклонена";
		public final String messageText = "Заявка на получение кредита отклонена.";
		public final String buttonText = null;
		public final String path = null;
		
		@Override
		public String getValue() {
			return value;
		}
	}
}
