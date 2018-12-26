package com.unsc.shard.service;

import com.unsc.shard.bean.User;

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

}
