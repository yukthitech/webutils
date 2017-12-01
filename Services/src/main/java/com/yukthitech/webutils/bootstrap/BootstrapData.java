package com.yukthitech.webutils.bootstrap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Bootstrap data that needs to be added to db at time of bootstrap.
 */
public class BootstrapData
{
	/**
	 * Entity group with entity data and other meta info.
	 */
	public static class EntityGroup
	{
		/**
		 * Name of the entity group.
		 */
		private String name;
		
		/**
		 * Model type to be used. Json gets mapped to this model type.
		 */
		private String modelType;
		
		/**
		 * Type of the entity to be created.
		 */
		private String entityType;
		
		/**
		 * Field by which entity of this group can be identified uniquely.
		 */
		private String identityField;
		
		/**
		 * Entities to be created.
		 */
		private List<Map<String, Object>> entities;
		
		/**
		 * Mappings from model field to entity field.
		 */
		private Map<String, String> fieldMappings = new HashMap<>();
		
		/**
		 * Space under which this entity should be stored.
		 */
		private String spaceIdentity;
		
		/**
		 * Service method to use for saving model.
		 */
		private String serviceMethod;

		/**
		 * Gets the name of the entity group.
		 *
		 * @return the name of the entity group
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * Sets the name of the entity group.
		 *
		 * @param name the new name of the entity group
		 */
		public void setName(String name)
		{
			this.name = name;
		}

		/**
		 * Gets the type of the entity to be created.
		 *
		 * @return the type of the entity to be created
		 */
		public String getEntityType()
		{
			return entityType;
		}

		/**
		 * Sets the type of the entity to be created.
		 *
		 * @param entityType the new type of the entity to be created
		 */
		public void setEntityType(String entityType)
		{
			this.entityType = entityType;
		}

		/**
		 * Gets the entities to be created.
		 *
		 * @return the entities to be created
		 */
		public List<Map<String, Object>> getEntities()
		{
			return entities;
		}

		/**
		 * Sets the entities to be created.
		 *
		 * @param entities the new entities to be created
		 */
		public void setEntities(List<Map<String, Object>> entities)
		{
			this.entities = entities;
		}

		/**
		 * Gets the field by which entity of this group can be identified uniquely.
		 *
		 * @return the field by which entity of this group can be identified uniquely
		 */
		public String getIdentityField()
		{
			return identityField;
		}

		/**
		 * Sets the field by which entity of this group can be identified uniquely.
		 *
		 * @param identityField the new field by which entity of this group can be identified uniquely
		 */
		public void setIdentityField(String identityField)
		{
			this.identityField = identityField;
		}

		/**
		 * Gets the model type to be used. Json gets mapped to this model type.
		 *
		 * @return the model type to be used
		 */
		public String getModelType()
		{
			return modelType;
		}

		/**
		 * Sets the model type to be used. Json gets mapped to this model type.
		 *
		 * @param modelType the new model type to be used
		 */
		public void setModelType(String modelType)
		{
			this.modelType = modelType;
		}

		/**
		 * Sets the mappings from model field to entity field.
		 *
		 * @param fieldMappings the new mappings from model field to entity field
		 */
		public void setFieldMappings(Map<String, String> fieldMappings)
		{
			this.fieldMappings = fieldMappings;
		}
		
		/**
		 * Gets the mappings from model field to entity field.
		 *
		 * @return the mappings from model field to entity field
		 */
		public Map<String, String> getFieldMappings()
		{
			return fieldMappings;
		}
		
		/**
		 * Fetches the entity field name (if specified) for specified field. If no mapping is provided
		 * the same field is returned.
		 * @param field Field for which mapping to be obtained
		 * @return Mapped entity field.
		 */
		public String getFieldMapping(String field)
		{
			String entityField = fieldMappings.get(field);
			
			return entityField != null ? entityField : field;
		}

		/**
		 * Gets the space under which this entity should be stored.
		 *
		 * @return the space under which this entity should be stored
		 */
		public String getSpaceIdentity()
		{
			return spaceIdentity;
		}

		/**
		 * Sets the space under which this entity should be stored.
		 *
		 * @param spaceIdentity the new space under which this entity should be stored
		 */
		public void setSpaceIdentity(String spaceIdentity)
		{
			this.spaceIdentity = spaceIdentity;
		}

		/**
		 * Gets the service method to use for saving model.
		 *
		 * @return the service method to use for saving model
		 */
		public String getServiceMethod()
		{
			return serviceMethod;
		}

		/**
		 * Sets the service method to use for saving model.
		 *
		 * @param serviceMethod the new service method to use for saving model
		 */
		public void setServiceMethod(String serviceMethod)
		{
			this.serviceMethod = serviceMethod;
		}
	}
	
	/**
	 * List of entity groups data to be added to db.
	 */
	private List<EntityGroup> entityGroups;
	
	/**
	 * Default user name to use created by and updated by fields.
	 */
	private String defaultUserName;

	/**
	 * Gets the list of entity groups data to be added to db.
	 *
	 * @return the list of entity groups data to be added to db
	 */
	public List<EntityGroup> getEntityGroups()
	{
		return entityGroups;
	}

	/**
	 * Sets the list of entity groups data to be added to db.
	 *
	 * @param entityGroups the new list of entity groups data to be added to db
	 */
	public void setEntityGroups(List<EntityGroup> entityGroups)
	{
		this.entityGroups = entityGroups;
	}

	/**
	 * Gets the default user name to use created by and updated by fields.
	 *
	 * @return the default user name to use created by and updated by fields
	 */
	public String getDefaultUserName()
	{
		return defaultUserName;
	}

	/**
	 * Sets the default user name to use created by and updated by fields.
	 *
	 * @param defaultUserName the new default user name to use created by and updated by fields
	 */
	public void setDefaultUserName(String defaultUserName)
	{
		this.defaultUserName = defaultUserName;
	}
}
