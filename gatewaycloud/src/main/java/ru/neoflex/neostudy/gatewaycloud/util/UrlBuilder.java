package ru.neoflex.neostudy.gatewaycloud.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * Утилитный класс, который содержит в себе класс-билдер для сборки URI-адреса.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlBuilder {
	private UriComponentsBuilder uriComponentsBuilder;
	
	/**
	 * Статический метод для создания и вызова объекта Builder
	 * @return {@code this}, объект Builder.
	 */
	public static Builder builder() {
		return new Builder();
	}
	
	private URI getUrlAddress() {
		return uriComponentsBuilder.encode().build().toUri();
	}
	
	/**
	 * Класс-билдер, служащий для облегчения сборки URI-адреса из составных частей.
	 */
	public static class Builder {
		private UriComponentsBuilder uriComponentsBuilder;
		
		/**
		 * Инициализирует обязательными атрибутами адреса и возвращает Builder для URL-адреса.
		 * @param scheme http-протокол.
		 * @param host хост, входящий в состав URL-адреса.
		 * @param port порт.
		 * @return {@code this}, объект Builder.
		 */
		public Builder init(String scheme, String host, String port) {
			uriComponentsBuilder = UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port);
			return this;
		}
		
		/**
		 * Собирает и возвращает построенный с помощью Builder URI-адрес.
		 * @return построенный URI-адрес.
		 */
		public URI build() {
			UrlBuilder urlBuilder = new UrlBuilder();
			urlBuilder.uriComponentsBuilder = uriComponentsBuilder;
			return urlBuilder.getUrlAddress();
		}
	}
}
