package ru.neoflex.neostudy.gatewaycloud.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import static ru.neoflex.neostudy.gatewaycloud.config.RoutingProperties.*;

@Configuration
public class ApplicationConfig {
	
	@Bean
	public Gateway gateway() {
		return new Gateway();
	}
	
	@Bean
	public Statement statement() {
		return new Statement();
	}
	
	@Bean
	public Deal deal() {
		return new Deal();
	}
	
	@Bean
	public RoutingProperties routingProperties() {
		return new RoutingProperties(gateway(), statement(), deal());
	}
	
	@Bean
	public RouteLocator routeLocator(RouteLocatorBuilder builder, Gateway gateway, Statement statement, Deal deal) {
		return builder
				.routes()
				.route("createLoanStatement", p -> p
						.path(gateway.getCreateLoanStatementPath())
						.and().method(HttpMethod.POST)
						.uri(statement.getHost()))
				.route("applyOffer", p -> p
						.path(gateway.getApplyOfferPath())
						.and().method(HttpMethod.POST)
						.filters(f -> f.rewritePath(gateway.getApplyOfferPath(), statement.getApplyOfferPath()))
						.uri(statement.getHost()))
				.route("finishRegistration", p -> p
						.path(gateway.getFinishRegistrationPath())
						.and().method(HttpMethod.POST)
						.filters(f -> f.rewritePath(getRegex(gateway.getFinishRegistrationPath()), getSubstitution(deal.getCalculateCreditPath())))
						.uri(deal.getHost()))
				.route("denyOffer", p -> p
						.path(gateway.getDenyOfferPath())
						.and().method(HttpMethod.GET)
						.filters(f -> f.rewritePath(getRegex(gateway.getDenyOfferPath()),  getSubstitution(deal.getDenyOfferPath())))
						.uri(deal.getHost()))
				.route("createDocuments", p -> p
						.path(gateway.getCreateDocumentsPath())
						.and().method(HttpMethod.POST)
						.filters(f -> f.rewritePath(getRegex(gateway.getCreateDocumentsPath()), getSubstitution(deal.getCreateDocumentsPath())))
						.uri(deal.getHost()))
				.route("signDocuments", p -> p
						.path(gateway.getSignDocumentsPath())
						.and().method(HttpMethod.POST)
						.filters(f -> f.rewritePath(getRegex(gateway.getSignDocumentsPath()), getSubstitution(deal.getSignDocumentPath())))
						.uri(deal.getHost()))
				.route("verifySesCode", p -> p
						.path(gateway.getVerifySesCodePath())
						.and().method(HttpMethod.POST)
						.filters(f -> f.rewritePath(getRegex(gateway.getVerifySesCodePath()), getSubstitution(deal.getVerifySignaturePath())))
						.uri(deal.getHost()))
				.route("updateStatementStatus", p -> p
						.path(gateway.getUpdateStatementStatusPath())
						.and().method(HttpMethod.PUT)
						.filters(f -> f.rewritePath(getRegex(gateway.getUpdateStatementStatusPath()), getSubstitution(deal.getUpdateStatementStatusPath())))
						.uri(deal.getHost()))
				.route("getStatement", p -> p
						.path(gateway.getGetStatementPath())
						.and().method(HttpMethod.GET)
						.filters(f -> f.rewritePath(getRegex(gateway.getGetStatementPath()), getSubstitution(deal.getGetStatementPath())))
						.uri(deal.getHost()))
				.route("getAllStatements", p -> p
						.path(gateway.getGetAllStatementsPath())
						.and().method(HttpMethod.GET)
						.filters(f -> f.rewritePath(getRegex(gateway.getGetAllStatementsPath()), getSubstitution(deal.getGetAllStatementsPath())))
						.uri(deal.getHost()))
				.route("deal-swagger", p -> p
						.path("/deal/v3/api-docs")
						.filters(f -> f.rewritePath("/deal/v3/api-docs", "/v3/api-docs"))
						.uri(deal.getHost()))
				.route("statement-swagger", p -> p
						.path("/statement/v3/api-docs")
						.filters(f -> f.rewritePath("/statement/v3/api-docs", "/v3/api-docs"))
						.uri(statement.getHost()))
				.build();
	}
	
	private String getRegex(String path) {
		return path.replace("{", "(?<").replace("}", ">.*)");
	}
	
	private String getSubstitution(String path) {
		return path.replace("{", "${");
	}
}
