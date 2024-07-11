package ru.neoflex.neostudy.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import ru.neoflex.neostudy.calculator.service.CalculatorService;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

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
public class CalculatorController {
	private final CalculatorService service;
	
	@PostMapping("/offers")
	@Operation(
			summary = "Предварительный расчёт предложений",
			description = "По данным от пользователя предлагает четыре предложения займа в зависимости от опций: страховка, зарплатный клиент",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success")
			})
	public ResponseEntity<List<LoanOfferDto>> calculateLoanOffers(
			@RequestBody @Parameter(description = "Пользовательские данные для предварительного расчёта кредита") LoanStatementRequestDto loanStatementRequest) {
		List<LoanOfferDto> offers = service.preScore(loanStatementRequest);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@PostMapping("/calc")
	@Operation(
			summary = "Расчёт графика платежей",
			description = "По данным от пользователя рассчитывает график платежей и сумму каждого платежа",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "406", description = "Not acceptable")
			})
	public ResponseEntity<CreditDto> calculateLoanTerms(
			@RequestBody @Parameter(description = "Пользовательсткие данные для расчёта кредита") ScoringDataDto scoringData) throws LoanRefusalException {
		CreditDto credit = service.score(scoringData);
		return new ResponseEntity<>(credit, HttpStatus.OK);
	}
	
	@ExceptionHandler(LoanRefusalException.class)
	private ResponseEntity<ExceptionDetails> refuseLoan(LoanRefusalException e, WebRequest request) {
		log.warn(e.getMessage());
		ExceptionDetails exceptionDetails = new ExceptionDetails(HttpStatus.NOT_ACCEPTABLE.value(), e.getMessage(), request.getDescription(false));
		return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(exceptionDetails);
	}
}
