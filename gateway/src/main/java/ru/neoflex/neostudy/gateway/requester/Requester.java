package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

/**
 * Сервис нижнего уровня, осуществляющий формирование запросов к другим МС и их выполнение.
 */
@Service
@RequiredArgsConstructor
public class Requester {
	private final RestTemplate restTemplate;
	
	public static final String CONNECTION_ERROR_TO_MS_DEAL = "Connection error to MS deal";
	public static final String CONNECTION_ERROR_TO_MS_STATEMENT = "Connection error to MS statement";
	
	/**
	 * Выполняет HTTP-запрос к другому сервису.
	 * @param requestEntity сформированный для выполнения запроса объект {@code RequestEntity} с типом T в теле
	 * запроса.
	 * @param responseType объект ParameterizedTypeReference, содержащий в себе тип возвращаемого значения {@code R}.
	 * @param <T> тип тела запроса.
	 * @param <R> тип тела ответа.
	 * @return объект {@code ResponseEntity<R>}, полученный при выполнении запроса, содержащий в себе запрашиваемый
	 * объект R.
	 */
	public <T, R> ResponseEntity<R> request(RequestEntity<T> requestEntity, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(requestEntity, responseType);
	}
	
	/**
	 * Возвращает объект RequestEntity с заголовком 'Content-Type: application/json', HTTP-методом POST, с переданным в
	 * параметрах метода URL-адресом, с объектом t в теле запроса.
	 * @param t объект, записываемый в тело запроса.
	 * @param uri URL-адрес, по которому будет отправлен запрос.
	 * @return сформированный объект {@code RequestEntity<T>}.
	 * @param <T> тип тела запроса.
	 */
	protected <T> RequestEntity<T> getRequestEntityWithBody(T t, URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.post(uri)
				.headers(headers)
				.body(t);
	}
	
	/**
	 * Возвращает объект RequestEntity с заголовком 'Content-Type: application/json', HTTP-методом PUT, с переданным в
	 * параметрах метода URL-адресом, с объектом t в теле запроса.
	 * @param t объект, записываемый в тело запроса.
	 * @param uri URL-адрес, по которому будет отправлен запрос.
	 * @return сформированный объект {@code RequestEntity<T>}.
	 * @param <T> тип тела запроса.
	 */
	protected <T> RequestEntity<T> getRequestEntityWithBodyMethodPut(T t, URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.put(uri)
				.headers(headers)
				.body(t);
	}
	
	/**
	 * Возвращает объект RequestEntity с HTTP-методом GET, с переданным в параметрах метода URL-адресом.
	 * @param uri URL-адрес, по которому будет отправлен запрос.
	 * @return сформированный объект {@code RequestEntity<T>}.
	 */
	protected RequestEntity<Void> getRequestEntityMethodGet(URI uri) {
		return RequestEntity
				.get(uri)
				.build();
	}
	
	/**
	 * Возвращает объект RequestEntity с заголовком 'Content-Type: application/json', HTTP-методом POST, с переданным в
	 * параметрах метода URL-адресом, с пустым телом запроса.
	 * @param uri URL-адрес, по которому будет отправлен запрос.
	 * @return сформированный объект {@code RequestEntity<T>}.
	 */
	protected RequestEntity<Void> getRequestEntity(URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.post(uri)
				.headers(headers)
				.build();
	}
}
