package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.dto.FinishingRegistrationRequestDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static ru.neoflex.neostudy.gateway.requester.Requester.*;

/**
 * Сервис, осуществляющий перенаправление запросов, которые были направлены для работы с заявкой на кредит.
 */
@Service
@RequiredArgsConstructor
public class StatementRequestService {
	private final Requester requester;
	
	/**
	 * Отправляет запрос с пользовательскими данными {@code LoanStatementRequestDto} в микросервис statement.
	 * Возвращает List, полученный в теле ответа от МС statement, содержащий четыре кредитных предложения.
	 * @param loanStatementRequestDto данные пользовательского запроса кредита.
	 * @param uri url-адрес, по которому будет отправлен запрос.
	 * @return список доступных предложений кредита.
	 * @throws InvalidUserDataException если данные из пользовательского запроса кредита не прошли прескоринг или
	 * паспортные данные из LoanStatementRequestDto не совпадают с паспортными данными клиента из базы данных.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto, URI uri) throws InvalidUserDataException, InternalMicroserviceException {
		List<LoanOfferDto> offers;
		try {
			offers = sendLoanOffersRequest(loanStatementRequestDto, uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_STATEMENT, e);
		}
		return offers;
	}
	
	private List<LoanOfferDto> sendLoanOffersRequest(LoanStatementRequestDto loanStatementRequestDto, URI uri) throws InvalidUserDataException, InternalMicroserviceException {
		ResponseEntity<List<LoanOfferDto>> responseEntity;
		List<LoanOfferDto> offers = new ArrayList<>();
		try {
			ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
			RequestEntity<LoanStatementRequestDto> requestEntity = requester.getRequestEntityWithBody(loanStatementRequestDto, uri);
			responseEntity = requester.request(requestEntity, responseType);
			offers = responseEntity.getBody();
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(400))) {
				throw new InvalidUserDataException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
		return offers;
	}
	
	/**
	 * Отправляет запрос с выбранным пользователем кредитным предложением в микросервис statement.
	 * @param loanOfferDto выбранное пользователем кредитное предложение.
	 * @param uri url-адрес, по которому будет отправлен запрос.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных (ответ от МС statement пришёл с кодом 404 Not found).
	 */
	public void sendChosenOffer(LoanOfferDto loanOfferDto, URI uri) throws InternalMicroserviceException, StatementNotFoundException {
		try {
			RequestEntity<LoanOfferDto> requestEntity = requester.getRequestEntityWithBody(loanOfferDto, uri);
			sendRequest(requestEntity);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_STATEMENT, e);
		}
	}
	
	public void requestForFinishRegistration(FinishingRegistrationRequestDto finishingRegistrationRequestDto, URI uri) throws InternalMicroserviceException, StatementNotFoundException, LoanRefusalException, InvalidPreApproveException {
		try {
			sendFinishRegistrationRequest(finishingRegistrationRequestDto, uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	private void sendFinishRegistrationRequest(FinishingRegistrationRequestDto finishingRegistrationRequestDto, URI uri) throws StatementNotFoundException, LoanRefusalException, InvalidPreApproveException, InternalMicroserviceException {
		try {
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			RequestEntity<FinishingRegistrationRequestDto> requestEntity = requester.getRequestEntityWithBody(finishingRegistrationRequestDto, uri);
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(406))) {
				throw new LoanRefusalException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(428))) {
				throw new InvalidPreApproveException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
	
	/**
	 * Отправляет запрос с отказом пользователя от кредита в МС deal.
	 * @param uri url-адрес, по которому будет отправлен запрос.
	 * @throws StatementNotFoundException выбрасывается, если {@code Statement} с указанным идентификатором statementId
	 * не найден в базе данных.
	 * @throws InternalMicroserviceException при запросе возникла ошибка или один из микросервисов оказался недоступен.
	 */
	public void sendOfferDenial(URI uri) throws StatementNotFoundException, InternalMicroserviceException {
		try {
			RequestEntity<Void> requestEntity = requester.getRequestEntityMethodGet(uri);
			sendRequest(requestEntity);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	private <T> void sendRequest(RequestEntity<T> requestEntity) throws StatementNotFoundException, InternalMicroserviceException {
		try {
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
}
