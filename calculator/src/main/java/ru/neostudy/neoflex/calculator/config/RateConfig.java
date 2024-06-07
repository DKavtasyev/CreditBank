package ru.neostudy.neoflex.calculator.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "rate")
public class RateConfig
{
	private String baseRate;
	private String insuranceEnabled;
	private String salaryClient;
	
	public RateConfig(String baseRate, String insuranceEnabled, String salaryClient, EmploymentStatusConfig employmentStatus, Position position, MaritalStatus maritalStatus, Woman woman, Man man, NonBinary nonBinary)
	{
		this.baseRate = baseRate;
		this.insuranceEnabled = insuranceEnabled;
		this.salaryClient = salaryClient;
		this.employmentStatus = employmentStatus;
		this.position = position;
		this.maritalStatus = maritalStatus;
		this.woman = woman;
		this.man = man;
		this.nonBinary = nonBinary;
	}
	
	private final EmploymentStatusConfig employmentStatus;
	private final Position position;
	private final MaritalStatus maritalStatus;
	private final Woman woman;
	private final Man man;
	private final NonBinary nonBinary;
	
	@ConfigurationProperties(prefix = "employment-status")
	@Getter
	@Setter
	public static class EmploymentStatusConfig
	{
		private String selfEmployed;
		private String businessOwner;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "position")
	public static class Position
	{
		private String middleManager;
		private String topManager;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "marital-status")
	public static class MaritalStatus
	{
		private String married;
		private String divorced;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "woman")
	public static class Woman
	{
		private int ageFrom;
		private int ageTo;
		private String rate;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "man")
	public static class Man
	{
		private int ageFrom;
		private int ageTo;
		private String rate;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "non-binary")
	public static class NonBinary
	{
		private String rate;
	}
}
