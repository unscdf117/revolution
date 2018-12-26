package com.unsc.shard.mapper;

import com.unsc.shard.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
	/**
	 * 保存
	 */
	Long save(User user);
	
	/**
	 * 查询
	 * @param id
	 * @return
	 */
	User get(Long id);

    User findUser(User user);
}
