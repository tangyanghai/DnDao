package com.example.administrator.androidstuct;

import com.example.daolib.annotation.DbField;
import com.example.daolib.annotation.DbTable;

/**
 * @author : Administrator
 * @time : 18:10
 * @for :
 */
@DbTable(tableName = "user")
public class User {
    @DbField("_id")
    private Integer id;
    @DbField("_name")
    private String name;

    private Boolean isMan;

    public Boolean getMan() {
        return isMan;
    }

    public void setMan(Boolean man) {
        isMan = man;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isMan=" + isMan +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }
}
