package ru.neoflex.neostudy.statement.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.statement.requester.DealRequestService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
	private final DealRequestService dealRequestService;
	
	/**
	 * Перенаправляет запрос с пользовательскими данными {@code LoanStatementRequestDto} в микросервис deal.
	 * Возвращает List, полученный в ответе от МС deal, содержащий четыре кредитных предложения.
	 * @param loanStatementRequestDto данные пользовательского запроса кредита.
	 * @return список доступных предложений кредита.
	 * @throws InvalidPassportDataException если паспортные данные из LoanStatementRequestDto не совпадают с паспортными
	 * данными клиента из базы данных.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) throws InvalidPassportDataException, InternalMicroserviceException {
		return dealRequestService.requestLoanOffers(loanStatementRequestDto);
	}
	
	/**
	 * Перенаправляет запрос с выбранным пользователем кредитным предложением в микросервис deal.
	 * @param loanOfferDto выбранное пользователем кредитное предложение.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void applyChosenOffer(LoanOfferDto loanOfferDto) throws StatementNotFoundException, InternalMicroserviceException {
		dealRequestService.sendChosenOffer(loanOfferDto);
	}
}
