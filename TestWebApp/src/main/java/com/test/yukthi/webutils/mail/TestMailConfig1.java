package com.test.yukthi.webutils.mail;

import java.util.List;

import com.yukthi.webutils.mail.template.AttachmentConfig;
import com.yukthi.webutils.mail.template.MailConfigField;
import com.yukthi.webutils.mail.template.MailTemplateConfig;

/**
 * Test mail configuration.
 * @author akiran
 */
@MailTemplateConfig(name = "TestMail1", description = "Test mail description", 
	attachments = { 
		@AttachmentConfig(name = "image1", description = "Image config", image = true)
	})
public class TestMailConfig1
{
	/**
	 * The Class Address.
	 */
	public static class Address
	{
		
		/** 
		 * The city. 
		 */
		@MailConfigField(description = "City of address")
		private String city;
		
		/** 
		 * The state. 
		 */
		@MailConfigField(description = "State of address")
		private String state;

		/**
		 * Gets the city.
		 *
		 * @return the city
		 */
		public String getCity()
		{
			return city;
		}

		/**
		 * Sets the city.
		 *
		 * @param city the new city
		 */
		public void setCity(String city)
		{
			this.city = city;
		}

		/**
		 * Gets the state.
		 *
		 * @return the state
		 */
		public String getState()
		{
			return state;
		}

		/**
		 * Sets the state.
		 *
		 * @param state the new state
		 */
		public void setState(String state)
		{
			this.state = state;
		}
	}
	
	/**
	 * The Class Department.
	 */
	public static class Department
	{
		
		/** 
		 * The name. 
		 */
		@MailConfigField(description = "Department name")
		private String name;

		/**
		 * Gets the name.
		 *
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name.
		 *
		 * @param name the new name
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}
	
	/**
	 * Name.
	 */
	@MailConfigField(description = "Name")
	private String name;
	
	/**
	 * Age.
	 */
	@MailConfigField(description = "Age")
	private int age;
	
	/**
	 * Address.
	 */
	@MailConfigField(description = "Address")
	private Address address;
	
	/**
	 * Departments.
	 */
	@MailConfigField(description = "Departments")
	private List<Department> departments;

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name.
	 *
	 * @param name the new name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the age.
	 *
	 * @return the age
	 */
	public int getAge()
	{
		return age;
	}

	/**
	 * Sets the age.
	 *
	 * @param age the new age
	 */
	public void setAge(int age)
	{
		this.age = age;
	}

	/**
	 * Gets the address.
	 *
	 * @return the address
	 */
	public Address getAddress()
	{
		return address;
	}

	/**
	 * Sets the address.
	 *
	 * @param address the new address
	 */
	public void setAddress(Address address)
	{
		this.address = address;
	}

	/**
	 * Gets the departments.
	 *
	 * @return the departments
	 */
	public List<Department> getDepartments()
	{
		return departments;
	}

	/**
	 * Sets the departments.
	 *
	 * @param departments the new departments
	 */
	public void setDepartments(List<Department> departments)
	{
		this.departments = departments;
	}
}
