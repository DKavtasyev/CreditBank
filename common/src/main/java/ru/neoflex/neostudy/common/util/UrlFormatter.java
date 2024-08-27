package ru.neoflex.neostudy.common.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Утилитный класс, помогающий составлять URI-адреса.
 */
@UtilityClass
public class UrlFormatter {
	
	/**
	 * Подставляет вместо переменной в адресе запроса, имя которой указано в фигурных скобках {}, указанное значение.
	 * @param parameterizedUrl - URL-адрес, содержащий переменную.
	 * @param value - значение переменной, которое необходимо подставить в url-адрес.
	 * @return адрес в формате URI с подставленным значением переменной запроса.
	 */
	public URI substituteUrlValue(String parameterizedUrl, String value) {
		return UriComponentsBuilder
				.fromUriString(parameterizedUrl)
				.buildAndExpand(value)
				.toUri();
	}
	
	/**
	 * Добавляет к URL-адресу параметр запроса query param.
	 * @param uri - URL-адрес, к которому нужно добавить параметр.
	 * @param paramName - имя добавляемого параметра.
	 * @param value - значение добавляемого параметра.
	 * @return адрес в формате URI с добавленным параметром запроса.
	 */
	public URI addQueryParameter(String uri, String paramName, String value) {
		return UriComponentsBuilder
				.fromUriString(uri)
				.queryParam(paramName, "{c}")
				.build(value);
	}
}
