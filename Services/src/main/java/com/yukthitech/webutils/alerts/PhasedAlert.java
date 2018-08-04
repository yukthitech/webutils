package com.yukthitech.webutils.alerts;

/**
 * Used to cache phased alerts.
 * @author akiran
 */
public class PhasedAlert
{
	/**
	 * Event object to be processed.
	 */
	private Object eventObject;
	
	/**
	 * Event type to be used.
	 */
	private String eventType;

	/**
	 * Instantiates a new phased alert.
	 *
	 * @param eventObject the event object
	 * @param eventType the event type
	 */
	public PhasedAlert(Object eventObject, String eventType)
	{
		this.eventObject = eventObject;
		this.eventType = eventType;
	}

	/**
	 * Gets the event object to be processed.
	 *
	 * @return the event object to be processed
	 */
	public Object getEventObject()
	{
		return eventObject;
	}

	/**
	 * Gets the event type to be used.
	 *
	 * @return the event type to be used
	 */
	public String getEventType()
	{
		return eventType;
	}
}
