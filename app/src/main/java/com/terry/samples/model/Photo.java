package com.terry.samples.model;

/**
 * Created by terry on 2016/4/21.
 */
public class Photo {
    private int id;
    private String name;
    private String path;

    public Photo(int id, String name, String path) {
        this.id = id;
        this.name = name;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
