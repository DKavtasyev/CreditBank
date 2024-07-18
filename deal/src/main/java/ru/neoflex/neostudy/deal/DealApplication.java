package ru.neoflex.neostudy.deal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.neoflex.neostudy.common.exception.StatementNotFoundException;
import ru.neoflex.neostudy.deal.entity.Statement;
import ru.neoflex.neostudy.deal.service.DataService;

import java.util.UUID;

@SpringBootApplication
public class DealApplication {
	
	public static void main(String[] args) throws StatementNotFoundException, JsonProcessingException {
		ConfigurableApplicationContext context = SpringApplication.run(DealApplication.class, args);
		DataService service = context.getBean(DataService.class);
		ObjectMapper mapper = context.getBean(ObjectMapper.class);
		
		Statement statement = service.findStatement(UUID.fromString("e971fdc1-bfb4-4ff0-9f27-6e661c121e68"));
		String statementAsString = mapper.writeValueAsString(statement);
		System.out.println(statementAsString);
		
	}
}
