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

    /**
     * 压力测试使用 (库存中心)
     * 订单对象变成了UserDTO 我懒惰啊 :) 而且代替sku库存的字段是 phone :) 我还是懒惰啊
     * @param dto DTO
     * @return 不要在意这些细节
     */
    boolean checkAvailableUser(UserDto dto);

    /**
     * 压力测试使用 (DataCenter 用于替代NC这种脑残系统 NC不就是脑残的拼音首字母吗)
     * @param dto
     * @return
     */
    boolean checkUserInfoFromNC(UserDto dto);
}
