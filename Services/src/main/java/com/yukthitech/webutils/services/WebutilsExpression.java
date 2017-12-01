package com.yukthitech.webutils.services;

import java.util.Set;

import com.yukthitech.utils.expr.Expression;
import com.yukthitech.utils.expr.ExpressionRegistry;
import com.yukthitech.utils.expr.IVariableTypeProvider;
import com.yukthitech.utils.expr.IVariableValueProvider;

/**
 * Encapsulation of parsed expression and webutils expression registry.
 * @author akiran
 */
public class WebutilsExpression
{
	/**
	 * Parsed expression.
	 */
	private Expression expression;
	
	/**
	 * Expression registry of operators and functions.
	 */
	private ExpressionRegistry registry;

	/**
	 * Instantiates a new webutils expression.
	 *
	 * @param expression the expression
	 * @param registry the registry
	 */
	public WebutilsExpression(Expression expression, ExpressionRegistry registry)
	{
		this.expression = expression;
		this.registry = registry;
	}

	/**
	 * Fetches variable names used in expression.
	 * @return Variable names used.
	 */
	public Set<String> getVariableNames()
	{
		return expression.getVariableNames();
	}

	/**
	 * Fetches the result type of expression.
	 * @param variableTypeProvider Provider to provider variable types.
	 * @return Result expression type.
	 */
	public Class<?> getType(IVariableTypeProvider variableTypeProvider)
	{
		return expression.getExpressionType(variableTypeProvider, registry);
	}
	
	/**
	 * Evaluates expression and returns result.
	 * @param variableValueProvider Variable value provider.
	 * @return Expression result
	 */
	public Object evaluate(IVariableValueProvider variableValueProvider)
	{
		return expression.evaluate(variableValueProvider, registry);
	}
	
	/**
	 * Gets the parsed expression.
	 *
	 * @return the parsed expression
	 */
	public Expression getExpression()
	{
		return expression;
	}
	
	/**
	 * Gets the expression registry of operators and functions.
	 *
	 * @return the expression registry of operators and functions
	 */
	public ExpressionRegistry getRegistry()
	{
		return registry;
	}
}
