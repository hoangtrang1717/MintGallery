package com.chocomint.mintery;

import java.util.Comparator;

public class SortByModified implements Comparator<Media> {
    @Override
    public int compare(Media media, Media t1) {
        return t1.dateModified.compareTo(media.dateModified);
    }
}
