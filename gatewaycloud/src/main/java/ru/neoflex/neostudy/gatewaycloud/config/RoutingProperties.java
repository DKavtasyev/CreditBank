package ru.neoflex.neostudy.gatewaycloud.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import ru.neoflex.neostudy.gatewaycloud.util.UrlBuilder;

import java.net.URI;

@Getter
@Setter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "route")
public class RoutingProperties {
	public static final String SCHEME = "http";
	
	private final Gateway gateway;
	private final Statement statement;
	private final Deal deal;
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "gateway")
	public static class Gateway {
		private String createLoanStatementPath;
		private String applyOfferPath;
		private String finishRegistrationPath;
		private String denyOfferPath;
		private String createDocumentsPath;
		private String signDocumentsPath;
		private String verifySesCodePath;
		private String updateStatementStatusPath;
		private String getStatementPath;
		private String getAllStatementsPath;
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "statement")
	public static class Statement {
		private String host;
		private String port;
		private String applyOfferPath;
		
		public URI getHost() {
			return UrlBuilder.builder().init(SCHEME, host, port).build();
		}
	}
	
	@Getter
	@Setter
	@ConfigurationProperties(prefix = "deal")
	public static class Deal {
		private String host;
		private String port;
		private String calculateCreditPath;
		private String denyOfferPath;
		private String createDocumentsPath;
		private String signDocumentPath;
		private String verifySignaturePath;
		private String updateStatementStatusPath;
		private String getStatementPath;
		private String getAllStatementsPath;
		
		public URI getHost() {
			return UrlBuilder.builder().init(SCHEME, host, port).build();
		}
	}
}
