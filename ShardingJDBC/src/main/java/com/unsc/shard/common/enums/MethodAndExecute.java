package com.unsc.shard.common.enums;

import lombok.Getter;

/**
 * 导向枚举
 * @author DELL
 * @date 2018/12/20
 */
@Getter
public enum MethodAndExecute {
    /**默认 勿用*/
    NO_AND_NOTHING("nothing", "nowhere");

    MethodAndExecute(String methodName, String executeName) {
        this.executeName = executeName;
        this.methodName = methodName;
    }

    private String methodName;

    private String executeName;
}
