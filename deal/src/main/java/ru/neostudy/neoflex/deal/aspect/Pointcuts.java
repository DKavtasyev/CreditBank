package ru.neostudy.neoflex.deal.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts
{
	@Pointcut("execution(* ru.neostudy.neoflex.deal.controller.DealController.*(..))")
	public void allControllerMethods(){}
	
	@Pointcut("execution(* ru.neostudy.neoflex.deal.requester.CalculatorRequester.request*(..))")
	public void allCalculatorRequesterMethods(){}
	
	@Pointcut("execution(* ru.neostudy.neoflex.deal.repository.*.save(..))")
	public void allSaveEntityMethods(){}
}
