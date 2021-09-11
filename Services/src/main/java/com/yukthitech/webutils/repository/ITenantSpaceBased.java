package com.yukthitech.webutils.repository;

/**
 * Used to mark entity as tenant space based.
 * @author akiran
 */
public interface ITenantSpaceBased
{
	/**
	 * Gets the space identity.
	 *
	 * @return the space identity
	 */
	public String getSpaceIdentity();
	
	/**
	 * Sets the space identity.
	 *
	 * @param spaceIdentity the new space identity
	 */
	public void setSpaceIdentity(String spaceIdentity);
}
