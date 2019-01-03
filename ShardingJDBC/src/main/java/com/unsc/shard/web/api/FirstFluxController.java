package com.unsc.shard.web.api;

import com.unsc.shard.bean.User;
import com.unsc.shard.common.aop.annotation.RevolutionLock;
import com.unsc.shard.common.aop.annotation.Visit;
import com.unsc.shard.service.UserService;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 第一个Flux Controller
 * @author Lu::JX
 * @date 2018/12/18
 */
@RestController
public class FirstFluxController {

    @Resource
    private UserService userService;

    @Resource
    private RedissonClient redissonClient;

    @GetMapping("/sos/{message}")
    public String sos(@PathVariable(value = "message") String message) {
        return message + "$$$";
    }

    @GetMapping("/flux/{message}")
    public Flux<String> flux(@PathVariable("message") String message) {

        return getFluxSource(message);
    }

    private Flux<String> getFluxSource(String message) {
        var list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add(message);
        return Flux.fromIterable(list);
    }

    @GetMapping("/addUser")
    public Mono<Long> addUser() {
        User user = new User(281786684163313644L, "高老板", "10000", "10000@live.cn", "gaojing", 310000, 1);
        var cf = new CompletableFuture<Long>();
        new Thread(() -> {
            Long count = 0L;
            try {
                Thread.sleep(5000);
                count = userService.save(user);
            } catch (InterruptedException e) {
                e.printStackTrace();
                cf.completeExceptionally(e);
            }
            cf.complete(count);
        }).start();
        System.out.println("s");
        return Mono.fromFuture(cf);
    }

    @GetMapping("/checkUser")
    @Visit(method = "aaa")
    public User checkUser() {
        User user = new User();
        user.setPhone("10000");

        return userService.findUser(user);
    }

    /**
     * 分布式事务测试
     * @return
     */
    @RequestMapping("/multitx")
    public String multiTx() {
        return userService.multiTxTest();
    }

    @GetMapping("/mono/user")
    public Mono<User> getMonoUser() {
        User user = new User(281786684163313644L, "高老板", "10000", "10000@live.cn", "gaojing", 310000, 1);
        return Mono.just(user);
    }

    @GetMapping("/lock")
    public Mono<String> testRedisLock() throws InterruptedException {
        ExecutorService pool = Executors.newCachedThreadPool();
        CountDownLatch latch = new CountDownLatch(10);
        for (var i = 0; i < 10; i++) {
            pool.submit(() -> {
                tryRevolutionLock(latch);
            });
        }
        latch.await();
        return Mono.just("OK 200!!");
    }

    @RevolutionLock(tryLock = true, fairLock = true, waitTime = 20L, exipreTime = 8L)
    private void tryRevolutionLock(CountDownLatch latch) {
        latch.countDown();
        System.out.println(Thread.currentThread().getName() + " : " + latch.getCount());
    }
}
