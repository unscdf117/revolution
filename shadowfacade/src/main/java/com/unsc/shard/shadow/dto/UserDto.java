package com.unsc.shard.shadow.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * User DTO
 * @author DELL
 * @date 2018/12/25
 */
@Data
public class UserDto implements Serializable {

    private Long id;//主键

    private String name;//姓名

    private String phone;//手机号

    private String email;//电子邮件

    private String password;//经过MD5加密的密码

    private Integer cityId; //城市id

    private Date createTime;//创建时间

    private Integer sex;//性别

    public UserDto(Long id, String name, String phone, String email, String password, Integer cityId, Date createTime, Integer sex) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.cityId = cityId;
        this.createTime = createTime;
        this.sex = sex;
    }

    public UserDto() {
    }

    public UserDto(String name, String phone, String email, String password, Integer cityId, Integer sex) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.cityId = cityId;
        this.sex = sex;
    }

    public UserDto(Long id, String name, String phone, String email, String password, Integer cityId, Integer sex) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.cityId = cityId;
        this.createTime = createTime;
        this.sex = sex;
    }
}
