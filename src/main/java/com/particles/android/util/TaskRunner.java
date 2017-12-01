package com.particles.android.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskRunner extends ThreadPoolExecutor {

	//private static final int DEF_CORE_POOL_SIZE = 0;
	private static final long DEF_KEEP_ALIVE_TIME = 5;
	private static final TimeUnit timeUnit = TimeUnit.SECONDS;
	
	public TaskRunner(int maximumPoolSize) {
		super(maximumPoolSize, maximumPoolSize, DEF_KEEP_ALIVE_TIME, timeUnit,
				new LinkedBlockingQueue<Runnable>());
	}

	public TaskRunner(int maximumPoolSize, ThreadFactory factory) {
		super(maximumPoolSize, maximumPoolSize, DEF_KEEP_ALIVE_TIME, timeUnit,
				new LinkedBlockingQueue<Runnable>(), factory);
	}
    
    public void stop() {
    	shutdownNow();
    }
}