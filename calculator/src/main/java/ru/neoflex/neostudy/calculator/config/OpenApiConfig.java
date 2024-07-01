package ru.neoflex.neostudy.calculator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
		info = @Info(
				title = "REST API MS calculator",
				version = "1.0",
				description = """
						Калькулятор кредита с ежемесячной фиксированной процентной ставкой,
						с аннуитетным ежемесячным платежом, со страховкой
						5 % от суммы кредита при сниженной ставке на 3 %
						""",
				contact = @Contact(url = "https://t.me/dkavtasyev", email = "dkavtasyev@gmail.com")
		),
		servers = {
				@Server(url = "http://localhost:8081")
		}
)
@Configuration
public class OpenApiConfig {

}
