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

@Service
@RequiredArgsConstructor
public class Requester {
	private final RestTemplate restTemplate;
	
	public static final String CONNECTION_ERROR_TO_MS_DEAL = "Connection error to MS deal";
	public static final String CONNECTION_ERROR_TO_MS_STATEMENT = "Connection error to MS statement";
	
	public <T, R> ResponseEntity<R> request(RequestEntity<T> requestEntity, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(requestEntity, responseType);
	}
	
	protected <T> RequestEntity<T> getRequestEntityWithBody(T t, URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.post(uri)
				.headers(headers)
				.body(t);
	}
	
	protected <T> RequestEntity<T> getRequestEntityWithBodyMethodPut(T t, URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.put(uri)
				.headers(headers)
				.body(t);
	}
	
	protected RequestEntity<Void> getRequestEntityMethodGet(URI uri) {
		return RequestEntity
				.get(uri)
				.build();
	}
	
	protected RequestEntity<Void> getRequestEntity(URI uri) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		return RequestEntity
				.post(uri)
				.headers(headers)
				.build();
	}
}
