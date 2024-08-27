package ru.neoflex.neostudy.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.neoflex.neostudy.common.constants.DateTimeFormat;
import ru.neoflex.neostudy.common.validation.CheckAge;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
@Builder
public class LoanStatementRequestDto {
	@NotNull(message = "Запрашиваемая сумма не может быть пустой")
	@DecimalMin(value = "30000", message = "Запрашиваемая сумма должна быть не менее 30000")
	@Schema(description = "Запрашиваемая сумма", example = "1000000")
	private BigDecimal amount;
	
	@NotNull(message = "Запрашиваемый срок не может быть пустым")
	@Min(value = 6, message = "Запрашиваемый срок кредитования должен быть не менее 6 месяцев")
	@Schema(description = "Запрашиваемый срок кредитования", example = "12")
	private Integer term;
	
	@NotBlank(message = "Имя не может состоять из пробелов или быть пустым")
	@Size(min = 2, max = 30, message = "Имя должно быть от 2 до 30 символов")
	@Schema(description = "Имя", example = "Ivan")
	private String firstName;
	
	@NotBlank(message = "Фамилия не может состоять из пробелов или быть пустой")
	@Size(min = 2, max = 30, message = "Фамилия должна быть от 2 до 30 символов")
	@Schema(description = "Фамилия", example = "Ivanov")
	private String lastName;
	
	@Size(min = 2, max = 30, message = "Отчество должно быть от 2 до 30 символов")
	@Schema(description = "Отчество", example = "Ivanovich")
	private String middleName;
	
	@NotNull(message = "Электронный адрес не может быть пустым")
	@Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Электронный адрес должен быть правильного формата")
	@Schema(description = "email", example = "ivan@mail.ru")
	private String email;
	
	@NotNull(message = "Дата рождения не может быть пустой")
	@CheckAge(value = 18, message = "Клиент не может быть моложе 18 лет")
	@Schema(description = "Дата рождения", example = "1995-03-14")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeFormat.DATE_PATTERN)
	private LocalDate birthDate;
	
	@NotNull(message = "Номер паспорта не может быть пустым")
	@Pattern(regexp = "^([1-9][0-9]{3})$", message = "Серия паспорта должна состоять из четырёх цифр")
	@Schema(description = "Серия паспорта", example = "1234")
	private String passportSeries;
	
	@NotNull(message = "Серия паспорта не может быть пустой")
	@Pattern(regexp = "^([1-9][0-9]{5})$", message = "Номер паспорта должен состоять из шести цифр")
	@Schema(description = "Номер паспорта", example = "123456")
	private String passportNumber;
}
