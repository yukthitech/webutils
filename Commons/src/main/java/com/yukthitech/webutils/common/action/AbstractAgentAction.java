package com.yukthitech.webutils.common.action;

/**
 * Abstract parent class for agent actions.
 * @author akiran
 */
public class AbstractAgentAction implements IAgentAction
{
	/**
	 * Name of the action.
	 */
	private String name;
	
	/**
	 * Label of the action.
	 */
	private String label;
	
	public AbstractAgentAction()
	{}
	
	public AbstractAgentAction(String name, String label)
	{
		this.name = name;
		this.label = label;
	}

	/**
	 * Sets the name of the action.
	 *
	 * @param name the new name of the action
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Sets the label of the action.
	 *
	 * @param label the new label of the action
	 */
	public void setLabel(String label)
	{
		this.label = label;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getName()
	 */
	@Override
	public String getName()
	{
		return name;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.action.IAgentAction#getLabel()
	 */
	@Override
	public String getLabel()
	{
		return label;
	}
	
	@Override
	public String getType()
	{
		return this.getClass().getSimpleName();
	}
	
	/**
	 * A dummy setter to ensure during deserialization "type" property will not 
	 * create problem.
	 * @param type type of this action.
	 */
	public void setType(String type)
	{}
}
