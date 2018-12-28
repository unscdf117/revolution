package com.unsc.shard.web.api;

import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 登陆
 * @author DELL
 * @date 2018/12/28
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Resource
    private RedissonClient client;

    @GetMapping("/mobile/{user}/{password}")
    public Mono<String> mobile(@PathVariable("user") String user, @PathVariable("password") String password) {
        RBucket<Object> bucket = client.getBucket(user);
        Object customer = bucket.get();
        if (customer == null) {
            //未登陆 写入
            bucket.set("mobile:" + user + "/:" + password, 20, TimeUnit.SECONDS);
            return Mono.just(" Mobile您尚未登陆 现已帮您登陆");
        }else {
            String s = String.valueOf(customer);
            String[] users = s.split("$split$");
            String set = "";
            for (var i = 0; i < users.length; i++) {
                if (!users[i].contains("mobile:") && !users[i].contains("/:")) {
                    set +=  users[i] + "$split$" + "mobile:" + user + "/:" + password;
                }else {
                    set = users[i];
                }
            }
            bucket.set(set, 20, TimeUnit.SECONDS);
            return Mono.just(bucket.get().toString());
        }
    }

    @GetMapping("/web/{user}/{password}")
    public Mono<String> web(@PathVariable("user") String user, @PathVariable("password") String password) {
        RBucket<Object> bucket = client.getBucket(user);
        Object customer = bucket.get();
        if (customer == null) {
            //未登陆 写入  20秒过期
            bucket.set("web:" + user + "#" + password, 20, TimeUnit.SECONDS);
            return Mono.just(" web您尚未登陆 现已帮您登陆");
        }else {
            String s = String.valueOf(customer);
            String[] users = s.split("$split$");
            String set = "";
            for (var i = 0; i < users.length; i++) {
                if (!users[i].contains("web") && !users[i].contains("#")) {
                    set += users[i] + "$split$" + "web:" + user + "#" + password;
                }else {
                    set = users[i];
                }
            }
            bucket.set(set, 20, TimeUnit.SECONDS);
            return Mono.just(bucket.get().toString());
        }
    }

    @GetMapping("/check/{user}/{pw}/{method}")
    public Mono<String> check(@PathVariable("user") String user, @PathVariable("pw") String pw, @PathVariable("method") String method) {
        RBucket<Object> bucket = client.getBucket(user);
        Object customer = bucket.get();
        if (bucket.get() == null) {
            return Mono.just("当前用户无任何登陆");
        }
        String value = String.valueOf(customer);
        String[] users = value.split("$split$");
        String res = "";
        for (var i = 0; i < users.length; i++) {
            if(users[i].contains("web:") && users[i].contains("#")) res += "web已经登陆";
            if(users[i].contains("mobile:") && users[i].contains("/:")) res += "mobile已经登陆";
        }
        res += "当前用户信息为: " + (String) bucket.get() + " 离自动过期还有" + bucket.remainTimeToLive() / 1000 + "秒";
        return Mono.just(res);
    }
}
