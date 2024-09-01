package ru.neoflex.neostudy.statement.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.List;

@Aspect
@Component
public class LoggingAspect {
	Logger log;
	
	@Around("ru.neoflex.neostudy.statement.aspect.Pointcuts.allControllerMethods()")
	public Object aroundStatementControllerMethodsLoggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		log = LogManager.getLogger(proceedingJoinPoint.getTarget().getClass());
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String methodName = methodSignature.getName();
		Object[] arguments = proceedingJoinPoint.getArgs();
		
		for(Object arg : arguments){
			if (arg instanceof BindingResult) {
				continue;
			}
			log.info(getLoggingText(arg, methodName, "Received argument: ", true));
		}
		
		Object targetMethodResult = proceedMethod(proceedingJoinPoint, methodName);
		
		log.info(getLoggingText(targetMethodResult, methodName, "Returned argument: ", true));
		return targetMethodResult;
	}
	
	@Around("ru.neoflex.neostudy.statement.aspect.Pointcuts.requestLoanOffersMethod()")
	private Object aroundRequestLoanOfferMethod(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		log = LogManager.getLogger(proceedingJoinPoint.getTarget().getClass());
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String methodName = methodSignature.getName();
		
		Object targetMethodResult = proceedMethod(proceedingJoinPoint, methodName);
		((List<?>) targetMethodResult).forEach(offer -> log.info(getLoggingText(offer, methodName, "Received data from calculator: ", true)));
		return targetMethodResult;
	}
	
	@SuppressWarnings("AroundAdviceStyleInspection")
	@Around("ru.neoflex.neostudy.statement.aspect.Pointcuts.sendChosenOfferMethod()")
	public void aroundSendChosenOfferMethodMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		log = LogManager.getLogger(proceedingJoinPoint.getTarget().getClass());
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String methodName = methodSignature.getName();
		Object[] args = proceedingJoinPoint.getArgs();
		log.info(getLoggingText(args[0], methodName, "Sending chosen offer to MS Deal: ", true));
		proceedMethod(proceedingJoinPoint, methodName);
		log.info(getLoggingText(null, methodName, "Chosen offer has sent to MS Deal. ", false));
	}
	
	private Object proceedMethod(ProceedingJoinPoint pjp, String methodName) throws Throwable {
		log = LogManager.getLogger(pjp.getTarget().getClass());
		Object targetMethodResult;
		try {
			targetMethodResult = pjp.proceed();
		}
		catch (Throwable e) {
			StringBuilder sb = new StringBuilder();
			sb.append("Method: ").append(methodName).append("; ").append(e);
			log.warn(sb);
			throw e;
		}
		return targetMethodResult;
	}
	
	private StringBuilder getLoggingText(Object arg, String methodName, String joinPoint, boolean withArgs) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append("Method: ")
					.append(methodName)
					.append("; ")
					.append(joinPoint);
			if (withArgs) {
				sb.append(arg.getClass().getSimpleName())
						.append("; ")
						.append("Fields: ");
				for (Field field : arg.getClass().getDeclaredFields()) {
					field.setAccessible(true);
					sb.append(field.getName())
							.append(": ")
							.append(field.get(arg))
							.append("; ");
					field.setAccessible(false);
				}
			}
		}
		catch (Exception e) {
			log.error("Logging error: {} {}", e, e.getMessage());
		}
		return sb;
	}
}
