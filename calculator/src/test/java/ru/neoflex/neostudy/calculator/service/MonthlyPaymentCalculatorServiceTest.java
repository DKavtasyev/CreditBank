package ru.neoflex.neostudy.calculator.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class MonthlyPaymentCalculatorServiceTest {
	static MonthlyPaymentCalculatorService monthlyPaymentCalculatorService;
	
	@BeforeAll
	static void init() {
		monthlyPaymentCalculatorService = new MonthlyPaymentCalculatorService();
	}
	
	@ParameterizedTest
	@CsvSource({
			"1000000, 12, 0.12, 88848.7886783417201853",
			"30000, 6, 0.12, 5176.4510013264426079",
			"100000000, 120, 0.12, 1434709.4840258737728000",
			"3000000, 24, 0.9, 273150.2384166466631238",
			"3000000, 24, 2.3, 583678.7846867152477770",
			"3000000, 24, 0.005, 125652.0813065728360767",
		
	})
	void calculate_whenStatementParametersReceived_thenReturnMonthlyPaymentValue(String amount, Integer term, String rate, String expectedRate) {
		BigDecimal actualRate = monthlyPaymentCalculatorService.calculate(new BigDecimal(amount), term, new BigDecimal(rate));
		assertThat(actualRate.toString()).isEqualTo(expectedRate);
	}
}