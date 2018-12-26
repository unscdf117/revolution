package com.unsc.shard.service.impl;

import com.unsc.shard.bean.User;
import com.unsc.shard.shadow.dto.DubboDto;
import com.unsc.shard.shadow.dto.UserDto;
import com.unsc.shard.shadow.facade.interfaces.UserBizFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by DELL on 2018/12/25.
 */
@Service
@Slf4j
public class UserOperateBizImpl {

    @Resource
    private UserBizFacade facade;

    public void execute() {
        User user = new User(281786684163313644L, "高老板", "10000", "10000@live.cn", "gaojing", 310000, 1);
        UserDto dto = new UserDto();
        BeanUtils.copyProperties(user, dto);
        String result = facade.operate(dto);
        log.info("Result : {}", result);
    }

    public void execute2() {
        Map<String, Object> param = new HashMap<>(2);
        String[] strs = new String[]{"5", "L", "P", "乖"};
        param.put("array", strs);
        DubboDto dto = new DubboDto();
        dto.setArray(strs);
        param.put("dto", dto);
        String result = facade.jungle(param);
        log.info("Result : {}", result);
    }
}
