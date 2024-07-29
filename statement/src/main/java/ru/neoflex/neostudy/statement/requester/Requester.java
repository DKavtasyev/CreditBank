package ru.neoflex.neostudy.statement.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class Requester {
	private final RestTemplate restTemplate;
	
	public <T, R> ResponseEntity<R> request(T t, ParameterizedTypeReference<R> responseType, String url) {
		RequestEntity<T> requestEntity = getRequestEntity(t, url);
		return restTemplate.exchange(requestEntity, responseType);
	}
	
	private <T> RequestEntity<T> getRequestEntity(T t, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return RequestEntity
				.post(url)
				.headers(headers)
				.body(t);
	}
}
