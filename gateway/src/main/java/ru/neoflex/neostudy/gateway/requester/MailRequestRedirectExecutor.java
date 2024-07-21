package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.common.util.UrlFormatter;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class MailRequestRedirectExecutor {
	private final Requester requester;
	
	public void sendSignDocumentsRequest(String signDocumentsUrl, String statementId, String sesCode) throws StatementNotFoundException, InternalMicroserviceException {
		URI uri = UrlFormatter.substituteUrlValue(signDocumentsUrl, statementId);
		uri = UrlFormatter.addQueryParameter(uri.toString(), "code", sesCode);
		requester.sendRequest(requester.getRequestEntity(uri));
	}
}
