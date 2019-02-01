package com.unsc.shard.shadow.common;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 枚举 工具类 常用于 code msg 或者 code data 类的Enum
 * 根据一个已知条件去获得另一个参数的值 具体使用场景自己斟酌
 * 基于ODC第一版的EnumUtil而来 而那一版 我遇到了这辈子最垃圾的程序员 dongmy
 * @author Lu::JX
 * @date 2018/8/1 20:26
 */
public class EnumUtils {

    /**
     * 通过Enum的Code匹配对应的Enum常量
     * @param clazz 你要传入的Enum字节码对象
     * @param code 枚举的Code
     * @param <T> 给你个眼神自己体会
     * @return 匹配到的枚举类型
     */
    public static <T extends Enum<T>> T match(Class<T> clazz, Integer code) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T result = null;
        T[] constants = clazz.getEnumConstants();
        Method method = clazz.getDeclaredMethod("getCode");
        Integer targetCode;
        for (T constant : constants) {
            String codeStr = method.invoke(constant).toString();
            targetCode = Integer.valueOf(codeStr);
            if (targetCode.intValue() == code) {
                result = constant;
            }
        }
        return result;
    }

    /**
     * 通过Enum的Name匹配对应的Enum常量
     * @param clazz 枚举类的字节码对象
     * @param methodName 方法名 就是你Enum中的getMsg啊 getName的方法名
     * @param name 就是枚举中的name啦 msg什么的都不行的..一定要是name
     * @param <T> 呵呵
     * @return 对应的枚举
     */
    public static <T extends Enum<T>> T match(Class<T> clazz, String methodName, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        T result = null;
        T[] enumConstants = clazz.getEnumConstants();
        String getMethodName = "get" + Character.toUpperCase(methodName.charAt(0)) + methodName.substring(1);
        Method method = clazz.getDeclaredMethod(getMethodName);
        String targetName;
        for (T enumConstant : enumConstants) {
            targetName = method.invoke(enumConstant).toString();
            if (targetName.equals(name)) {
                result = enumConstant;
            }
        }
        return result;
    }
}
