package com.example.gallery.Sort;

import java.io.File;
import java.util.Comparator;

public class SortByModified implements Comparator<File> {
    @Override
    public int compare(File a, File b) {
        return Long.compare(a.lastModified(), b.lastModified());
    }
}