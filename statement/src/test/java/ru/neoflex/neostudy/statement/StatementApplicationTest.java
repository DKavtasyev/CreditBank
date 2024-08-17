package ru.neoflex.neostudy.statement;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.mockito.Mockito.mockStatic;

class StatementApplicationTest {
	
	@Test
	void main_whenMethodStarts_thenStartsSpringContainer() {
		
		try(MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
			String[] args = new String[1];
			ConfigurableApplicationContext expectedContext = new AnnotationConfigApplicationContext();
			springApplicationMock.when(() -> SpringApplication.run(StatementApplication.class, args)).thenReturn(expectedContext);
			ConfigurableApplicationContext actualContext = SpringApplication.run(StatementApplication.class, args);
			Assertions.assertThat(actualContext).isSameAs(expectedContext);
		}
	}
}