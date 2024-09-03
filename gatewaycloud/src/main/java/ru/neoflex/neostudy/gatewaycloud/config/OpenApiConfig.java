package ru.neoflex.neostudy.gatewaycloud.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
		info = @Info(
				title = "REST API MS gateway",
				version = "1.0.0",
				description = """
						Модуль реализует паттерн API gateway. Является единой точкой входа для API, предоставляемого
						другими микросервисами. Позволяет инкапсулировать логику внутренней системы, предоставив клиенту
						простой и понятный API.
						""",
				contact = @Contact(url = "https://t.me/dkavtasyev", email = "dkavtasyev@gmail.com")
		),
		servers = {
				@Server(url = "http://localhost:8080")
		})
@Configuration
public class OpenApiConfig {

	@Bean
	public GroupedOpenApi dealApi() {
		return GroupedOpenApi.builder()
				.group("Deal")
				.pathsToMatch("/deal/admin/**", "/deal/document/**", "/deal/calculate/**")
				.build();
	}
	
	@Bean
	public GroupedOpenApi statementApi() {
		return GroupedOpenApi.builder()
				.group("Statement")
				.pathsToMatch("/statement/**", "/statement")
				.build();
	}
}
