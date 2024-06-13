package ru.neoflex.neostudy.deal.aspect;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import ru.neoflex.neostudy.common.constants.CreditStatus;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.deal.entity.Client;
import ru.neoflex.neostudy.deal.entity.Statement;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Aspect
@Component
@Log4j2
public class LoggingAspect
{
	
	@Around("ru.neoflex.neostudy.deal.aspect.Pointcuts.allControllerMethods()")
	public Object aroundControllerMethodsLoggingAdvice(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
	{
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String methodName = methodSignature.getName();
		Object[] arguments = proceedingJoinPoint.getArgs();
		
		for (Object arg : arguments)
		{
			if (arg instanceof BindingResult || arg instanceof UUID)
				continue;
			
			log.info(getLoggingText(arg, methodName, "Received argument: ", true));
		}
		
		Object targetMethodResult = proceedMethod(proceedingJoinPoint, methodName);
		
		log.info(getLoggingText(targetMethodResult, methodName, "Returned argument: ", true));
		return targetMethodResult;
	}
	
	@AfterReturning(pointcut = "execution(* ru.neoflex.neostudy.deal.service.ClientEntityService.findClientByPassport(..))", returning = "optionalClient")
	private void afterReturningFindClientByPassport(Optional<Client> optionalClient)
	{
		optionalClient.ifPresentOrElse(client -> log.info(getLoggingText(client, "checkAndSaveClient", "Client was found in DB: ", true)), () -> log.info("Method: checkAndSaveClient; creating new Client..."));
	}
	
	@AfterThrowing(pointcut = "execution(* ru.neoflex.neostudy.deal.service.ClientEntityService.checkAndSaveClient(..))", throwing = "exception")
	private void aroundCheckAndSaveClientMethod(Throwable exception)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Method: ").append("checkAndSaveClient").append("; ").append(exception);
		log.warn(sb);
	}
	
	@AfterReturning(pointcut = "execution(* ru.neoflex.neostudy.deal.service.DataService.writeData(..))", returning = "statement")
	private void afterReturningWriteDataMethod(Statement statement)
	{
		log.info(getLoggingText(statement, "writeData", "Created and save new Statement: ", true));
	}
	
	@After("execution(* ru.neoflex.neostudy.deal.service.DataService.updateStatement(..))")
	private void afterUpdateStatementMethod(JoinPoint joinPoint)
	{
		Object[] arguments = joinPoint.getArgs();
		
		for (Object arg : arguments)
		{
			String joinPointString = String.format("For statement id: %s has been set loan offer: ", ((LoanOfferDto) arg).getStatementId().toString());
			log.info(getLoggingText(arg, "updateStatement", joinPointString, true));
		}
	}
	
	@After("execution(* ru.neoflex.neostudy.deal.service.StatementEntityService.setStatus(..))")
	private void afterSetStatusMethod(JoinPoint joinPoint)
	{
		Object[] args = joinPoint.getArgs();
		String joinPointString = String.format("For statement id: %1$s has been set status: %2$s.", ((Statement) args[0]).getStatementId().toString(), args[1].toString());
		log.info(getLoggingText(args[0], "setStatus", joinPointString, false));
	}
	
	@Around("ru.neoflex.neostudy.deal.aspect.Pointcuts.allCalculatorRequesterMethods()")
	private Object aroundCalculatorRequesterMethods(ProceedingJoinPoint proceedingJoinPoint) throws Throwable
	{
		MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
		String methodName = methodSignature.getName();
		
		Object targetMethodResult = proceedMethod(proceedingJoinPoint, methodName);
		
		if (targetMethodResult instanceof List<?>)
			((List<?>) targetMethodResult).forEach(e -> log.info(getLoggingText(e, methodName, "Received data from calculator: ", true)));
		else
			log.info(getLoggingText(targetMethodResult, methodName, "Received data from calculator: ", true));
		
		return targetMethodResult;
	}
	
	@After("ru.neoflex.neostudy.deal.aspect.Pointcuts.allSaveEntityMethods()")
	private void afterSaveEntityMethods(JoinPoint joinPoint)
	{
		Object[] args = joinPoint.getArgs();
		
		for (Object arg : args)
			log.info(getLoggingText(arg, "save", "Saved entity: " + arg.getClass().getSimpleName(), false));
	}
	
	@After("execution(* ru.neoflex.neostudy.deal.service.ScoringService.scoreAndSaveCredit(..))")
	private void afterScoreAndSaveCredit(JoinPoint joinPoint)
	{
		Object[] args = joinPoint.getArgs();
		
		String joinPointString1 = String.format("Credit id: %1$s has been created and has been set for statement id: %2$s.", ((Statement) args[1]).getCredit().getCreditId().toString(), ((Statement) args[1]).getStatementId().toString());
		log.info(getLoggingText(args[1], "scoreAndSaveCredit", joinPointString1, false));
		
		String joinPointString2 = String.format("For credit id: %1$s has been set status: %2$s.", ((Statement) args[1]).getCredit().getCreditId().toString(), CreditStatus.CALCULATED);
		log.info(getLoggingText(args[1], "scoreAndSaveCredit", joinPointString2, false));
	}
	
	private Object proceedMethod(ProceedingJoinPoint pjp, String methodName) throws Throwable
	{
		Object targetMethodResult;
		try
		{
			targetMethodResult = pjp.proceed();
		}
		catch (Throwable e)
		{
			StringBuilder sb = new StringBuilder();
			sb.append("Method: ").append(methodName).append("; ").append(e);
			log.warn(sb);
			throw e;
		}
		return targetMethodResult;
	}
	
	private StringBuilder getLoggingText(Object arg, String methodName, String joinPoint, boolean withArgs)
	{
		StringBuilder sb = new StringBuilder();
		try
		{
			sb.append("Method: ")
					.append(methodName)
					.append("; ")
					.append(joinPoint);
			if (withArgs)
			{
				sb.append(arg.getClass().getSimpleName())
						.append("; ")
						.append("Fields: ");
				for (Field field : arg.getClass().getDeclaredFields())
				{
					field.setAccessible(true);
					sb.append(field.getName())
							.append(": ")
							.append(field.get(arg))
							.append("; ");
					field.setAccessible(false);
				}
			}
		}
		catch (Throwable e)
		{
			log.error("Logging error: " + e);
		}
		return sb;
	}
}
