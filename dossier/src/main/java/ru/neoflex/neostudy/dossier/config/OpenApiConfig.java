package ru.neoflex.neostudy.dossier.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
		info = @Info(
				title = "REST API MS dossier",
				version = "1.0.0",
				description = """
						Модуль оповещения пользователя о событиях оформления кредита по электронной почте
						""",
				contact = @Contact(url = "https://t.me/dkavtasyev", email = "dkavtasyev@gmail.com")
		),
		servers = {
				@Server(url = "http://localhost:8084")
		}
)
@Configuration
public class OpenApiConfig {
}
