package com.webutils.common;

public interface IDistributedLockManager
{
	public default void lockAndExecute(String lockName, int durationMillis, Executable executable)
	{
		lockAndExecuteWithReturn(lockName, durationMillis, () -> 
		{
			executable.execute();
			return null;
		});
	}
	
	public <T> T lockAndExecuteWithReturn(String lockName, int durationMillis, ExecutableWithReturn<T> executable);
}
