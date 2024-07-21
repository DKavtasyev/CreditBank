package ru.neoflex.neostudy.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@UtilityClass
public class UrlFormatter {
	
	public URI substituteUrlValue(String parameterizedUrl, String value) {
		return UriComponentsBuilder
				.fromUriString(parameterizedUrl)
				.buildAndExpand(value)
				.toUri();
	}
	
	public URI addQueryParameter(String uri, String paramName, String value) {
		return UriComponentsBuilder
				.fromUriString(uri)
				.queryParam(paramName, "{c}")
				.build(value);
	}
}
