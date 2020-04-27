package com.example.gallery;

import java.util.Date;

public class ImageInformation {
    String name;
    Date date;
    int size;
    int img;
    public ImageInformation(){

    }
    public ImageInformation(int img){
        this.img=img;
    }
    public ImageInformation(String name, int img){
        this.name=name;
        this.img=img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}

