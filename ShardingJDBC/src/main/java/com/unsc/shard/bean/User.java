package com.unsc.shard.bean;

import lombok.Data;

import java.util.Date;

@Data
public class User {

	private Long id;//主键
	
	private String name;//姓名
	
	private String phone;//手机号
	
	private String email;//电子邮件

	private String password;//经过MD5加密的密码
	
	private Integer cityId; //城市id
    
    private Date createTime;//创建时间
    
    private Integer sex;//性别

    public User(Long id, String name, String phone, String email, String password, Integer cityId, Date createTime, Integer sex) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.cityId = cityId;
        this.createTime = createTime;
        this.sex = sex;
    }

    public User() {
    }

    public User(String name, String phone, String email, String password, Integer cityId, Integer sex) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.cityId = cityId;
        this.sex = sex;
    }

    public User(Long id, String name, String phone, String email, String password, Integer cityId, Integer sex) {
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