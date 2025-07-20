package com.yukthitech.webutils.controllers;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Map;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.yukthitech.webutils.InvalidRequestException;
import com.yukthitech.webutils.annotations.ActionName;
import com.yukthitech.webutils.common.BaseModel;
import com.yukthitech.webutils.common.IWebUtilsActionConstants;
import com.yukthitech.webutils.common.client.IRequestCustomizer;
import com.yukthitech.webutils.common.controllers.ICrudController;
import com.yukthitech.webutils.common.models.BaseResponse;
import com.yukthitech.webutils.common.models.BasicReadResponse;
import com.yukthitech.webutils.common.models.BasicSaveResponse;
import com.yukthitech.webutils.repository.WebutilsBaseEntity;
import com.yukthitech.webutils.services.BaseCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

/**
 * Base class for crud controllers with common functionalities.
 * @author akiran
 * @param <M> Model type for which controller is being defined.
 * @param <S> service type to be used.
 * @param <C> Current controller type.
 */
public class BaseCrudController<M extends BaseModel, S extends BaseCrudService<?, ?>, C extends ICrudController<M, C>> 
	extends BaseController 
	implements ICrudController<M, C>
{
	/**
	 * Spring application context to fetch service instance.
	 */
	@Autowired
	private ApplicationContext applicationContext;
	
	/**
	 * Service to be used for current controller apis.
	 */
	private S service;

	/**
	 * Model type of this controller.
	 */
	private Class<M> modelType;
	
	/**
	 * Service type of this controller.
	 */
	private Class<S> serviceType;
	
	/**
	 * Initialize method which would fetch model and service types. And also finds 
	 * required service in sprint context.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@PostConstruct
	private void init()
	{
		Map<TypeVariable<?>, Type> genericMap = TypeUtils.getTypeArguments(getClass(), BaseCrudController.class);
		TypeVariable<?> typeVars[] = BaseCrudController.class.getTypeParameters();
		
		modelType = (Class) genericMap.get(typeVars[0]);
		serviceType = (Class) genericMap.get(typeVars[1]);
	}
	
	/**
	 * Gets the service to be used by this controller. If not set already, will get 
	 * from spring context.
	 * @return service to be used
	 */
	protected S getService()
	{
		if(service != null)
		{
			return service;
		}
		
		service = applicationContext.getBean(serviceType);
		return service;
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.ICrudController#save(java.lang.Object)
	 */
	@ResponseBody
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_SAVE)
	@Override
	public BasicSaveResponse save(@RequestBody @Valid M model)
	{
		WebutilsBaseEntity entity = getService().save(model);
		return new BasicSaveResponse(entity.getId());
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.ICrudController#fetchById(long)
	 */
	@ResponseBody
	@RequestMapping("/fetch/{id}")
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_READ)
	@Override
	public BasicReadResponse<M> fetchById(@PathVariable("id") long id)
	{
		M model = getService().fetchFullModel(id, modelType);
		return new BasicReadResponse<>(model);
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.ICrudController#update(java.lang.Object)
	 */
	@ResponseBody
	@RequestMapping(value = "/update", method = RequestMethod.POST)
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_UPDATE)
	@Override
	public BaseResponse update(@RequestBody @Valid M model)
	{
		if(model.getId() == null || model.getId() <= 0)
		{
			throw new InvalidRequestException("Invalid id specified for update: " + model.getId());
		}
		
		getService().update(model);
		return new BaseResponse();
	}

	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.ICrudController#delete(long)
	 */
	@ResponseBody
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
	@ActionName(IWebUtilsActionConstants.ACTION_TYPE_DELETE)
	@Override
	public BaseResponse delete(@PathVariable("id") long id)
	{
		getService().deleteById(id);
		return new BaseResponse();
	}
	
	/* (non-Javadoc)
	 * @see com.yukthitech.webutils.common.controllers.IClientController#setRequestCustomizer(com.yukthitech.webutils.common.client.IRequestCustomizer)
	 */
	@Override
	public C setRequestCustomizer(IRequestCustomizer customizer)
	{
		return null;
	}
}
