package com.unsc.shard.common.exts;

/**
 * 分布式锁回调
 * @author DELL
 * @date 2018/1/2
 */
public interface DistributedLockCallback<T> {

    /**
     * 调用者必须在此方法中实现需要加分布式锁的业务逻辑
     * @return
     */
    T process();
 
    /**
     * 得到分布式锁名称
     * @return
     */
    String getLockName();
}
