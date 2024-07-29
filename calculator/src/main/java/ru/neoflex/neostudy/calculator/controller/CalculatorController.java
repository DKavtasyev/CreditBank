package ru.neoflex.neostudy.calculator.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.calculator.service.CalculatorService;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.util.List;

/**
 * Калькулятор кредита с ежемесячной фиксированной процентной ставкой, с аннуитетным ежемесячным платежом, со страховкой
 * 5 % от суммы кредита при сниженной ставке на 3 %
 */
@Log4j2
@RestController
@RequiredArgsConstructor
public class CalculatorController implements CalculatorControllerInterface {
	private final CalculatorService calculatorService;
	
	@Override
	public ResponseEntity<List<LoanOfferDto>> generateOffers(LoanStatementRequestDto loanStatementRequest, BindingResult bindingResult) throws InternalMicroserviceException {
		if (bindingResult.hasErrors()) {
			throw new InternalMicroserviceException("MS calculator: invalid input parameters");
		}
		List<LoanOfferDto> offers = calculatorService.preScore(loanStatementRequest);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<CreditDto> calculateCredit(ScoringDataDto scoringData, BindingResult bindingResult) throws LoanRefusalException, InternalMicroserviceException {
		if (bindingResult.hasErrors()) {
			throw new InternalMicroserviceException("MS calculator: invalid input parameters");
		}
		CreditDto credit = calculatorService.score(scoringData);
		return new ResponseEntity<>(credit, HttpStatus.OK);
	}
}
