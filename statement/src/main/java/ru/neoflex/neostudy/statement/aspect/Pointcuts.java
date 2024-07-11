package ru.neoflex.neostudy.statement.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts {
	@Pointcut("execution(* ru.neoflex.neostudy.statement.controller.StatementController.*(..))")
	public void allControllerMethods() {}
	
	@Pointcut("execution(* ru.neoflex.neostudy.statement.requester.DealRequester.requestLoanOffers(..))")
	public void requestLoanOffersMethod() {}
	
	@Pointcut("execution(* ru.neoflex.neostudy.statement.requester.DealRequester.sendChosenOffer(..))")
	public void sendChosenOfferMethod() {}
}
