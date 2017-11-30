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

    @SerializedName("isPhoto")
    @Expose
    private String isPhoto;

    @SerializedName("isVideo")
    @Expose
    private String isVideo;

    @SerializedName("time_ms")
    @Expose
    private String time_ms;



    private Bitmap bitmap;

    public String getIsVideo() {
        return isVideo;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public News(String time, String title,String linkHref,Bitmap bitmap){

        this.bitmap = bitmap;
        this.time = time;
        this.title = title;
        this.linkHref = linkHref;
    }

    public String getIs_photo() {
        return isPhoto;
    }

    public String getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getLinkHref() {
        return linkHref;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getTime_ms() {
        return time_ms;
    }

    public void setTime_ms(String time_ms) {
        this.time_ms = time_ms;
    }


}