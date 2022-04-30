package com.yu.gridimageview;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ImageInfo {

    /**
     * 所有图片应该占用的高度
     */
    private int height = 0;
    /**
     * 所有图片应该占用的宽度
     */
    private int width = 0;
    private int row = 1;
    /**
     * 图片显示链接
     */
    @Nullable
    private String url = null;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    public void setUrl(@Nullable String url) {
        this.url = url;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @NonNull
    @Override
    public String toString() {
        return "ImageInfo{" +
                "height=" + height +
                ", width=" + width +
                ", row=" + row +
                ", url='" + url + '\'' +
                '}';
    }
}
