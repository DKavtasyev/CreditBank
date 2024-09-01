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
	STATEMENT_DENIED(new StatementDeniedTopic()),
	CLIENT_REJECTION(new ClientRejectionTopic());
	
	private final Topic topic;
	
	Theme(Topic topic) {
		this.topic = topic;
	}
	
	public String getTopicName() {
		return topic.getTopicName();
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
		protected String topicName;
		protected String subject;
		protected String messageText;
		protected String buttonText;
		protected UriComponentsBuilder uriComponentsBuilder;
		
		String getTopicName(){
			return topicName;
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
			topicName = "finish-registration";
			subject = "Ваша заявка предварительно одобрена — завершите оформление";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Благодарим Вас за обращение в CreditBank.<br>
					Рады сообщить, что Ваша заявка на кредит предварительно одобрена!</p>
					<p>Для завершения процесса оформления, пожалуйста, предоставьте нам полные данные о вашем работодателе и прописке. Вы можете отправить эти данные, воспользовавшись соответствующей формой в личном кабинете или нажав кнопку в нижней части письма.</p>
					<p>Если у вас возникнут какие-либо вопросы или потребуется дополнительная помощь, пожалуйста, свяжитесь с нами.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = "Завершить оформление";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Завершить оформление кредита");
		}
	}
	
	@Getter
	private static class CreateDocumentsTopic extends Topic {
		public CreateDocumentsTopic() {
			topicName = "create-documents";
			subject = "Решение по вашей заявке на кредит";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Сообщаем вам о результате рассмотрения вашей заявки на кредит.</p>
					<p>Мы рады сообщить, что ваша заявка одобрена! Для завершения оформления, пожалуйста, нажмите кнопку в нижней части данного письма.</p>
					<p>Если у вас есть вопросы или вам потребуется помощь на следующем этапе, мы всегда готовы вам помочь.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = "Сформировать документы";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Оформить документы на кредит");
		}
	}
	
	@Getter
	private static class SendDocumentsTopic extends Topic {
		public SendDocumentsTopic() {
			topicName = "send-documents";
			subject = "Оформление документов";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>В ответ на Ваш запрос мы подготовили необходимые документы для подписания. Пожалуйста, ознакомьтесь с приложенными файлами.<br>
					Для завершения процедуры необходимо Ваше согласие с условиями. Нажмите, пожалуйста, кнопку в нижней части письма, чтобы подтвердить свое согласие с условиями, описанными в документе.</p>
					<p>Если у Вас возникнут вопросы или потребуется дополнительная информация, не стесняйтесь обращаться к нам.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = "Подтвердить согласие";
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(HTTPS).host(HOST).path(PATH).queryParam("text", "Запросить подписание документов");
		}
	}
	
	@Getter
	private static class SendSesTopic extends Topic {
		public SendSesTopic() {
			topicName = "send-ses";
			subject = "Подписание документов";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Благодарим вас за выбор CreditBank.</p>
					<p>Для завершения оформления документов, пожалуйста, используйте кнопку в нижней части письма.</p>
					<p>Если у вас возникнут вопросы или потребуется дополнительная помощь, пожалуйста, не стесняйтесь обращаться к нам.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = "Подписать документы";
			uriComponentsBuilder = UriComponentsBuilder.fromPath("/deal/document/{statementId}/sign");
		}
	}
	
	@Getter
	private static class CreditIssuedTopic extends Topic {
		public CreditIssuedTopic() {
			topicName = "credit-issued";
			subject = "Ваш кредит успешно оформлен";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Рады сообщить, что ваш кредит успешно оформлен!</p>
					<p>Если у вас возникнут вопросы или потребуется дополнительная информация, пожалуйста, свяжитесь с нами. Мы всегда готовы помочь.</p>
					<p>Спасибо, что выбрали CreditBank.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = null;
			uriComponentsBuilder = null;
		}
	}
	
	@Getter
	private static class StatementDeniedTopic extends Topic {
		public StatementDeniedTopic() {
			topicName = "statement-denied";
			subject = "Решение по вашей заявке на кредит";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Благодарим вас за обращение в CreditBank.</p>
					<p>К сожалению, по результатам рассмотрения вашей заявки на кредит мы вынуждены сообщить об отказе. Это решение было принято на основании тщательной проверки всех предоставленных данных.</p>
					<p>Мы понимаем, что это может быть разочаровывающим, и приносим свои извинения за возможные неудобства. Если у вас есть вопросы или вы хотите узнать подробнее о причинах отказа, пожалуйста, свяжитесь с нами — мы будем рады обсудить с вами возможные шаги и альтернативные варианты.</p>
					<p>Спасибо за понимание и доверие.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = null;
			uriComponentsBuilder = null;
		}
	}
	
	@Getter
	private static class ClientRejectionTopic extends Topic {
		public ClientRejectionTopic() {
			topicName = "client-rejection";
			subject = "Подтверждение отказа от кредита";
			messageText = """
					<p>Уважаемый(ая) %s,</p>
					<p>Мы получили ваш запрос на отказ от кредита и подтверждаем, что ваше решение принято.</p>
					<p>Заявка на кредит была успешно аннулирована, и дальнейшие действия по ее оформлению прекращены. Если в будущем вы решите снова обратиться за кредитом или у вас возникнут вопросы, мы будем рады помочь вам.</p>
					<p>Благодарим вас за проявленный интерес и сотрудничество.</p>
					<p>С уважением,<br>
					Ваш CreditBank</p>""";
			buttonText = null;
			uriComponentsBuilder = null;
		}
	}
}
