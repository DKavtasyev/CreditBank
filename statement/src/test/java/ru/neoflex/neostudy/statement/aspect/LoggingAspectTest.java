package ru.neoflex.neostudy.statement.aspect;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.neostudy.statement.controller.StatementController;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {
	
	@Mock
	Logger log;
	
	@Mock
	MethodSignature methodSignature;
	@Mock
	ProceedingJoinPoint proceedingJoinPoint;
	
	@Spy
	@InjectMocks
	LoggingAspect loggingAspect;
	
	@Nested
	@DisplayName("Тестирование аспекта aroundStatementControllerMethodsLoggingAdvice")
	class TestingAroundStatementControllerMethodsLoggingAdvice {
		
		
		@Test
		void aroundStatementControllerMethodsLoggingAdvice() throws Throwable {
	
			try(MockedStatic<LogManager> logManagerMock = mockStatic(LogManager.class)) {
				StatementController statementControllerMock = mock(StatementController.class);
				when(proceedingJoinPoint.getTarget()).thenReturn(statementControllerMock);
				
				logManagerMock.when(() -> LogManager.getLogger(statementControllerMock.getClass())).thenReturn(log);
				String[] args = new String[3];
				args[0] = "arg0";
				args[1] = "arg1";
				args[2] = "arg2";
				when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
				when(methodSignature.getName()).thenReturn("MethodName");
				when(proceedingJoinPoint.getArgs()).thenReturn(args);
				loggingAspect.aroundStatementControllerMethodsLoggingAdvice(proceedingJoinPoint);
				verify(log, atLeast(1)).info(any(StringBuilder.class));
			}
		}
		
	}
	
	@Nested
	@DisplayName("Тестирование аспекта aroundSendChosenOfferMethodMethods")
	class TestingGetLoanOffersMethod {
		
		
		@Test
		void aroundSendChosenOfferMethodMethods() throws Throwable {
			try(MockedStatic<LogManager> logManagerMock = mockStatic(LogManager.class)) {
				StatementController statementControllerMock = mock(StatementController.class);
				when(proceedingJoinPoint.getTarget()).thenReturn(statementControllerMock);
				
				logManagerMock.when(() -> LogManager.getLogger(statementControllerMock.getClass())).thenReturn(log);
				String[] args = new String[3];
				args[0] = "arg0";
				args[1] = "arg1";
				args[2] = "arg2";
				when(proceedingJoinPoint.getSignature()).thenReturn(methodSignature);
				when(methodSignature.getName()).thenReturn("MethodName");
				when(proceedingJoinPoint.getArgs()).thenReturn(args);
				loggingAspect.aroundSendChosenOfferMethodMethods(proceedingJoinPoint);
				verify(log, atLeast(1)).info(any(StringBuilder.class));
			}
			
			
			
		}
	}
}