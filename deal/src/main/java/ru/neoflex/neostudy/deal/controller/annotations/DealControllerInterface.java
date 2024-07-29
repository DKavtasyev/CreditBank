package ru.neoflex.neostudy.deal.controller.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("${app.rest.deal.prefix}")
@Tag(
		name = "Сделка",
		description = "Управление данными сделки")
public interface DealControllerInterface {
	
	@PostMapping("/statement")
	@Operation(
			summary = "Предоставление возможных предложений займа",
			description = "Получает запрос от пользователя на предварительный расчёт кредита, сохраняет данные о клиенте " +
					"и о заявке, делает запрос в МС Калькулятор для расчёта возможных условий, и предоставляет информацию " +
					"пользователю для выбора.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody
															  @Parameter(description = "Пользовательские данные для предварительного расчёта кредита")
															  LoanStatementRequestDto loanStatementRequest) throws InvalidPassportDataException, InternalMicroserviceException;
	
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
	ResponseEntity<Void> applyOffer(@RequestBody
										   @Parameter(description = "Выбранное пользователем предложение кредита")
										   LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException;
	
	@PostMapping("/calculate/{statementId}")
	@Operation(
			summary = "Оформление условий кредита",
			description = "Принимает и сохраняет полную информацию от клиента для оформления кредита, делает запрос для " +
					"расчёта точных условий кредита с графиком платежей, сохраняет все данные.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "406", description = "Not acceptable"),
					@ApiResponse(responseCode = "428", description = "Precondition required"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> calculateCredit(@RequestBody
												@Parameter(description = "Пользовательские данные для расчёта и оформления кредита")
												FinishingRegistrationRequestDto finishingRegistrationRequestDto,
												@PathVariable("statementId")
												@Parameter(description = "Идентификатор заявки Statement")
												UUID statementId) throws StatementNotFoundException, LoanRefusalException, InternalMicroserviceException, InvalidPreApproveException;
	
	@GetMapping("/offer/deny/{statementId}")
	@Operation(
			summary = "Отказ клиента от кредита",
			description = "При получении отказа клиента от кредита сохраняет информацию в заявке. По statementId находит" +
					"соответствующую заявку и меняет её статус на CLIENT_DENIED.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> denyOffer(@PathVariable("statementId")
										  @Parameter(description = "Идентификатор заявки Statement")
										  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException;
}
