package ru.neoflex.neostudy.deal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.constants.ApplicationStatus;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.KafkaService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("${app.rest.deal.prefix}")
@RequiredArgsConstructor
@Tag(
		name = "Сделка",
		description = "Управление данными сделки")
public class DealController {
	private final PreScoringService preScoringService;
	private final ScoringService scoringService;
	private final DataService dataService;
	private final KafkaService kafkaService;
	
	@PostMapping("/statement")
	@Operation(
			summary = "Предоставление возможных предложений займа",
			description = "Получает запрос от пользователя на предварительный расчёт кредита, сохраняет данные о клиенте " +
					"и о заявке, делает запрос в МС Калькулятор для расчёта возможных условий, и предоставляет информацию " +
					"пользователю для выбора.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request")
			})
	public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@RequestBody @Parameter(description = "Пользовательские данные для предварительного расчёта кредита") LoanStatementRequestDto loanStatementRequest) throws InvalidPassportDataException, InternalMicroserviceException {
		Statement statement = dataService.prepareData(loanStatementRequest);
		List<LoanOfferDto> offers = preScoringService.getOffers(loanStatementRequest, statement);
		dataService.updateStatement(statement, ApplicationStatus.PREAPPROVAL);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@PostMapping("/offer/select")
	@Operation(
			summary = "Применение выбранного пользователем кредитного предложения",
			description = """
					Сохраняет в пользовательской заявке выбранное предложение, статус кредита меняет на "Одобренный".
					
					Примечание: для отправки запроса через Swagger нужно в поле statementId Request body установить
					значение statementId соответствующей statement из базы данных, или скопировать один из четырёх
					предложений займа, полученных при запросе по адресу: /deal/statement.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	public ResponseEntity<Void> applyOffer(@RequestBody @Parameter(description = "Выбранное пользователем предложение кредита") LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException {
		
		dataService.applyOfferAndSave(loanOffer);
		kafkaService.sendFinishRegistrationRequest(loanOffer.getStatementId());
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@GetMapping("/offer/deny/{statementId}")
	@Operation(
			summary = "Отказ клиента от кредита",
			description = "При получении отказа клиента от кредита сохраняет информацию в заявке. По statementId находит" +
					"соответствующую заявку и меняет её статус на CLIENT_DENIED.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
			})
	public ResponseEntity<Void> denyOffer(@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, InternalMicroserviceException {
		Statement statement = dataService.denyOffer(statementId);
		kafkaService.sendDenial(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/calculate/{statementId}")
	@Operation(
			summary = "Оформление условий кредита",
			description = "Принимает и сохраняет полную информацию от клиента для оформления кредита, делает запрос для " +
					"расчёта точных условий кредита с графиком платежей, сохраняет все данные.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "406", description = "Not acceptable"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	public ResponseEntity<Void> calculateLoanParameters(@RequestBody @Parameter(description = "Пользовательские данные для расчёта и оформления кредита") FinishingRegistrationRequestDto finishingRegistrationRequestDto,
														@PathVariable("statementId") UUID statementId) throws StatementNotFoundException, JsonProcessingException, LoanRefusalException, InternalMicroserviceException {
		Statement statement = dataService.findStatement(statementId);
		scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement);
		kafkaService.sendCreatingDocumentsRequest(statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
