package ru.neoflex.neostudy.deal.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.deal.exception.InvalidPreScoreParameters;
import ru.neoflex.neostudy.deal.exception.InvalidScoreParameters;
import ru.neoflex.neostudy.deal.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("${app.rest.prefix}")
@RequiredArgsConstructor
@Log4j2
@Tag(
		name = "Сделка",
		description = "Управление данными сделки")
public class DealController
{
	private final PreScoringService preScoringService;
	private final ScoringService scoringService;
	private final DataService dataService;
	
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
	public ResponseEntity<List<LoanOfferDto>> getLoanOffers(
			@RequestBody @Valid LoanStatementRequestDto loanStatementRequest,
			BindingResult bindingResult) throws InvalidPassportDataException, InvalidPreScoreParameters
	{
		if (bindingResult.hasErrors())
			throw new InvalidPreScoreParameters(bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.reduce((s1, s2) -> s1 + "; " + s2)
					.orElse("Unknown errors"));
		
		Statement statement = dataService.writeData(loanStatementRequest);
		List<LoanOfferDto> offers = preScoringService.getOffers(loanStatementRequest, statement);
		
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
					@ApiResponse(responseCode = "400", description = "Bad request"),
					@ApiResponse(responseCode = "404", description = "Not found")
			})
	public ResponseEntity<Void> applyOffer(@RequestBody @Valid LoanOfferDto loanOffer,
										   BindingResult bindingResult) throws StatementNotFoundException, InvalidPreScoreParameters
	{
		if (bindingResult.hasErrors())
			throw new InvalidPreScoreParameters(bindingResult.getAllErrors().stream()				// TODO а зачем здесь валидация?..
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.reduce((s1, s2) -> s1 + "; " + s2)
					.orElse("Unknown errors"));
		
		dataService.updateStatement(loanOffer);
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
					@ApiResponse(responseCode = "404", description = "Not found")
			})
	public ResponseEntity<Void> calculateLoanParameters(@RequestBody @Valid FinishingRegistrationRequestDto finishingRegistrationRequestDto,
														@PathVariable("statementId") UUID statementId,
														BindingResult bindingResult) throws StatementNotFoundException, InvalidScoreParameters
	{
		if (bindingResult.hasErrors())
			throw new InvalidScoreParameters(bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.reduce((s1, s2) -> s1 + "; " + s2)
					.orElse("Unknown errors"));
		
		Statement statement = dataService.findStatement(statementId);
		scoringService.scoreAndSaveCredit(finishingRegistrationRequestDto, statement);
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
}
