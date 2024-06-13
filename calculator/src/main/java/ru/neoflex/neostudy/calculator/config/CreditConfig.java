package ru.neoflex.neostudy.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "credit")
@Getter
@Setter
public class CreditConfig
{
	private String insurancePercent;
	private String minAmount;
	private String minTerm;
}
