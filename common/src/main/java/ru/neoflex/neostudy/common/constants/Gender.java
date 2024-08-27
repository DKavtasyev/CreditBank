package ru.neoflex.neostudy.common.constants;

import lombok.Getter;

@Getter
public enum Gender {
	MALE("мужской"),
	FEMALE("женский"),
	NON_BINARY("небинарный");
	
	private final String value;
	Gender(String genderValue) {
		this.value = genderValue;
	}
}
