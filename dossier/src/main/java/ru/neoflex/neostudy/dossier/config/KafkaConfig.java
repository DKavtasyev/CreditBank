package ru.neoflex.neostudy.dossier.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.neoflex.neostudy.common.constants.Theme;
import ru.neoflex.neostudy.common.dto.EmailMessage;

import java.util.Map;

import static org.apache.kafka.clients.consumer.ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG;
import static org.springframework.kafka.support.serializer.JsonDeserializer.VALUE_DEFAULT_TYPE;
import static ru.neoflex.neostudy.common.constants.Theme.*;

@Configuration
@EnableKafka
public class KafkaConfig {
	
	@Bean
	public ConsumerFactory<String, EmailMessage> consumerFactory(KafkaProperties kafkaProperties, ObjectMapper mapper) {
		Map<String, Object> props = kafkaProperties.buildConsumerProperties(null);
		props.put(KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
		props.put(VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
		props.put(VALUE_DEFAULT_TYPE, EmailMessage.class);
		
		DefaultKafkaConsumerFactory<String, EmailMessage> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(props);
		JsonDeserializer<EmailMessage> deserializer = new JsonDeserializer<>(mapper);
		kafkaConsumerFactory.setValueDeserializer(deserializer);
		return kafkaConsumerFactory;
	}
	
	@Bean
	public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, EmailMessage>> listenerContainerFactory(ConsumerFactory<String, EmailMessage> consumerFactory) {
		var factory = new ConcurrentKafkaListenerContainerFactory<String, EmailMessage>();
		factory.setConsumerFactory(consumerFactory);
		return factory;
	}
	
	@Bean
	public NewTopic finishRegistrationTopic() {
		return buildTopicBuilder(FINISH_REGISTRATION);
	}
	
	@Bean
	public NewTopic createDocumentsTopic() {
		return buildTopicBuilder(CREATE_DOCUMENTS);
	}
	
	@Bean
	public NewTopic sendDocumentsTopic() {
		return buildTopicBuilder(SEND_DOCUMENTS);
	}
	
	@Bean
	public NewTopic sendSesTopic() {
		return buildTopicBuilder(SEND_SES);
	}
	
	@Bean
	public NewTopic creditIssuedTopic() {
		return buildTopicBuilder(CREDIT_ISSUED);
	}
	
	@Bean
	public NewTopic statementDeniedTopic() {
		return buildTopicBuilder(STATEMENT_DENIED);
	}
	
	private NewTopic buildTopicBuilder(Theme theme) {
		return TopicBuilder
				.name(theme.getTopicName())
				.partitions(1)
				.replicas(1)
				.build();
	}
}
