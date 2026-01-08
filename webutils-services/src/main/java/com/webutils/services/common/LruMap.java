package com.webutils.services.common;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruMap<K, V> extends LinkedHashMap<K, V> 
{
	private static final long serialVersionUID = 1L;
	private final int maxSize;

    public LruMap(int maxSize)
    {
        super(maxSize, 0.75f, true);
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest)
    {
        return size() > maxSize;
    }
}
