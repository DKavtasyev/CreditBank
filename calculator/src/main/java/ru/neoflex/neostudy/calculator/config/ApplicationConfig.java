package ru.neoflex.neostudy.calculator.config;


import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(value = {
		RateConfig.class,
		RateConfig.EmploymentStatusConfig.class,
		RateConfig.Position.class,
		RateConfig.MaritalStatus.class,
		RateConfig.Woman.class,
		RateConfig.Man.class,
		RateConfig.NonBinary.class,
		CreditConfig.class,
		RefusalConfig.class,
		RefusalConfig.WorkExperience.class})
public class ApplicationConfig {
}
