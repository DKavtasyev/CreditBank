package ru.neoflex.neostudy.gateway.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.gateway.requester.MailRequestRedirectExecutor;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class UIController {
	private final MailRequestRedirectExecutor redirectExecutor;
	
	@Value("${server.port}")
	private String port;
	@Value("${app.rest.prefix.document}")
	private String documentPathPrefix;
	
	@GetMapping("/document/{statementId}/sign/code")
	public String signPage(@PathVariable("statementId") String statementId,
						   @RequestParam("code") String sesCode,
						   Model model){
		String signDocumentsUrl = "http://localhost:" + port + documentPathPrefix + "/{statementId}/sign/code";
		String messageText = "Документ успешно подписан!";
		try {
			redirectExecutor.sendSignDocumentsRequest(signDocumentsUrl, statementId, sesCode);
		}
		catch (StatementNotFoundException e) {
			messageText = "Ошибка. Заявка на кредит не найдена.";
		}
		catch (InternalMicroserviceException e) {
			messageText = "Ошибка на сервере: " + e.getMessage();
		}
		model.addAttribute("currentDate", LocalDate.now());
		model.addAttribute("messageText", messageText);
		return "sign-page";
	}
}
