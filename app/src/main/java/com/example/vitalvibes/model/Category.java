package com.example.vitalvibes.model;

public class Category {
    private int Id;
    private String imgUrl;
    private String name;

    public Category() {
    }

    public Category(int id, String imgUrl, String name) {
        Id = id;
        this.imgUrl = imgUrl;
        this.name = name;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
