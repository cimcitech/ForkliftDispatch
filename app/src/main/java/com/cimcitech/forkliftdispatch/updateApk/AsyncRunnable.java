package com.cimcitech.forkliftdispatch.updateApk;


public interface AsyncRunnable<T> {
	/**
	 * 回到前台
	 * @param result
	 */
	void postForeground(T result);
	
	T run();
}
