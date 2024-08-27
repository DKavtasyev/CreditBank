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
	
	public <T, R> ResponseEntity<R> request(RequestEntity<T> requestEntity, ParameterizedTypeReference<R> responseType) {
		return restTemplate.exchange(requestEntity, responseType);
	}
	
	public <T> RequestEntity<T> getRequestEntity(T t, String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		return RequestEntity
				.post(url)
				.headers(headers)
				.body(t);
	}
}
