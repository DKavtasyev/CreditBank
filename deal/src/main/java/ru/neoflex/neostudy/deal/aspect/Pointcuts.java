package ru.neoflex.neostudy.deal.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts
{
	@Pointcut("execution(* ru.neoflex.neostudy.deal.controller.DealController.*(..))")
	public void allControllerMethods(){}
	
	@Pointcut("execution(* ru.neoflex.neostudy.deal.requester.CalculatorRequester.request*(..))")
	public void allCalculatorRequesterMethods(){}
	
	@Pointcut("execution(* ru.neostudy.neoflex.deal.repository.*.save(..))")
	public void allSaveEntityMethods(){}
}
