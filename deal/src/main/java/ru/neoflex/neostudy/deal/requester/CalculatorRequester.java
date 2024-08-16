package ru.neoflex.neostudy.deal.requester;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.neoflex.neostudy.common.dto.CreditDto;
import ru.neoflex.neostudy.common.dto.LoanOfferDto;
import ru.neoflex.neostudy.common.dto.LoanStatementRequestDto;
import ru.neoflex.neostudy.common.dto.ScoringDataDto;
import ru.neoflex.neostudy.common.exception.dto.ExceptionDetails;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.LoanRefusalException;

import java.net.URI;
import java.util.List;

/**
 * Сервис для формирования и отправки запросов в МС Calculator.
 */
@Service
@RequiredArgsConstructor
public class CalculatorRequester {
	private static final String OFFERS_URL = "http://localhost:8081/calculator/offers";
	private static final String CREDIT_URL = "http://localhost:8081/calculator/calc";
	
	private final RestTemplate restTemplate;
	private final ObjectMapper objectMapper;
	
	/**
	 * Отправляет в микросервис calculator запрос с данными в теле запроса типа {@code LoanStatementRequestDto}, которые
	 * получены от пользователя при ега запросе на выдачу кредита. Возвращает List, полученный в ответе от МС calculator,
	 * содержащий четыре кредитных предложения.
	 * @param loanStatementRequestDto данные пользовательского запроса кредита.
	 * @return список доступных предложений кредита.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС calculator недоступен.
	 */
	public List<LoanOfferDto> requestLoanOffers(LoanStatementRequestDto loanStatementRequestDto) throws InternalMicroserviceException {
		ParameterizedTypeReference<List<LoanOfferDto>> responseType = new ParameterizedTypeReference<>() {};
		RequestEntity<LoanStatementRequestDto> requestEntity = getRequestEntity(loanStatementRequestDto, OFFERS_URL);
		
		ResponseEntity<List<LoanOfferDto>> responseEntity;
		try {
			responseEntity = restTemplate.exchange(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			throw new InternalMicroserviceException("Calculator error", e);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS calculator", e);
		}
		return responseEntity.getBody();
	}
	
	/**
	 * Отправляет запрос в МС calculator с пользовательскими данными {@code ScoringDataDto} для расчёта кредита в
	 * теле запроса, десериализует из тела ответа и возвращает объект CreditDto, содержащий данные о графике и суммах
	 * платежей по кредиту.
	 * @param scoringDataDto данные от пользователя для расчёта кредита.
	 * @return объект типа CreditDto, содержащий все данные о сроках и суммах платежей по кредиту.
	 * @throws LoanRefusalException ответ от МС calculator пришёл со статусом 406 Not acceptable.
	 * @throws InternalMicroserviceException если при запросе возникла ошибка или МС calculator недоступен.
	 */
	public CreditDto requestCalculatedCredit(ScoringDataDto scoringDataDto) throws LoanRefusalException, InternalMicroserviceException {
		RequestEntity<ScoringDataDto> requestEntity = getRequestEntity(scoringDataDto, CREDIT_URL);
		
		CreditDto creditDto;
		try {
			creditDto = requestCreditDto(requestEntity);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException("Connection error to MS calculator", e);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't deserialize value", e);
		}
		return creditDto;
	}
	
	/**
	 * Возвращает сформированный параметризованный типом аргумента метод t {@code RequestEntity} с методом запроса
	 * POST, адресом запроса url, телом запроса t.
	 * @param t объект, записываемый в тело запроса.
	 * @param url адрес, по которому будет произведён запрос.
	 * @param <T> тип аргумента t.
	 * @return {@code RequestEntity<T>}.
	 */
	private <T> RequestEntity<T> getRequestEntity(T t, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return RequestEntity
				.post(URI.create(url))
				.headers(headers)
				.body(t);
	}
	
	/**
	 * Производит запрос в соответствии с переданным RequestEntity, принимает ответ, десериализует из тела ответа и
	 * возвращает объект CreditDto, содержащий данные о графике и суммах платежей по кредиту.
	 * @param requestEntity сформированный объект запроса {@code RequestEntity}.
	 * @return объект типа CreditDto, содержащий все данные о сроках и суммах платежей по кредиту.
	 * @throws JsonProcessingException если не удалось десериализовать значение из тела принятого ответа.
	 * @throws LoanRefusalException ответ от МС calculator пришёл со статусом 406 Not acceptable.
	 * @throws InternalMicroserviceException если данные на входе калькулятора имеют некорректный формат.
	 */
	private CreditDto requestCreditDto(RequestEntity<ScoringDataDto> requestEntity) throws JsonProcessingException, LoanRefusalException, InternalMicroserviceException {
		ResponseEntity<String> responseEntity;
		CreditDto creditDto = null;
		try {
			responseEntity = restTemplate.exchange(requestEntity, String.class);
			creditDto = objectMapper.readValue(responseEntity.getBody(), CreditDto.class);
		}
		catch (HttpClientErrorException e) {
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(406))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new LoanRefusalException(exceptionDetails.getMessage());
			}
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))){
				ExceptionDetails exceptionDetails = objectMapper.readValue(e.getResponseBodyAsString(), ExceptionDetails.class);
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
		return creditDto;
	}
}
