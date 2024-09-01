package ru.neoflex.neostudy.deal.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static ru.neoflex.neostudy.common.constants.Theme.*;

@Configuration
public class KafkaConfig {
	
	@Bean
	public NewTopic finishRegistrationTopic() {
		return TopicBuilder
				.name(FINISH_REGISTRATION.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
	
	@Bean
	public NewTopic createDocumentsTopic() {
		return TopicBuilder
				.name(CREATE_DOCUMENTS.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
	
	@Bean
	public NewTopic sendDocumentsTopic() {
		return TopicBuilder
				.name(SEND_DOCUMENTS.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
	
	@Bean
	public NewTopic sendSesTopic() {
		return TopicBuilder
				.name(SEND_SES.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
	
	@Bean
	public NewTopic creditIssuedTopic() {
		return TopicBuilder
				.name(CREDIT_ISSUED.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
	
	@Bean
	public NewTopic statementDeniedTopic() {
		return TopicBuilder
				.name(STATEMENT_DENIED.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
}
