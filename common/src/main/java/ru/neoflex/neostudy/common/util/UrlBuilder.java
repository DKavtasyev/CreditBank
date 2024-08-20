package ru.neoflex.neostudy.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.web.util.UriComponents;
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
		 * Инициализирует Builder с помощью готового объекта UriComponentsBuilder.
		 * @param uriComponentsBuilder объект для построения адреса из UriComponents.
		 * @return {@code this}, объект Builder.
		 */
		public Builder init(UriComponentsBuilder uriComponentsBuilder) {
			this.uriComponentsBuilder = uriComponentsBuilder;
			return this;
		}
		
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
		 * К существующему объекту билдера добавляет часть path URL-адреса.
		 * @param path часть, добавляемая к билдеру адреса.
		 * @return {@code this}, объект Builder.
		 */
		public Builder addPath(String path) {
			uriComponentsBuilder.path(path);
			return this;
		}
		
		/**
		 * К существующему объекту билдера добавляет часть path URL-адреса и подставляет указанное значение вместо
		 * переменной в path запроса, имя которой указано в фигурных скобках.
		 * @param path часть path URL-адреса, содержащая переменную.
		 * @param pathVariable значение переменной, которое необходимо подставить в url-адрес.
		 * @return {@code this}, объект Builder.
		 */
		public Builder addPath(String path, String pathVariable) {
			UriComponents uriComponents = UriComponentsBuilder.fromPath(path).buildAndExpand(pathVariable);
			uriComponentsBuilder.uriComponents(uriComponents);
			return this;
		}
		
		/**
		 * Добавляет к билдеру имя и значение query-параметра.
		 * @param parameter имя query-параметра.
		 * @param value значение query-параметра.
		 * @return {@code this}, объект Builder.
		 */
		public Builder addQueryParameter(String parameter, String value) {
			uriComponentsBuilder.queryParam(parameter, value);
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
