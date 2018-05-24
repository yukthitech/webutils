package com.yukthitech.webutils.common.actionplan;

/**
 * Condition that can be specified on action step.
 * @author akiran
 */
public class StepCondition
{
	/**
	 * Expression to be evaluated.
	 */
	private String expression;
	
	/**
	 * Expected value of the expression.
	 */
	private String value;
	
	/**
	 * Value which should not match with expression.
	 */
	private String notValue;

	/**
	 * Used to 'and' other condition.
	 */
	private StepCondition and;
	
	/**
	 * Used to 'or' other condition.
	 */
	private StepCondition or;

	/**
	 * Gets the expression to be evaluated.
	 *
	 * @return the expression to be evaluated
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * Sets the expression to be evaluated.
	 *
	 * @param expression the new expression to be evaluated
	 */
	public void setExpression(String expression)
	{
		this.expression = expression;
	}

	/**
	 * Gets the expected value of the expression.
	 *
	 * @return the expected value of the expression
	 */
	public String getValue()
	{
		return value;
	}

	/**
	 * Sets the expected value of the expression.
	 *
	 * @param value the new expected value of the expression
	 */
	public void setValue(String value)
	{
		this.value = value;
	}

	/**
	 * Gets the used to 'and' other condition.
	 *
	 * @return the used to 'and' other condition
	 */
	public StepCondition getAnd()
	{
		return and;
	}

	/**
	 * Sets the used to 'and' other condition.
	 *
	 * @param and the new used to 'and' other condition
	 */
	public void setAnd(StepCondition and)
	{
		this.and = and;
	}

	/**
	 * Gets the used to 'or' other condition.
	 *
	 * @return the used to 'or' other condition
	 */
	public StepCondition getOr()
	{
		return or;
	}

	/**
	 * Sets the used to 'or' other condition.
	 *
	 * @param or the new used to 'or' other condition
	 */
	public void setOr(StepCondition or)
	{
		this.or = or;
	}

	/**
	 * Gets the value which should not match with expression.
	 *
	 * @return the value which should not match with expression
	 */
	public String getNotValue()
	{
		return notValue;
	}

	/**
	 * Sets the value which should not match with expression.
	 *
	 * @param notValue the new value which should not match with expression
	 */
	public void setNotValue(String notValue)
	{
		this.notValue = notValue;
	}
}
