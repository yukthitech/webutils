package com.webutils.cache;

import java.util.function.Supplier;

public interface ICache
{
	public void set(String key, Object value);
	
	public <T> T computeIfAbsent(String key, Supplier<T> valueSupplier);
	
	public Object get(String key);
	
	public void remove(String key);
}
