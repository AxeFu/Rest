package com.example.server.mock;

import com.example.server.reflection.annotations.TableField;

import java.util.List;

public class DTOMock {

    public List<String> stringList = List.of("Hello", "World!");

    @TableField("name")
    private String name;
    @TableField("lastName")
    private String lastName;
    @TableField("sex")
    private String sex;

    @TableField("birth_date")
    private String date;

    public int publicField = 15;

    public DTOMock(String name, String lastName, String sex, String date) {
        this.name = name;
        this.lastName = lastName;
        this.sex = sex;
        this.date = date;
    }
}
