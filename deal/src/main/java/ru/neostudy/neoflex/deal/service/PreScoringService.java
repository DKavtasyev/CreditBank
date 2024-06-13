package ru.neostudy.neoflex.deal.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neostudy.neoflex.deal.dto.LoanOfferDto;
import ru.neostudy.neoflex.deal.dto.LoanStatementRequestDto;
import ru.neostudy.neoflex.deal.entity.Statement;
import ru.neostudy.neoflex.deal.requester.CalculatorRequester;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PreScoringService
{
	private final CalculatorRequester calculatorRequester;
	
	public List<LoanOfferDto> getOffers(LoanStatementRequestDto loanStatementRequest, Statement statement)
	{
		return calculatorRequester.requestLoanOffers(loanStatementRequest)
				.stream()
				.map(offer -> offer.setStatementId(statement.getStatementId()))
				.sorted((s1, s2) -> s2.getTotalAmount().compareTo(s1.getTotalAmount()))
				.toList();
	}
}
