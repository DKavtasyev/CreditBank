package ru.neoflex.neostudy.calculator.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts
{
	@Pointcut("execution(* ru.neoflex.neostudy.calculator.controller.CalculatorController.*alculation*(..))")
	public void allControllerMethods(){}
	
	@Pointcut("execution(* ru.neoflex.neostudy.calculator.service.CalculatorService.preScore(..))")
	public void calculatorServicePreScoreMethod(){}
	
	@Pointcut("execution(* ru.neoflex.neostudy.calculator.service.CalculatorService.score(..))")
	public void calculatorServiceScoreMethod(){}
}
