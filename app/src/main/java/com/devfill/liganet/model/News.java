package com.devfill.liganet.model;


import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class News {

    @SerializedName("date")
    @Expose
    private String time;
    @SerializedName("text")
    @Expose
    private String title;
    @SerializedName("articleUrl")
    @Expose
    private String linkHref;
    @SerializedName("imgUrl")
    @Expose
    private String imgUrl;
    @SerializedName("videoUrl")
    @Expose
    private String videoUrl;


    @SerializedName("isPhoto")
    @Expose
    private String isPhoto;

    private Bitmap bitmap;


    public News(String time, String title,String linkHref,Bitmap bitmap){

        this.bitmap = bitmap;
        this.time = time;
        this.title = title;
        this.linkHref = linkHref;
    }
    public String getIs_photo() {
        return isPhoto;
    }

    public void setIs_photo(String is_photo) {
        this.isPhoto = is_photo;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }

    public String getlinkHref() {
        return linkHref;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

}