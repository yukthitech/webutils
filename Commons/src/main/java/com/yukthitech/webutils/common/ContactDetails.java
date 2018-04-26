package com.yukthitech.webutils.common;

import com.yukthitech.webutils.common.annotations.Model;

/**
 * Represent generic contact details.
 * @author akiran
 */
@Model
public class ContactDetails
{
	/**
	 * Employee id.
	 */
	private long id;
	
	/**
	 * Name of the employee.
	 */
	private String name;
	
	/**
	 * Phone number of employee.
	 */
	private String phoneNo;
	
	/**
	 * Mail id of the employee.
	 */
	private String emailId;
	
	/**
	 * Gets the employee id.
	 *
	 * @return the employee id
	 */
	public long getId()
	{
		return id;
	}

	/**
	 * Sets the employee id.
	 *
	 * @param id the new employee id
	 */
	public void setId(long id)
	{
		this.id = id;
	}

	/**
	 * Gets the name of the employee.
	 *
	 * @return the name of the employee
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the employee.
	 *
	 * @param name the new name of the employee
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the phone number of employee.
	 *
	 * @return the phone number of employee
	 */
	public String getPhoneNo()
	{
		return phoneNo;
	}

	/**
	 * Sets the phone number of employee.
	 *
	 * @param phoneNo the new phone number of employee
	 */
	public void setPhoneNo(String phoneNo)
	{
		this.phoneNo = phoneNo;
	}

	/**
	 * Gets the mail id of the employee.
	 *
	 * @return the mail id of the employee
	 */
	public String getEmailId()
	{
		return emailId;
	}

	/**
	 * Sets the mail id of the employee.
	 *
	 * @param emailId the new mail id of the employee
	 */
	public void setEmailId(String emailId)
	{
		this.emailId = emailId;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}

		if(!(obj instanceof ContactDetails))
		{
			return false;
		}

		ContactDetails other = (ContactDetails) obj;
		return emailId.equals(other.emailId) && phoneNo.equals(other.phoneNo);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashcode()
	 */
	@Override
	public int hashCode()
	{
		return emailId.hashCode() + phoneNo.hashCode();
	}
}
