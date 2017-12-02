package com.test.yukthitech.webutils.mail;

import java.io.File;
import java.util.List;

import com.yukthitech.webutils.mail.template.MailAttachment;
import com.yukthitech.webutils.mail.template.MailConfigField;
import com.yukthitech.webutils.mail.template.MailTemplateConfig;

/**
 * Test mail configuration.
 * @author akiran
 */
@MailTemplateConfig(name = "TestMail1", description = "Test mail description")
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
	 * Attachment 1.
	 */
	@MailAttachment(name = "Attach1", description = "attachment1")
	private File attachment1;
	
	/**
	 * Attachment 2.
	 */
	@MailAttachment(name = "Attach2", description = "attachment2")
	private File attachment2;
	
	/** 
	 * The attachment3.
	 */
	@MailAttachment(name = "Attach3", description = "attachment3")
	private String attachment3;

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

	/**
	 * Gets the attachment 1.
	 *
	 * @return the attachment 1
	 */
	public File getAttachment1()
	{
		return attachment1;
	}

	/**
	 * Sets the attachment 1.
	 *
	 * @param attachment1 the new attachment 1
	 */
	public void setAttachment1(File attachment1)
	{
		this.attachment1 = attachment1;
	}

	/**
	 * Gets the attachment 2.
	 *
	 * @return the attachment 2
	 */
	public File getAttachment2()
	{
		return attachment2;
	}

	/**
	 * Sets the attachment 2.
	 *
	 * @param attachment2 the new attachment 2
	 */
	public void setAttachment2(File attachment2)
	{
		this.attachment2 = attachment2;
	}

	/**
	 * Gets the attachment3.
	 *
	 * @return the attachment3
	 */
	public String getAttachment3()
	{
		return attachment3;
	}

	/**
	 * Sets the attachment3.
	 *
	 * @param attachment3 the new attachment3
	 */
	public void setAttachment3(String attachment3)
	{
		this.attachment3 = attachment3;
	}
}
