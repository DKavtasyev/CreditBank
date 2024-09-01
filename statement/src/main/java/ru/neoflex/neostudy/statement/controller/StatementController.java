package ru.neoflex.neostudy.statement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.statement.controller.annotations.StatementControllerInterface;
import ru.neoflex.neostudy.statement.service.StatementService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatementController implements StatementControllerInterface {
	private final StatementService statementService;
	
	/**
	 * Перенаправляет запрос с пользовательскими данными {@code LoanStatementRequestDto} в микросервис deal. Принимает в
	 * теле запроса объект {@code LoanStatementRequestDto}, проводит прескоринг данных, содержащихся в нём. Возвращает
	 * объект {@code Statement}, полученный в ответе от МС deal.
	 * @param loanStatementRequestDto данные пользовательского запроса кредита.
	 * @return ответ {@code ResponseEntity} со статусом 200 и со списком с рассчитанными кредитными предложениями типа
	 * {@code LoanOfferDto} в теле ответа.
	 */
	@Override
	public ResponseEntity<List<LoanOfferDto>> createStatement(LoanStatementRequestDto loanStatementRequestDto, BindingResult bindingResult) throws InvalidPreScoreParametersException, InvalidPassportDataException, InternalMicroserviceException {
		if (bindingResult.hasErrors()) {
			throw new InvalidPreScoreParametersException(bindingResult.getAllErrors().stream()
					.map(DefaultMessageSourceResolvable::getDefaultMessage)
					.reduce((s1, s2) -> s1 + "; " + s2)
					.orElse("Unknown errors"));
		}
		
		List<LoanOfferDto> offers = statementService.getLoanOffers(loanStatementRequestDto);
		return new ResponseEntity<>(offers, HttpStatus.OK);
	}
	
	@Override
	public ResponseEntity<Void> applyOffer(LoanOfferDto loanOfferDto) throws StatementNotFoundException, InternalMicroserviceException {
		statementService.applyChosenOffer(loanOfferDto);
		return new ResponseEntity<>(HttpStatus.OK);
	}
}
