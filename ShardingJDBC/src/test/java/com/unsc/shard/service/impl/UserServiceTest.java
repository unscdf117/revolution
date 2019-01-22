package com.unsc.shard.service.impl;

import com.unsc.shard.BaseTestCase;
import com.unsc.shard.bean.User;
import com.unsc.shard.mapper.UserMapper;
import com.unsc.shard.service.UserService;
import org.junit.Test;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 * Created by DELL on 2018/12/25.
 */
public class UserServiceTest extends BaseTestCase {

    @Resource
    private UserOperateBizImpl userOperateBiz;

    @Resource
    private UserMapper userDao;

    @Test
    public void test() {
        userOperateBiz.execute();
    }

    @Test
    public void test2() {
        userOperateBiz.execute2();
    }

    @Test
    public void testFindUser() {
        String name = "狗比黄凯";
        User hk = new User();
        hk.setName(name);
        User user = userDao.findUser(hk);
        System.out.println(user);
    }
}