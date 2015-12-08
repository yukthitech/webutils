/*
 * The MIT License (MIT)
 * Copyright (c) 2015 "Yukthi Techsoft Pvt. Ltd." (http://yukthi-tech.co.in)

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.yukthi.webutils.utils;


import java.util.Date;

import org.testng.Assert;
import org.testng.annotations.Test;


/**
 * @author akiran
 *
 */
public class TPropertyMapping
{
	/**
	 * Tested nested properties are identified properly based on camel case names
	 * @throws Exception
	 */
	@Test
	public void testNestedProp() throws Exception
	{
		BeanProperty property = new BeanProperty("simple", null, null, null);
		Assert.assertEquals("simple", property.getName());
		Assert.assertNull(property.getNestedName());

		property = new BeanProperty("simpleProp", null, null, null);
		Assert.assertEquals(property.getName(), "simpleProp");
		Assert.assertEquals(property.getNestedName(), "simple.prop");

		property = new BeanProperty("simplePropO", null, null, null);
		Assert.assertEquals(property.getName(), "simplePropO");
		Assert.assertEquals(property.getNestedName(), "simple.prop.o");
	}
	
	@Test
	public void testCopyToNested()
	{
		ModelBean model = new ModelBean("test", 100L, new Date(), new Date());
		EntityBean entityBean = new EntityBean();
		
		PropertyMapper.copyProperties(entityBean, model);
		
		Assert.assertEquals(entityBean.getName(), "test");
		Assert.assertEquals(entityBean.getSubprop().getId(), 100L);
		Assert.assertEquals(entityBean.getCreatedOn(), model.getCreatedOn());
	}

	@Test
	public void testCopyFromNested()
	{
		EntityBean entityBean = new EntityBean("test", new EntitySubbean(200L), new Date());
		ModelBean modelBean = new ModelBean();
		
		PropertyMapper.copyProperties(modelBean, entityBean);
		
		Assert.assertEquals(modelBean.getName(), "test");
		Assert.assertEquals(modelBean.getSubpropId(), 200L);
		Assert.assertEquals(modelBean.getCreatedOn(), entityBean.getCreatedOn());
		Assert.assertNull(modelBean.getUpdatedOn());
	}
}
