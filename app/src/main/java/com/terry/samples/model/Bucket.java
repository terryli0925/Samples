package com.terry.samples.model;

/**
 * Created by terry on 2016/4/21.
 */
public class Bucket {
    private String name;
    private int photoCount;

    public Bucket(String name, int count) {
        this.name = name;
        this.photoCount = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }
}
