package com.unsc.shard.bean;

public class Address {
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getLit() {
        return lit;
    }

    public void setLit(Integer lit) {
        this.lit = lit;
    }

    private Long id;
	 
	private String code;
	 
	private String name;
	 
	private String pid;
	 
	private Integer type;
	 
	private Integer lit;
	 
}
