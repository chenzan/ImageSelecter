package com.chzan.imageselecter.bean;

import java.util.List;

/**
 * bucket
 * Created by chenzan on 2016/6/8.
 */
public class ImageFolder {
    public String name;
    public String path;
    public ImageItem cover;
    public List<ImageItem> childImages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageFolder that = (ImageFolder) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
