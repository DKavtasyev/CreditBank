package ru.neoflex.neostudy.deal.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

public class ResponseBodyMatcher
{
	private final ObjectMapper objectMapper = new ObjectMapper();
	
	public static ResponseBodyMatcher responseBody()
	{
		return new ResponseBodyMatcher();
	}
	
	public <T> ResultMatcher containsListAsJson(Object expectedObject, Class<T> targetClass)
	{
		return mvcResult -> {
			String json = mvcResult.getResponse().getContentAsString();
			List<T> actualObject = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, targetClass));
			Assertions.assertThat(actualObject).usingRecursiveComparison().isEqualTo(expectedObject);
		};
	}
}
