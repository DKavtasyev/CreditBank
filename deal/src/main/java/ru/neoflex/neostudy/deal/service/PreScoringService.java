package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreScoringService {
	private final CalculatorRequester calculatorRequester;
	
	/**
	 * Возвращает список кредитных предложений. В каждый элемент кредитного предложения устанавливает идентификатор
	 * заявки {@code Statement}, для которой возвращаются кредитные выражения, и сортирует их по возрастанию от
	 * "худшего" к "лучшему".
	 * @param loanStatementRequest данные запроса кредита от пользователя.
	 * @param statement объект-entity, содержащий все данные по кредиту.
	 * @return список с кредитными предложениями {@code LoanOfferDto}.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС calculator недоступен.
	 */
	@SuppressWarnings("all") // Подавляет замечание в связи с использованием метода peek (кроме него ничего не подходит).
	public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequest, Statement statement) throws InternalMicroserviceException {
		return calculatorRequester.requestLoanOffers(loanStatementRequest)
				.stream()
				.peek(offer -> offer.setStatementId(statement.getStatementId()))
				.sorted((s1, s2) -> s2.getTotalAmount().compareTo(s1.getTotalAmount()))
				.toList();
	}
}
