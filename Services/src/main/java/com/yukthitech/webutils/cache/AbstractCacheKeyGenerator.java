package com.yukthitech.webutils.cache;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.yukthitech.utils.CommonUtils;
import com.yukthitech.webutils.IWebUtilsInternalConstants;

/**
 * Used to generate cache key in webutils standard.
 * @author akiran
 */
public abstract class AbstractCacheKeyGenerator implements KeyGenerator
{
	/**
	 * Construct key with specified params.
	 *
	 * @param keyExpr the key expr
	 * @param groupExprs the group expr
	 * @param excludeMethod the exclude method
	 * @param excludeParams the exclude params
	 * @param target the target
	 * @param method the method
	 * @param params the params
	 * @return the cache key
	 */
	protected CacheKey constructKey(String keyExpr, String[] groupExprs, boolean excludeMethod, boolean excludeParams, Object target, Method method, Object... params)
	{
		String methodName = method.getName();
		Object key = null;
		Set<String> groups = null;
		
		if(excludeMethod)
		{
			methodName = null;
		}
		
		EvaluationContext context = null;
		
		if(StringUtils.isNotBlank(keyExpr))
		{
			context = buildContext(target, method, params);
			key = IWebUtilsInternalConstants.SPRING_EXPRESSION_PARSER.parseExpression(keyExpr).getValue(context);
		}
		
		if(groupExprs != null && groupExprs.length > 0)
		{
			context = context != null ? context : buildContext(target, method, params);
			groups = new HashSet<>();
			
			for(String groupExpr : groupExprs)
			{
				String group = IWebUtilsInternalConstants.SPRING_EXPRESSION_PARSER.parseExpression(groupExpr).getValue(context).toString();
				groups.add(group);
			}
		}
		
		if(excludeParams)
		{
			params = null;
		}
		
		return new CacheKey(methodName, key, params, groups);
	}
	
	/**
	 * Builds the context.
	 *
	 * @param target the target
	 * @param method the method
	 * @param params the params
	 * @return the evaluation context
	 */
	protected EvaluationContext buildContext(Object target, Method method, Object params[])
	{
		Map<String, Object> root = CommonUtils.toMap(
				"methodName", method.getName(),
				"method", method,
				"target", target,
				"args", params
			);
			
		EvaluationContext evaluationContext = new StandardEvaluationContext(CommonUtils.toMap("root", root));

		if(params != null)
		{
			int idx = 0;
			
			for(Object param : params)
			{
				evaluationContext.setVariable("a" + idx, param);
				evaluationContext.setVariable("p" + idx, param);
				idx++;
			}
		}

		return evaluationContext;
	}
}
