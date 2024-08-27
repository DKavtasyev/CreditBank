package ru.neoflex.neostudy.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
		info = @Info(
				title = "REST API MS gateway",
				version = "0.0.1",
				description = """
						Модуль, выполняющий функции фасада для всех остальных модулей. Позволяет инкапсулировать логику
						внутренней системы, предоставив клиенту простой и понятный API.
						""",
				contact = @Contact(url = "https://t.me/dkavtasyev", email = "dkavtasyev@gmail.com")
		),
		servers = {
				@Server(url = "http://localhost:8080")
		}
)
@Configuration
public class OpenApiConfig {
}
