package ru.neoflex.neostudy.gateway.controller.annotations;

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

@RequestMapping("${app.rest.prefix.statement}")
@Tag(
		name = "Заявка",
		description = "Создание и регистрация заявки на кредит")
public interface StatementRestControllerInterface {
	
	@PostMapping
	@Operation(
			summary = "Создание заявки на кредит",
			description = """
					Перенаправляет запрос о создании заявки в MS deal, в ответ принимает четыре предварительно
					рассчитанных предложения о кредите, предоставляет их пользователю для выбора подходящего предложения.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<List<LoanOfferDto>> createLoanStatement(@RequestBody
																  @Parameter(description = "Пользовательские данные для предварительного расчёта кредита")
																  LoanStatementRequestDto loanStatementRequest) throws InvalidUserDataException, InternalMicroserviceException;
	
	@PostMapping("/select")
	@Operation(
			summary = "Применение выбранного пользователем кредитного предложения",
			description = """
					Перенаправляет запрос с выбранным пользователем кредитным предложением в MS deal.
					
					Примечание: для отправки запроса через Swagger нужно в поле statementId request body установить
					значение statementId соответствующей statement из базы данных, или скопировать один из четырёх
					предложений займа, полученныхв ответе на запрос по адресу: /statement.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> applyOffer(@RequestBody
										   @Parameter(description = "Выбранное пользователем предложение кредита")
										   LoanOfferDto loanOffer) throws StatementNotFoundException, InternalMicroserviceException;
	
	@PostMapping("/registration/{statementId}")
	@Operation(
			summary = "Оформление кредита",
			description = "Перенаправляет запрос с информацией для оформления кредита в MS deal.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "406", description = "Not acceptable"),
					@ApiResponse(responseCode = "428", description = "Precondition required"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> finishRegistration(@RequestBody
												   @Parameter(description = "Пользовательские данные для расчёта и оформления кредита")
												   FinishingRegistrationRequestDto finishingRegistrationRequestDto,
												   @PathVariable("statementId")
												   @Parameter(description = "Идентификатор заявки Statement")
												   UUID statementId) throws StatementNotFoundException, InternalMicroserviceException, LoanRefusalException, InvalidPreApproveException;
	
	@GetMapping("/deny/{statementId}")
	@Operation(
			summary = "Отказ клиента от кредита",
			description = "Перенаправляет запрос с отказом клиента от кредита в MS deal",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")
			})
	ResponseEntity<Void> denyOffer(@PathVariable("statementId")
										  @Parameter(description = "Идентификатор заявки Statement")
										  UUID statementId) throws StatementNotFoundException, InternalMicroserviceException;
}
