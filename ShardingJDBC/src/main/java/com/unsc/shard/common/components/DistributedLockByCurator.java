//package com.unsc.shard.common.components;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.curator.framework.CuratorFramework;
//import org.apache.curator.framework.recipes.cache.PathChildrenCache;
//import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
//import org.apache.zookeeper.CreateMode;
//import org.apache.zookeeper.ZooDefs;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import java.util.concurrent.CountDownLatch;
//
///**
// * Curator + ZK 实现分布式锁
// * @author DELL
// */
//@Component
//@Slf4j
//public class DistributedLockByCurator implements InitializingBean {
//    /**
//     * 上锁根节点路径常量
//     */
//    private final static String ROOT_PATH_LOCK = "RootLock";
//    /**
//     * OMG 是个CountDownLatch
//     */
//    private CountDownLatch countDownLatch = new CountDownLatch(1);
//
//    @Resource
//    private CuratorFramework curatorFramework;
//
//    /**
//     * 获取锁
//     */
//    public void acquireDistributedLock(String path) {
//        String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
//        while (true) {
//            try {
//                curatorFramework
//                        .create()
//                        .creatingParentsIfNeeded()
//                        .withMode(CreateMode.EPHEMERAL)
//                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
//                        .forPath(keyPath);
//                log.info("Acquire lock succeed (n_n)  !! from path:{}", keyPath);
//                break;
//            } catch (Exception e) {
//                log.info("Acquire lock failed (T_T) !! from path:{} curator will try again  ", keyPath);
//                try {
//                    if (countDownLatch.getCount() <= 0) {
//                        countDownLatch = new CountDownLatch(1);
//                    }
//                    countDownLatch.await();
//                } catch (InterruptedException e1) {
//                    e1.printStackTrace();
//                }
//            }
//        }
//    }
//
//    /**
//     * 释放锁
//     */
//    public boolean releaseDistributedLock(String path) {
//        try {
//            String keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
//            if (curatorFramework.checkExists().forPath(keyPath) != null) {
//                curatorFramework.delete().forPath(keyPath);
//            }
//        } catch (Exception e) {
//            log.error("failed to release lock");
//            return false;
//        }
//        return true;
//    }
//
//    /**
//     * 创建watcher
//     */
//    private void addWatcher(String path) throws Exception {
//        String keyPath;
//        if (path.equals(ROOT_PATH_LOCK)) {
//            keyPath = "/" + path;
//        } else {
//            keyPath = "/" + ROOT_PATH_LOCK + "/" + path;
//        }
//        final PathChildrenCache cache = new PathChildrenCache(curatorFramework, keyPath, false);
//        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
//        cache.getListenable().addListener((client, event) -> {
//            if (event.getType().equals(PathChildrenCacheEvent.Type.CHILD_REMOVED)) {
//                String oldPath = event.getData().getPath();
//                log.info("success to release lock for path:{}", oldPath);
//                if (oldPath.contains(path)) {
//                    //释放计数器，让当前的请求获取锁
//                    countDownLatch.countDown();
//                }
//            }
//        });
//    }
//
//    /**
//     * 创建父节点 并创建永久节点
//     */
//
//    @Override
//    public void afterPropertiesSet() {
//        curatorFramework = curatorFramework.usingNamespace("lock-namespace");
//        String path = "/" + ROOT_PATH_LOCK;
//        try {
//            if (curatorFramework.checkExists().forPath(path) == null) {
//                curatorFramework.create()
//                        .creatingParentsIfNeeded()
//                        .withMode(CreateMode.PERSISTENT)
//                        .withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
//                        .forPath(path);
//            }
//            addWatcher(ROOT_PATH_LOCK);
//            log.info("root path‘s watcher created !!");
//        } catch (Exception e) {
//            log.error("connect zookeeper failed please check the log >> {}", e.getMessage(), e);
//        }
//    }
//}