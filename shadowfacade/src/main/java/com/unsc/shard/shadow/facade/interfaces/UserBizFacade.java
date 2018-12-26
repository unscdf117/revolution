package com.unsc.shard.shadow.facade.interfaces;

import com.unsc.shard.shadow.dto.UserDto;

import java.util.Map;

/**
 * User Biz Facade
 * @author DELL
 * @date 2018/12/25
 */
public interface UserBizFacade {

    String operate(UserDto dto);

    String jungle(Map<String, Object> params);
}
