package com.yukthi.webutils.common.models.mails;

import java.util.TreeSet;

/**
 * Represents mail template configuration with all field details and attachment details.
 */
public class MailTemplateConfiguration
{
	/**
	 * A field with in this mail template configuration that can be consumed in
	 * mail templates.
	 * 
	 * @author akiran
	 */
	public static class Field implements Comparable<Field>
	{
		/**
		 * Name of the field.
		 */
		private String name;

		/**
		 * Description about the field.
		 */
		private String description;

		/**
		 * Field type.
		 */
		private String fieldType;

		/**
		 * Instantiates a new field.
		 */
		public Field()
		{}

		/**
		 * Instantiates a new field.
		 *
		 * @param name
		 *            the name
		 * @param description
		 *            the description
		 * @param fieldType
		 *            the field type
		 */
		public Field(String name, String description, String fieldType)
		{
			this.name = name;
			this.description = description;
			this.fieldType = fieldType;
		}

		/**
		 * Gets the name of the field.
		 *
		 * @return the name of the field
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the field.
		 *
		 * @param name
		 *            the new name of the field
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the description about the field.
		 *
		 * @return the description about the field
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Sets the description about the field.
		 *
		 * @param description
		 *            the new description about the field
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}

		/**
		 * Gets the field type.
		 *
		 * @return the field type
		 */
		public String getFieldType()
		{
			return fieldType;
		}

		/**
		 * Sets the field type.
		 *
		 * @param fieldType
		 *            the new field type
		 */
		public void setFieldType(String fieldType)
		{
			this.fieldType = fieldType;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Field other)
		{
			return name.compareTo(other.name);
		}
	}

	/**
	 * Represents attachment details of mail templates.
	 * 
	 * @author akiran
	 */
	public static class Attachment implements Comparable<Attachment>
	{
		/**
		 * Name of the attachment.
		 */
		private String name;
		
		/**
		 * Content id for the attachment. Will be useful to add inline images.
		 */
		private String contentId;

		/**
		 * Description of the attachment.
		 */
		private String description;

		/**
		 * Flag indication if attachment is image.
		 */
		private boolean image;
		
		/**
		 * Field (or nested field) name defined as this attachment.
		 */
		private String field;
		
		/**
		 * Instantiates a new attachment.
		 */
		public Attachment()
		{}
		
		/**
		 * Instantiates a new attachment.
		 *
		 * @param name the name
		 * @param contentId Content id to be used for attachment.
		 * @param description the description
		 * @param image the image
		 * @param field Field name
		 */
		public Attachment(String name, String contentId, String description, boolean image, String field)
		{
			this.name = name;
			this.contentId = contentId;
			this.description = description;
			this.image = image;
			this.field = field;
		}

		/**
		 * Gets the name of the attachment.
		 *
		 * @return the name of the attachment
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the attachment.
		 *
		 * @param name
		 *            the new name of the attachment
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the content id for the attachment. Will be useful to add inline images.
		 *
		 * @return the content id for the attachment
		 */
		public String getContentId()
		{
			return contentId;
		}

		/**
		 * Sets the content id for the attachment. Will be useful to add inline images.
		 *
		 * @param contentId the new content id for the attachment
		 */
		public void setContentId(String contentId)
		{
			this.contentId = contentId;
		}

		/**
		 * Gets the description of the attachment.
		 *
		 * @return the description of the attachment
		 */
		public String getDescription()
		{
			return description;
		}

		/**
		 * Sets the description of the attachment.
		 *
		 * @param description
		 *            the new description of the attachment
		 */
		public void setDescription(String description)
		{
			this.description = description;
		}

		/**
		 * Checks if is flag indication if attachment is image.
		 *
		 * @return the flag indication if attachment is image
		 */
		public boolean isImage()
		{
			return image;
		}

		/**
		 * Sets the flag indication if attachment is image.
		 *
		 * @param image
		 *            the new flag indication if attachment is image
		 */
		public void setImage(boolean image)
		{
			this.image = image;
		}
		
		/**
		 * Gets the field (or nested field) name defined as this attachment.
		 *
		 * @return the field (or nested field) name defined as this attachment
		 */
		public String getField()
		{
			return field;
		}

		/**
		 * Sets the field (or nested field) name defined as this attachment.
		 *
		 * @param field the new field (or nested field) name defined as this attachment
		 */
		public void setField(String field)
		{
			this.field = field;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(Attachment other)
		{
			return name.compareTo(other.name);
		}
	}

	/**
	 * Name of the mail template configuration.
	 */
	private String name;

	/**
	 * Description of the mail template configuration.
	 */
	private String description;

	/**
	 * Class which is defining this configuration.
	 */
	private String type;

	/**
	 * Fields defined as part of configuration.
	 */
	private TreeSet<Field> fields = new TreeSet<>();

	/**
	 * List of attachment details.
	 */
	private TreeSet<Attachment> attachments = new TreeSet<>();

	/**
	 * Instantiates a new mail template configuration.
	 */
	public MailTemplateConfiguration()
	{}

	/**
	 * Instantiates a new mail template configuration.
	 *
	 * @param name
	 *            the name
	 * @param description
	 *            the description
	 * @param type
	 *            the type
	 */
	public MailTemplateConfiguration(String name, String description, String type)
	{
		this.name = name;
		this.description = description;
		this.type = type;
	}

	/**
	 * Gets the name of the mail template configuration.
	 *
	 * @return the name of the mail template configuration
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Sets the name of the mail template configuration.
	 *
	 * @param name
	 *            the new name of the mail template configuration
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the description of the mail template configuration.
	 *
	 * @return the description of the mail template configuration
	 */
	public String getDescription()
	{
		return description;
	}

	/**
	 * Sets the description of the mail template configuration.
	 *
	 * @param description
	 *            the new description of the mail template configuration
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 * Gets the class which is defining this configuration.
	 *
	 * @return the class which is defining this configuration
	 */
	public String getType()
	{
		return type;
	}

	/**
	 * Sets the class which is defining this configuration.
	 *
	 * @param type
	 *            the new class which is defining this configuration
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 * Gets the fields defined as part of configuration.
	 *
	 * @return the fields defined as part of configuration
	 */
	public TreeSet<Field> getFields()
	{
		return fields;
	}

	/**
	 * Sets the fields defined as part of configuration.
	 *
	 * @param fields
	 *            the new fields defined as part of configuration
	 */
	public void setFields(TreeSet<Field> fields)
	{
		this.fields = fields;
	}

	/**
	 * Adds field details.
	 *
	 * @param field
	 *            field to be added
	 */
	public void addField(Field field)
	{
		fields.add(field);
	}

	/**
	 * Gets the list of attachment details.
	 *
	 * @return the list of attachment details
	 */
	public TreeSet<Attachment> getAttachments()
	{
		return attachments;
	}

	/**
	 * Sets the list of attachment details.
	 *
	 * @param attachments the new list of attachment details
	 */
	public void setAttachments(TreeSet<Attachment> attachments)
	{
		this.attachments = attachments;
	}

	/**
	 * Adds attachment.
	 *
	 * @param attachment
	 *            attachment to be added
	 */
	public void addAttachment(Attachment attachment)
	{
		attachments.add(attachment);
	}
}
