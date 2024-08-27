package ru.neoflex.neostudy.dossier.aspect;


import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.EmailMessage;

@Component
@Aspect
public class LoggingAspect {
	@Before("execution(* ru.neoflex.neostudy.dossier.service.KafkaConsumer.*(..))")
	public void beforeKafkaConsumerAllMethods(JoinPoint joinPoint) {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String methodName = methodSignature.getName();
		Object[] args = joinPoint.getArgs();
		EmailMessage emailMessageArg = (EmailMessage) args[0];
		Logger log = LoggerFactory.getLogger(joinPoint.getTarget().getClass());
		log.info("Kafka message received. Method: {}, Message: {}", methodName, emailMessageArg);
	}
}
