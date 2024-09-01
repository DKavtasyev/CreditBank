package ru.neoflex.neostudy.calculator.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.util.List;

@RequestMapping(path = "${app.rest.prefix}")
@Tag(name = "Калькулятор",
		description = "Предварительный и основной расчёт кредита")
public interface CalculatorControllerInterface {
	
	@PostMapping("/offers")
	@Operation(
			summary = "Предварительный расчёт предложений",
			description = "По данным от пользователя предлагает четыре предложения займа в зависимости от опций: страховка, зарплатный клиент",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<List<LoanOfferDto>> generateOffers(
			@Valid
			@RequestBody
			@Parameter(description = "Пользовательские данные для предварительного расчёта кредита")
			LoanStatementRequestDto loanStatementRequest,
			BindingResult bindingResult) throws InternalMicroserviceException;
	
	@PostMapping("/calc")
	@Operation(
			summary = "Расчёт графика платежей",
			description = "По данным от пользователя рассчитывает график платежей и сумму каждого платежа",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "406", description = "Not acceptable"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<CreditDto> calculateCredit(
			@Valid
			@RequestBody
			@Parameter(description = "Пользовательсткие данные для расчёта кредита")
			ScoringDataDto scoringData,
			BindingResult bindingResult) throws LoanRefusalException, InternalMicroserviceException;
}
