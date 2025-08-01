package com.yukthitech.webutils.services.prop;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.yukthitech.utils.PropertyAccessor;
import com.yukthitech.utils.PropertyAccessor.Property;
import com.yukthitech.utils.ReflectionUtils;
import com.yukthitech.webutils.lov.StoredLovEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TestPropertyCopyService
{
	private PropertyCopyService propertyCopyService = new PropertyCopyService();

	@BeforeClass
	private void init() throws Exception
	{
		ReflectionUtils.invokeMethod(propertyCopyService, PropertyCopyService.class.getDeclaredMethod("init"));
	}
	
	@Data
	@AllArgsConstructor
	public static class SourceBean
	{
		private String strProp;
		private int intProp;
	}
	
	@Data
	public static class TargetBean
	{
		private String strProp;
		private int intProp;
	}
	
	@Test
	public void testCopyProperties_simple()
	{
		Method methods[] = SourceBean.class.getMethods();
		
		for(Method method : methods)
		{
			System.out.println(method);
		}
		
		Map<String, Property> propMap = PropertyAccessor.getProperties(SourceBean.class);
		
		for(Property prop : propMap.values())
		{
			System.out.println(prop);
		}

		
		SourceBean source = new SourceBean("Test", 123);
		TargetBean target = new TargetBean();
		
		propertyCopyService.copyProperties(source, target);
		
		Assert.assertEquals(target.getStrProp(), source.getStrProp());
		Assert.assertEquals(target.getIntProp(), source.getIntProp());
	}
	
	@Data
	@AllArgsConstructor
	public static class NestedSource
	{
		private SourceBean bean;
	}
	
	@Data
	public static class NestedTarget
	{
		private TargetBean bean;
	}
	
	@Test
	public void testCopyProperties_nested()
	{
		NestedSource source = new NestedSource(new SourceBean("Test", 123));
		NestedTarget target = new NestedTarget();
		
		propertyCopyService.copyProperties(source, target);
		
		Assert.assertNotNull(target.getBean());
		Assert.assertEquals(target.getBean().getStrProp(), source.getBean().getStrProp());
		Assert.assertEquals(target.getBean().getIntProp(), source.getBean().getIntProp());
	}
	
	@Data
	@AllArgsConstructor
	public static class SourceWithList
	{
		private List<SourceBean> beans;
	}

	@Data
	public static class TargetWithList
	{
		private List<TargetBean> beans;
	}

	@Test
	public void testCopyProperties_collections()
	{
		SourceWithList source = new SourceWithList(Arrays.asList(new SourceBean("Test1", 1), new SourceBean("Test2", 2)));
		TargetWithList target = new TargetWithList();
		
		propertyCopyService.copyProperties(source, target);
		
		Assert.assertNotNull(target.getBeans());
		Assert.assertEquals(target.getBeans().size(), 2);
		
		Assert.assertEquals(target.getBeans().get(0).getStrProp(), "Test1");
		Assert.assertEquals(target.getBeans().get(0).getIntProp(), 1);

		Assert.assertEquals(target.getBeans().get(1).getStrProp(), "Test2");
		Assert.assertEquals(target.getBeans().get(1).getIntProp(), 2);
	}
	
	@Test
	public void testCloneBean()
	{
		SourceBean source = new SourceBean("Test", 123);
		TargetBean target = propertyCopyService.cloneBean(source, TargetBean.class);
		
		Assert.assertEquals(target.getStrProp(), source.getStrProp());
		Assert.assertEquals(target.getIntProp(), source.getIntProp());
	}
	
	@Test
	public void testCloneList()
	{
		List<SourceBean> source = Arrays.asList(new SourceBean("Test1", 1), new SourceBean("Test2", 2));
		List<TargetBean> target = propertyCopyService.cloneList(source, TargetBean.class);
		
		Assert.assertNotNull(target);
		Assert.assertEquals(target.size(), 2);
		
		Assert.assertEquals(target.get(0).getStrProp(), "Test1");
		Assert.assertEquals(target.get(0).getIntProp(), 1);

		Assert.assertEquals(target.get(1).getStrProp(), "Test2");
		Assert.assertEquals(target.get(1).getIntProp(), 2);
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class SourceWithId
	{
		private Long lov;
	}

	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class TargetWithEntity
	{
		private StoredLovEntity lov;
	}

	@Test
	public void testEntityToId()
	{
		SourceWithId source = new SourceWithId(10L);
		TargetWithEntity target = propertyCopyService.cloneBean(source, TargetWithEntity.class);
		
		Assert.assertEquals(target.getLov().getId(), (Long) 10L);
		
		source = propertyCopyService.cloneBean(target, SourceWithId.class);
		Assert.assertEquals(source.getLov(), (Long) 10L);
	}
}