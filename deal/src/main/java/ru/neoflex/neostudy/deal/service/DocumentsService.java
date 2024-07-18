package ru.neoflex.neostudy.deal.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.neostudy.common.exception.InternalMicroserviceException;
import ru.neoflex.neostudy.deal.entity.Statement;

@Service
@RequiredArgsConstructor
public class DocumentsService {
	
	private final ObjectMapper mapper;
	
	public String formDocument(Statement statement) throws InternalMicroserviceException {
		statement.setSignDate(null)
				.setStatementStatusHistory(null)
				.setStatus(null)
				.setSessionCode(null);
		try {
			mapper.enable(SerializationFeature.INDENT_OUTPUT);
			return mapper.writeValueAsString(statement);
		}
		catch (JsonProcessingException e) {
			throw new InternalMicroserviceException("Can't serialize credit documents", e);
		}
		finally {
			mapper.disable(SerializationFeature.INDENT_OUTPUT);
		}
	}
}
