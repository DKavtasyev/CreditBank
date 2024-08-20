package ru.neoflex.neostudy.statement.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.InvalidPassportDataException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.util.UrlBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DealRequestService {
	private static final String CONNECTION_ERROR_TO_MS_DEAL = "Connection error to MS deal";
	
	@Value("${app.rest.request.deal.host}")
	private String dealHost;
	@Value("${app.rest.request.deal.port}")
	private String dealPort;
	@Value("${app.rest.request.deal.offers-path}")
	private String offersPath;
	@Value("${app.rest.request.deal.apply-offer-path}")
	private String applyOfferPath;
	
	private final Requester requester;
	
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto) throws InvalidPassportDataException, InternalMicroserviceException {
		ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
		List<LoanOfferDto> offers = new ArrayList<>();
		try {
			offers = getLoanOffers(loanStatementRequestDto, responseType, offers);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
		return offers;
	}
	
	private List<LoanOfferDto> getLoanOffers(LoanStatementRequestDto loanStatementRequestDto, ParameterizedTypeReference<List<LoanOfferDto>> responseType, List<LoanOfferDto> offers) throws InvalidPassportDataException, InternalMicroserviceException {
		try {
			URI uri = UrlBuilder.builder().init("http", dealHost, dealPort).addPath(offersPath).build();
			RequestEntity<LoanStatementRequestDto> requestEntity = requester.getRequestEntity(loanStatementRequestDto, uri);
			ResponseEntity<List<LoanOfferDto>> responseEntity = requester.request(requestEntity, responseType);
			offers = responseEntity.getBody();
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))){
				throw new InvalidPassportDataException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
		return offers;
	}
	
	public void sendChosenOffer(LoanOfferDto loanOfferDto) throws StatementNotFoundException, InternalMicroserviceException {
		ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
		try {
			sendChosenOffer(loanOfferDto, responseType);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
		
	}
	
	private void sendChosenOffer(LoanOfferDto loanOfferDto, ParameterizedTypeReference<Void> responseType) throws StatementNotFoundException, InternalMicroserviceException {
		try {
			URI uri = UrlBuilder.builder().init("http", dealHost, dealPort).addPath(applyOfferPath).build();
			RequestEntity<LoanOfferDto> requestEntity = requester.getRequestEntity(loanOfferDto, uri);
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))){
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
}
