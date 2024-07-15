package ru.neoflex.neostudy.dossier.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties(AppConfig.class)
public class ApplicationConfig {
	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
