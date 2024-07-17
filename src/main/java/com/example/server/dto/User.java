package com.example.server.dto;

import com.example.server.reflection.annotations.TableField;

public class User  {
    @TableField(value = "id", isKey = true)
    public Long id;
    @TableField("first_name")
    public String firstName;
    @TableField("last_name")
    public String lastName;
    @TableField("login")
    public String login;
    @TableField("hash")
    public String password;

    public User(Long id, String firstName, String lastName, String login, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.login = login;
        this.password = password;
        this.id = id;
    }
}
