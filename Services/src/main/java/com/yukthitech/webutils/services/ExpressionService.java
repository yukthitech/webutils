package com.yukthitech.webutils.services;

import java.util.Collection;

import org.springframework.stereotype.Service;

import com.yukthitech.utils.expr.Expression;
import com.yukthitech.utils.expr.ExpressionEvaluator;
import com.yukthitech.utils.expr.ExpressionRegistry;
import com.yukthitech.utils.expr.IFunction;
import com.yukthitech.utils.expr.IOperator;
import com.yukthitech.utils.expr.RegistryFactory;

import jakarta.annotation.PostConstruct;

/**
 * Service to parse and evaluate expressions.
 * @author akiran
 */
@Service
public class ExpressionService
{
	/**
	 * Expression evaluator.
	 */
	private ExpressionEvaluator expressionEvaluator = new ExpressionEvaluator();
	
	/**
	 * Expression registry.
	 */
	private ExpressionRegistry expressionRegistry = new ExpressionRegistry();
	
	/**
	 * Gets default operators and functions into expression registry.
	 */
	@PostConstruct
	private void init()
	{
		RegistryFactory.registerDefaults(expressionRegistry);
	}
	
	/**
	 * Gets all registered operators.
	 * @return all registered operators.
	 */
	public Collection<IOperator> getAllOperators()
	{
		return expressionRegistry.getAllOperators();
	}
	
	/**
	 * Gets all registered functions.
	 * @return registered functions.
	 */
	public Collection<IFunction> getAllFunctions()
	{
		return expressionRegistry.getAllFunctions();
	}
	
	/**
	 * Parses provided expression string into expression.
	 * @param expressionStr Expression string to be parsed.
	 * @return Parsed expression.
	 */
	public WebutilsExpression parse(String expressionStr)
	{
		Expression expression = expressionEvaluator.parse(expressionStr);
		return new WebutilsExpression(expression, expressionRegistry);
	}
}
