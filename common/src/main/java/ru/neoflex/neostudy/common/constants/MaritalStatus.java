package ru.neoflex.neostudy.common.constants;

import lombok.Getter;

/**
 * Значения семейного положения клиента.
 */
@Getter
public enum MaritalStatus {
	MARRIED("женат/замужем"),
	DIVORCED("разведён(а)"),
	SINGLE("не женат/не замужем"),
	WIDOW_WIDOWER("вдовец/вдова");
	
	private final String value;
	MaritalStatus(String genderValue) {
		this.value = genderValue;
	}
}
