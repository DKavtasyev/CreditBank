package ru.neoflex.neostudy.statement.controller.annotations;

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
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;

import java.util.List;

@RequestMapping("${app.rest.prefix}")
@Tag(
		name = "Заявка",
		description = "Оформление заявки на кредит")
public interface StatementControllerInterface {
	
	@PostMapping
	@Operation(
			summary = "Создание заявки на кредит",
			description = "Получает запрос от пользователя на предварительный расчёт кредита, проверяет полученные данные, " +
					"делает запрос возможных условий в МС Сделка и предоставляет информацию " +
					"пользователю для выбора.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "500", description = "Internal server error")})
	ResponseEntity<List<LoanOfferDto>> createStatement(@RequestBody
															  @Valid
															  @Parameter(description = "Пользовательские данные для предварительного расчёта кредита")
															  LoanStatementRequestDto loanStatementRequestDto,
															  BindingResult bindingResult) throws InvalidPreScoreParametersException, InvalidPassportDataException, InternalMicroserviceException;
	
	@PostMapping("/offer")
	@Operation(
			summary = "Применение выбранного пользователем кредитного предложения",
			description = """
					Отсылает post-запрос в МС Сделка для указания выбранного кредитного предложения.
					
					Примечание: для отправки запроса через Swagger нужно в поле statementId Request body установить
					значение statementId соответствующей statement из базы данных, или скопировать один из четырёх
					предложений займа, полученных при запросе по адресу: /statement.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "404", description = "Not found"),
					@ApiResponse(responseCode = "500", description = "Internal server error")})
	ResponseEntity<Void> applyOffer(@RequestBody
										   @Parameter(description = "Выбранное пользователем предложение кредита")
										   LoanOfferDto loanOfferDto) throws StatementNotFoundException, InternalMicroserviceException;
}
