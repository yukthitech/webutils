package com.webutils.cache;

import java.util.concurrent.TimeUnit;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CacheConfig<K,V>
{
    private String name;
    private long expiryTime;
    private TimeUnit expiryTimeUnit;
    private int maxSize;

    public CacheConfig<K,V> expireAfterAccess(long expiryTime, TimeUnit expiryTimeUnit)
    {
        this.expiryTime = expiryTime;
        this.expiryTimeUnit = expiryTimeUnit;
        return this;
    }

    public CacheConfig<K,V> maxSize(int maxSize)
    {
        this.maxSize = maxSize;
        return this;
    }
}
