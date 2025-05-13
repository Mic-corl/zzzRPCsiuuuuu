package com.zyzz;

import java.io.Serializable;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 用户名
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
