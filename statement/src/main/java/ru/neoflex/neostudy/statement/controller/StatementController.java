package ru.neoflex.neostudy.statement.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.statement.service.StatementService;

import java.util.List;

@RestController
@RequestMapping("${app.rest.prefix}")
@RequiredArgsConstructor
@Tag(
		name = "Заявка",
		description = "Оформление заявки на кредит")
public class StatementController {
	private final StatementService statementService;
	
	@PostMapping("")
	@Operation(
			summary = "Предоставление возможных предложений займа",
			description = "Получает запрос от пользователя на предварительный расчёт кредита, проверяет полученные данные, " +
					"делает запрос возможных условий в МС Сделка и предоставляет информацию " +
					"пользователю для выбора.",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request")})
	public ResponseEntity<List<LoanOfferDto>> getLoanOffers(@RequestBody @Valid @Parameter(description = "Пользовательские данные для предварительного расчёта кредита") LoanStatementRequestDto loanStatementRequestDto,
															BindingResult bindingResult) throws InvalidPreScoreParametersException, InvalidPassportDataException, JsonProcessingException {
		if (bindingResult.hasErrors()) {
			throw new InvalidPreScoreParametersException(bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.reduce((s1, s2) -> s1 + "; " + s2)
					.orElse("Unknown errors"));
		}
		
		List<LoanOfferDto> offers = statementService.getLoanOffers(loanStatementRequestDto);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@PostMapping("/offer")
	@Operation(
			summary = "Применение выбранного пользователем кредитного предложения",
			description = """
					Отсылает post-запрос в МС Сделка для .
					
					Примечание: для отправки запроса через Swagger нужно в поле statementId Request body установить
					значение statementId соответствующей statement из базы данных, или скопировать один из четырёх
					предложений займа, полученных при запросе по адресу: /statement.
					""",
			responses = {
					@ApiResponse(responseCode = "200", description = "Success"),
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "404", description = "Not found")
			})
	public ResponseEntity<Void> applyOffer(@RequestBody @Parameter(description = "Выбранное пользователем предложение кредита") LoanOfferDto loanOfferDto) throws StatementNotFoundException, JsonProcessingException, InvalidPreScoreParametersException {
		statementService.applyChosenOffer(loanOfferDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
