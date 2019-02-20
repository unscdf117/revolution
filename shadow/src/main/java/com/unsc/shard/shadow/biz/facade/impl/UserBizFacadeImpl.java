package com.unsc.shard.shadow.biz.facade.impl;

import com.unsc.shard.shadow.dto.DubboDto;
import com.unsc.shard.shadow.dto.UserDto;
import com.unsc.shard.shadow.facade.interfaces.UserBizFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * RPC UserBizFacadeImpl
 * @author DELL
 * @date 2018/12/25
 */
@Slf4j
@Service
public class UserBizFacadeImpl implements UserBizFacade {

    @Override
    public String operate(UserDto dto) {
        log.info("UserDto Received: {}", dto);
        return dto == null ? "failed" : "success";
    }

    @Override
    public String jungle(Map<String, Object> params) {
        if (params.get("array") instanceof List) {
            log.info("Array is List");
        }
        if (params.get("array") instanceof String[]) {
            log.info("Array is array");
        }
        DubboDto dto = (DubboDto) params.get("dto");
        String[] array = dto.getArray();
        System.out.println("is array ?" + array);
        log.info("is Array ? : {}", array);
        log.info("finished");
        return "200";
    }

    @Override
    public boolean checkUserInfoFromNC(UserDto dto) {
        //因为用友NC系统的脑残特性 所以沈阳弄了一个DataCenter 这个比起库存中心要快一点
        try {
            Thread.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean checkAvailableUser(UserDto dto) {
        //沈阳那边的StockCenter其实速度并不算快 因为要拆单 匹仓匹库存..唉
        try {
            Thread.sleep(40);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
