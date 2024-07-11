package ru.neoflex.neostudy.statement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.InvalidPreScoreParametersException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.statement.requester.DealRequester;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
	private final DealRequester dealRequester;
	
	public List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto) throws InvalidPassportDataException, JsonProcessingException {
		return dealRequester.requestLoanOffers(loanStatementRequestDto);
	}
	
	public void applyChosenOffer(LoanOfferDto loanOfferDto) throws StatementNotFoundException, JsonProcessingException, InvalidPreScoreParametersException {
		dealRequester.sendChosenOffer(loanOfferDto);
	}
}
