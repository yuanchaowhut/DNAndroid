package cn.com.egova.dbframework.bean;

import cn.com.egova.dbframework.annotation.DbField;
import cn.com.egova.dbframework.annotation.DbTable;

/**
 * Created by yuanchao on 2018/3/9.
 */

@DbTable("t_user")
public class User {
    @DbField("_id")
    private String id;
    private String name;
    @DbField("pwd")
    private String password;
    private String email;
    private Integer status;

    public User() {
    }

    public User(String id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
