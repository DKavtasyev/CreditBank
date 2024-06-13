package ru.neoflex.neostudy.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.calculator.service.CalculatorService;
import ru.neoflex.neostudy.calculator.exception.LoanRefusalException;

import java.util.List;

/**
 * Калькулятор кредита с ежемесячной фиксированной процентной ставкой, с аннуитетным ежемесячным платежом, со страховкой
 * 5 % от суммы кредита при сниженной ставке на 3 %
 */
@Log4j2
@RestController
@RequestMapping(path = "${app.rest.prefix}")
@RequiredArgsConstructor
@Tag(
		name = "Калькулятор",
		description = "Предварительный и основной расчёт кредита")
public class CalculatorController
{
	private final CalculatorService service;
	
	@PostMapping("/offers")
	@Operation(
			summary = "Предварительный расчёт предложений",
			description = "По данным от пользователя предлагает четыре предложения займа в зависимости от опций: страховка, зарплатный клиент",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request")
			})
	public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(
			@RequestBody @Valid @Parameter(description = "Пользовательские данные для предварительного расчёта кредита") LoanStatementRequestDto loanStatementRequest,
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
	@Operation(
			summary = "Расчёт графика платежей",
			description = "По данным от пользователя рассчитывает график платежей и сумму каждого платежа",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request")
			})
	public ResponseEntity<CreditDto> calculateLoanTerms(
			@RequestBody @Valid @Parameter(description = "Пользовательсткие данные для расчёта кредита") ScoringDataDto scoringData,
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
