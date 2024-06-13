package ru.neostudy.neoflex.deal.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ru.neoflex.neostudy.deal.controller.DealController;
import ru.neoflex.neostudy.deal.service.DataService;
import ru.neoflex.neostudy.deal.service.PreScoringService;
import ru.neoflex.neostudy.deal.service.ScoringService;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = DealController.class)
public class DealControllerTest
{
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockBean
	private PreScoringService preScoringService;
	@MockBean
	private ScoringService scoringService;
	@MockBean
	private DataService dataService;
	
	@Nested
	@DisplayName("Тестирование метода DealController:getLoanOffers()")
	class TestingGetLoanOffersMethod
	{
	
	}
	
	
}
