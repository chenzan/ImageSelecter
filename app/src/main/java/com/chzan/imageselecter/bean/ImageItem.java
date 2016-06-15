package com.chzan.imageselecter.bean;

/**
 * Created by chenzan on 2016/6/6.
 */
public class ImageItem {
    public String path;
    public String name;
    public long time;

    public ImageItem(String path, String name, long time) {
        this.path = path;
        this.name = name;
        this.time = time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImageItem image = (ImageItem) o;

        return path != null ? path.equalsIgnoreCase(image.path) : image.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
