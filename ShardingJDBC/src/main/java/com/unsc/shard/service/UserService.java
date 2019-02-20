package com.unsc.shard.service;

import com.unsc.shard.bean.User;

import java.util.List;

/**
 * @author DELL
 */
public interface UserService {
    /**
     * 增
     * @param user
     * @return
     */
	Long save(User user);

    /**
     * 查 根据主键
     * @param l
     * @return
     */
	User get(Long l);

    /**
     * 根据属性 查
     * @param user
     * @return
     */
    User findUser(User user);

    String multiTxTest();

    /**
     * 压力测试使用 使用User替代Order
     * @param user user 替代 order
     * @param list
     * @return 呵呵
     */
    String mockOrderOperateWithUser(User user, List<User> list);
}
