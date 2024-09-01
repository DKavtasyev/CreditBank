package ru.neoflex.neostudy.calculator.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.PaymentScheduleElementDto;

import java.lang.reflect.Field;
import java.util.List;

@Component
@Aspect
@Log4j2
public class LoggingAspect {
	@Before("ru.neoflex.neostudy.calculator.aspect.Pointcuts.allControllerMethods()")
	public void beforeControllerCalculationMethods(JoinPoint joinPoint) throws IllegalAccessException {
		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
		String methodName = methodSignature.getName();
		Object[] arguments = joinPoint.getArgs();
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Before method: ").append(methodName).append("; ");
		
		for (Field field : arguments[0].getClass().getDeclaredFields()) {
			field.setAccessible(true);
			stringBuilder.append(field.getName()).append(": ").append(field.get(arguments[0])).append("; ");
			field.setAccessible(false);
		}
		log.info(stringBuilder);
	}
	
	@AfterReturning(pointcut = "ru.neoflex.neostudy.calculator.aspect.Pointcuts.calculatorServicePreScoreMethod()",
			returning = "offerList")
	public void afterReturningResultOfPreScoreMethod(List<LoanOfferDto> offerList) throws IllegalAccessException {
		for (LoanOfferDto loanOffer : offerList) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Method: preScore; LoanOffer calculated: (");
			
			addFieldValues(loanOffer, stringBuilder);
			
			log.info(stringBuilder);
		}
	}
	
	@AfterReturning(pointcut = "ru.neoflex.neostudy.calculator.aspect.Pointcuts.calculatorServiceScoreMethod()",
			returning = "credit")
	public void afterReturningResultOfScoreMethod(CreditDto credit) throws IllegalAccessException {
		for (PaymentScheduleElementDto paymentScheduleElement : credit.getPaymentSchedule()) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append(String.format("Method: score; PaymentScheduleElement № %d calculated: (", paymentScheduleElement.getNumber()));
			
			addFieldValues(paymentScheduleElement, stringBuilder);
			
			log.info(stringBuilder);
		}
		
		StringBuilder commonStringBuilder = makeMessage(credit);
		log.info(commonStringBuilder);
		
	}
	
	private static StringBuilder makeMessage(CreditDto credit) {
		StringBuilder commonStringBuilder = new StringBuilder();
		commonStringBuilder.append("Method: score; CreditDto calculated: (");
		commonStringBuilder.append(String.format("amount: %f; term: %d; monthlyPayment: %f; rate: %f; psk: %f; isInsuranceEnabled: %b; isSalaryClient: %b",
				credit.getAmount().doubleValue(),
				credit.getTerm(),
				credit.getMonthlyPayment().doubleValue(),
				credit.getRate().doubleValue(),
				credit.getPsk().doubleValue(),
				credit.getIsInsuranceEnabled(),
				credit.getIsSalaryClient()));
		return commonStringBuilder;
	}
	
	private void addFieldValues(Object o, StringBuilder stringBuilder) throws IllegalAccessException {
		for (Field field : o.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			stringBuilder.append(field.getName()).append(": ").append(field.get(o)).append("; ");
			field.setAccessible(false);
		}
		stringBuilder.append(")");
	}
}
