package com.notai.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 标签实体类
 */
@Entity(tableName = "tags")
public class Tag {
    @PrimaryKey(autoGenerate = true)
    private long id = 0;
    private String name;
    private String color = "#6200EE"; // 默认紫色

    public static final List<String> DEFAULT_COLORS = Arrays.asList(
            "#6200EE", // 紫色
            "#03DAC5", // 青色
            "#FF6B6B", // 红色
            "#4ECDC4", // 蓝绿色
            "#FFE66D", // 黄色
            "#A8E6CF"  // 绿色
    );

    public Tag() {
    }

    public Tag(String name) {
        this.name = name;
    }

    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public Tag(long id, String name, String color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return id == tag.id &&
                Objects.equals(name, tag.name) &&
                Objects.equals(color, tag.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, color);
    }
}

