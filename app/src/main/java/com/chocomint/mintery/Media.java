package com.chocomint.mintery;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

public class Media implements Serializable {
    int id;
    String path;
    int type;
    Date dateModified;
    Long duration;
    String name;
    String size;

    public Media(int id, String path, int type, Date dateModified, Long duration, String name, String size) {
        this.id = id;
        this.path = path;
        this.type = type;
        this.dateModified = dateModified;
        this.duration = duration;
        this.name = name;
        this.size = size;
    }
}
