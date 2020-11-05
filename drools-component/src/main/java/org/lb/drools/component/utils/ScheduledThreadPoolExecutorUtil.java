package org.lb.drools.component.utils;

import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName ScheduledThreadPoolExecutorUtil
 * @Description TODO
 * @Author Terran
 * @Date 2020/9/28 14:12
 * @Version 1.0
 */
public class ScheduledThreadPoolExecutorUtil {

    /** 加载规则文件专用 */
    public static ScheduledThreadPoolExecutor RULE_SCHEDULE = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            new DefaultThreadFactory("rule-schedule-"),new ThreadPoolExecutor.AbortPolicy(){
                @Override
                public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                   throw new RejectedExecutionException("Autoload rule file exception:[{}]" + e);
                }
            }
    );

    /** 加载KIE专用 */
    public static ScheduledThreadPoolExecutor CACHE_KIE = new ScheduledThreadPoolExecutor(
            Runtime.getRuntime().availableProcessors() * 2,
            new DefaultThreadFactory("kie-schedule-"),new ThreadPoolExecutor.AbortPolicy(){
            @Override
            public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
                throw new RejectedExecutionException("Autoload kie file exception:[{}]" + e);
            }
        }
    );

    /** 默认线程工厂*/
    static class DefaultThreadFactory implements ThreadFactory{
        private static final AtomicInteger POOL_NUMBER = new AtomicInteger(1);
        private final ThreadGroup threadGroup;
        private final AtomicInteger atomicInteger = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(String name){
            SecurityManager securityManager = System.getSecurityManager();
            threadGroup = (securityManager != null) ? securityManager.getThreadGroup() :
                                                    Thread.currentThread().getThreadGroup();
            namePrefix = name + "-pool-" + POOL_NUMBER.getAndIncrement() + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(threadGroup,r,namePrefix + atomicInteger.getAndIncrement(),0);
            if (thread.isDaemon()){
                thread.setDaemon(false);
            }
            if (thread.getPriority() != Thread.NORM_PRIORITY){
                thread.setPriority(Thread.NORM_PRIORITY);
            }
            return thread;
        }
    }
}
