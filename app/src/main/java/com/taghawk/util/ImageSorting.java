package com.taghawk.util;

import com.taghawk.model.home.ImageList;

import java.util.Comparator;

public class ImageSorting implements Comparator {
    public int compare(Object o1, Object o2) {
        ImageList s1 = (ImageList) o1;
        ImageList s2 = (ImageList) o2;

        if (s1.getPosition() == s2.getPosition())
            return 0;
        else if (s1.getPosition() > s2.getPosition())
            return 1;
        else
            return -1;
    }
}  