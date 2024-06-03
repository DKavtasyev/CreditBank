package ru.neostudy.neoflex.calculator.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.neostudy.neoflex.calculator.dto.CreditDto;
import ru.neostudy.neoflex.calculator.dto.LoanOfferDto;
import ru.neostudy.neoflex.calculator.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.calculator.dto.ScoringDataDto;
import ru.neostudy.neoflex.calculator.exception.LoanRefusalException;
import ru.neostudy.neoflex.calculator.service.CalculatorService;

import java.util.List;

/**
 * Калькулятор кредита с ежемесячной фиксированной процентной ставкой, с аннуитетным ежемесячным платежом, со страховкой
 * 5 % от суммы кредита при сниженной ставке на 3 %
 * */
@Log4j2
@RestController
@RequestMapping(path = "${app.rest.prefix}")
@RequiredArgsConstructor
public class CalculatorController
{
	private final CalculatorService service;
	
	@PostMapping("/offers")
	public ResponseEntity<List<LoanOfferDto>> calculationOfPossibleLoanTerms(@RequestBody @Valid LoanStatementRequestDto loanStatementRequest,
																			 BindingResult bindingResult)
	{
		if (bindingResult.hasErrors())
		{
			log.warn("LoanStatement Data is invalid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		List<LoanOfferDto> offers = service.preScore(loanStatementRequest);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@PostMapping("/calc")
	public ResponseEntity<CreditDto> fullCalculationOfLoanTerms(@RequestBody @Valid ScoringDataDto scoringData,
																BindingResult bindingResult) throws Exception
	{
		if (bindingResult.hasErrors())
		{
			log.warn("ScoringData is invalid");
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		CreditDto credit = service.score(scoringData);
		return new ResponseEntity<>(credit, HttpStatus.OK);
	}
	
	@ExceptionHandler(LoanRefusalException.class)
	private ResponseEntity<String> refuseLoan()
	{
		log.info("Loan denied");
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
}
