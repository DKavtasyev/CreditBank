package ru.neostudy.neoflex.calculator.aspect;

import org.aspectj.lang.annotation.Pointcut;

public class Pointcuts
{
	@Pointcut("execution(* ru.neostudy.neoflex.calculator.controller.CalculatorController.*alculation*(..))")
	public void allControllerMethods(){}
	
	@Pointcut("execution(* ru.neostudy.neoflex.calculator.service.CalculatorService.preScore(..))")
	public void calculatorServicePreScoreMethod(){}
	
	@Pointcut("execution(* ru.neostudy.neoflex.calculator.service.CalculatorService.score(..))")
	public void calculatorServiceScoreMethod(){}
}
