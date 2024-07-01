package ru.neoflex.neostudy.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.requester.CalculatorRequester;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreScoringService {
	private final CalculatorRequester calculatorRequester;
	
	public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequest, Statement statement) {
		return calculatorRequester.requestLoanOffers(loanStatementRequest)
				.stream()
				.map(offer -> offer.setStatementId(statement.getStatementId()))
				.sorted((s1, s2) -> s2.getTotalAmount().compareTo(s1.getTotalAmount()))
				.toList();
	}
}
