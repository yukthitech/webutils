package com.webutils.services.common;

import java.util.Map;

import javax.persistence.Table;

import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.PropertyAccessor.Property;
import com.yukthitech.utils.exceptions.InvalidStateException;

public class CommonUtils
{
    public static <E, M> M entityToModel(E entity, Class<M> modelType)
    {
    	M model = PropertyAccessor.cloneObject(entity, modelType);
    	
    	Map<String, Property> modelPropMap = PropertyAccessor.getProperties(modelType);
    	Map<String, Property> entPropMap = PropertyAccessor.getProperties(entity.getClass());
    	
    	for(String propName : modelPropMap.keySet())
    	{
    		Property modelProp = modelPropMap.get(propName);
    		
    		if(!propName.toLowerCase().endsWith("id") && propName.length() > 2 &&
    				(Long.class.equals(modelProp.getType()) || long.class.equals(modelProp.getType())))
    		{
    			continue;
    		}
    		
    		String entPropName = propName.substring(0, propName.length() - 2);
    		Property entProp = entPropMap.get(entPropName);
    		
    		if(entProp == null)
    		{
    			continue;
    		}
    		
    		if(entProp.getType().getAnnotation(Table.class) == null)
    		{
    			continue;
    		}
    		
    		Object propEnt = entProp.getValue(entity);
    		
    		if(propEnt == null)
    		{
    			continue;
    		}
    		
    		Long id = (Long) PropertyAccessor.getProperty(propEnt, "id");
    		
    		if(id == null)
    		{
    			continue;
    		}
    		
    		modelProp.setValue(model, id);
    	}
    	
    	return model;
    }

    public static <E, M> E modelToEntity(M model, Class<E> entityType)
    {
    	E entity = PropertyAccessor.cloneObject(model, entityType);
    	
    	Map<String, Property> modelPropMap = PropertyAccessor.getProperties(model.getClass());
    	Map<String, Property> entPropMap = PropertyAccessor.getProperties(entityType);
    	
    	for(String propName : modelPropMap.keySet())
    	{
    		Property modelProp = modelPropMap.get(propName);
    		
    		if(!propName.toLowerCase().endsWith("id") && propName.length() > 2 &&
    				(Long.class.equals(modelProp.getType()) || long.class.equals(modelProp.getType())))
    		{
    			continue;
    		}
    		
    		String entPropName = propName.substring(0, propName.length() - 2);
    		Property entProp = entPropMap.get(entPropName);
    		
    		if(entProp == null)
    		{
    			continue;
    		}
    		
    		if(entProp.getType().getAnnotation(Table.class) == null)
    		{
    			continue;
    		}
    		
    		Object idFromModel = modelProp.getValue(model);
    		
    		if(idFromModel == null && !(idFromModel instanceof Long))
    		{
    			continue;
    		}
    		
    		Object subentity = null;
    		
    		try
    		{
    			subentity = entProp.getType().getConstructor().newInstance();
    		}catch(Exception ex)
    		{
    			throw new InvalidStateException("An error occurred while creating instance of type: {}", entProp.getType().getName(), ex);
    		}
    		
    		PropertyAccessor.setProperty(subentity, "id", idFromModel);
    		
    		entProp.setValue(entity, subentity);
    	}
    	
    	return entity;
    }
}
