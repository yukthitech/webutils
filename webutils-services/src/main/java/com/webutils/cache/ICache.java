package com.webutils.cache;

import java.util.function.Supplier;

public interface ICache<K,V>
{
	public void set(K key, V value);
	
	public V computeIfAbsent(K key, Supplier<V> valueSupplier);
	
	public V get(K key);
	
	public void remove(K key);
}
