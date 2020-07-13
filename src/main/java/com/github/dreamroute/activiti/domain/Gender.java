package com.github.dreamroute.activiti.domain;

public enum Gender {
    COMMON(-1),
    MALE(1),
    FEMALE(0);

    private Integer code;

    Gender(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Gender common(Integer code) {
        this.code = code;
        return this;
    }
}
