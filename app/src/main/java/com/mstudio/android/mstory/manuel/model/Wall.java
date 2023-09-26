package com.mstudio.android.mstory.manuel.model;

public class Wall {
    String id;
    String image_wallpaper;
    String type_wallpaper;
    String tag_wallpaper;
    String video_wallpaper;
    int width;
    int height;
    public Wall(String id, String image_wallpaper,String type_wallpaper,int width,int heig,String tag_wallpaper,String video_wallpaper) {
        this.id = id;
        this.image_wallpaper = image_wallpaper;
        this.type_wallpaper = type_wallpaper;
        this.width = width;
        this.height = height;
        this.video_wallpaper =  video_wallpaper;
        this.tag_wallpaper = tag_wallpaper;
    }
    public Wall() {

    }

    public String getVideo_wallpaper() {
        return video_wallpaper;
    }

    public void setVideo_wallpaper(String video_wallpaper) {
        this.video_wallpaper = video_wallpaper;
    }

    public String getTag_wallpaper() {
        return tag_wallpaper;
    }

    public void setTag_wallpaper(String tag_wallpaper) {
        this.tag_wallpaper = tag_wallpaper;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType_wallpaper() {
        return type_wallpaper;
    }

    public void setType_wallpaper(String type_wallpaper) {
        this.type_wallpaper = type_wallpaper;
    }

    public String getImage_wallpaper() {
        return image_wallpaper;
    }

    public void setImage_wallpaper(String image_wallpaper) {
        this.image_wallpaper = image_wallpaper;
    }


}
