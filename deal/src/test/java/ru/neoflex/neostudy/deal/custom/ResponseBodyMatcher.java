package ru.neoflex.neostudy.deal.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.ResultMatcher;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ResponseBodyMatcher {
	private final ObjectMapper objectMapper;
	
	public static ResponseBodyMatcher responseBody(ObjectMapper objectMapper) {
		return new ResponseBodyMatcher(objectMapper);
	}
	
	public ResponseBodyMatcher(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}
	
	public <T> ResultMatcher containsListAsJson(Object expectedObject, Class<T> targetClass) {
		return mvcResult -> {
			String json = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
			List<T> actualObject = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
			assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
		};
	}
	
	public <T> ResultMatcher containsObjectAsJson(Object expectedObject, Class<T> targetClass) {
		return mvcResult -> {
			String json = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
			T actualObject = objectMapper.readValue(json, targetClass);
			assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
		};
	}
}
