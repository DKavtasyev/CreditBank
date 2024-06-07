package ru.neostudy.neoflex.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "refusal")
public class RefusalConfig
{
	private int ratioOfAmountToSalary;
	private int minAge;
	private int maxAge;
	
	private final WorkExperience workExperience;
	
	public RefusalConfig(int ratioOfAmountToSalary, int minAge, int maxAge, WorkExperience workExperience)
	{
		this.ratioOfAmountToSalary = ratioOfAmountToSalary;
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.workExperience = workExperience;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "work-experience")
	public static class WorkExperience
	{
		private int minTotal;
		private int minCurrent;
	}
}
