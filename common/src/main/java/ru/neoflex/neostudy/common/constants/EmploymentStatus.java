package ru.neoflex.neostudy.common.constants;

import lombok.Getter;

@Getter
public enum EmploymentStatus {
	UNEMPLOYED("безработный"),
	SELF_EMPLOYED("самозанятый"),
	EMPLOYED("трудящийся"),
	BUSINESS_OWNER("бизнесмен");
	
	private final String value;
	EmploymentStatus(String genderValue) {
		this.value = genderValue;
	}
	
}
