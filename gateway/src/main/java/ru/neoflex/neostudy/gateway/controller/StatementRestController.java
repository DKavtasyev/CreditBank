package ru.neoflex.neostudy.gateway.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;
import ru.neoflex.neostudy.gateway.service.RequestService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("${app.rest.prefix.statement}")
@Tag(
		name = "Заявка",
		description = "Создание и регистрация заявки на кредит")
@Tag(
		name = "Фасад",
		description = "Предоставление общего интерфейса API для работы с приложением")
public class StatementRestController {
	private final RequestService requestService;
	
	
	@PostMapping
	public ResponseEntity<List<LoanOfferDto>> createLoanStatement(@RequestBody
																  @Parameter(description = "Пользовательские данные для предварительного расчёта кредита")
																  LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException {
		List<LoanOfferDto> offers = requestService.createLoanStatementRequest(loanStatementRequest);
		return ResponseEntity.status(HttpStatus.OK).body(offers);
	}
	
	@PostMapping("/select")
	public ResponseEntity<Void> applyOffer(@RequestBody
										   @Parameter(description = "Выбранное пользователем предложение кредита")
										   LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.applyOfferRequest(loanOffer);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/registration/{statementId}")
	public ResponseEntity<Void> finishRegistration(@RequestBody
												   @Parameter(description = "Пользовательские данные для расчёта и оформления кредита")
												   FinishingRegistrationRequestDto finishingRegistrationRequestDto,
												   @PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException, InvalidPreApproveException {
		requestService.finishRegistrationRequest(finishingRegistrationRequestDto, statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/deny/{statementId}")
	public ResponseEntity<Void> denyOffer(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		requestService.denyOfferRequest(statementId);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
