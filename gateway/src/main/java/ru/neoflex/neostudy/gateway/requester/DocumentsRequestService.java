package ru.neoflex.neostudy.gateway.requester;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import ru.neoflex.neostudy.common.exception.*;

import java.net.URI;
import java.util.Objects;

import static ru.neoflex.neostudy.gateway.requester.Requester.CONNECTION_ERROR_TO_MS_DEAL;

@SuppressWarnings("DuplicatedCode")
@Service
@RequiredArgsConstructor
public class DocumentsRequestService {
	private final Requester requester;
	
	public void requestCreatingDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		try {
			sendRequest(uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	public void requestSignatureOfDocuments(URI uri) throws StatementNotFoundException, InternalMicroserviceException, DocumentSignatureException {
		try {
			sendRequest(uri);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	public void requestVerifyingSesCode(URI uri) throws StatementNotFoundException, InternalMicroserviceException, SignatureVerificationFailedException, DocumentSignatureException {
		RequestEntity<Void> requestEntity = requester.getRequestEntity(uri);
		
		try {
			sendRequestToVerifySesCode(requestEntity);
		}
		catch (RestClientException e) {
			throw new InternalMicroserviceException(CONNECTION_ERROR_TO_MS_DEAL, e);
		}
	}
	
	private void sendRequestToVerifySesCode(RequestEntity<Void> requestEntity) throws SignatureVerificationFailedException, StatementNotFoundException, DocumentSignatureException, InternalMicroserviceException {
		try {
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(401))) {
				throw new SignatureVerificationFailedException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(422))) {
				throw new DocumentSignatureException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
	
	private void sendRequest(URI uri) throws StatementNotFoundException, DocumentSignatureException, InternalMicroserviceException {
		try {
			RequestEntity<Void> requestEntity = requester.getRequestEntity(uri);
			ParameterizedTypeReference<Void> responseType = new ParameterizedTypeReference<>() {};
			requester.request(requestEntity, responseType);
		}
		catch (HttpClientErrorException e) {
			ExceptionDetails exceptionDetails = e.getResponseBodyAs(ExceptionDetails.class);
			Objects.requireNonNull(exceptionDetails);
			if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
				throw new StatementNotFoundException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(422))) {
				throw new DocumentSignatureException(exceptionDetails.getMessage());
			}
			else if (e.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(500))) {
				throw new InternalMicroserviceException(exceptionDetails.getMessage());
			}
		}
	}
}
