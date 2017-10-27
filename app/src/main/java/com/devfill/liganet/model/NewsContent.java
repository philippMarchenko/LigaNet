package com.devfill.liganet.model;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewsContent {

    @SerializedName("Urls")
    @Expose
    private List<String> urls;

    @SerializedName("Data")
    @Expose
    private Data data;


    public static class Data {

        public Data(String annotation,String text,String date){

            this.annotation = annotation;
            this.text = text;
            this.date = date;

        }

        String annotation;
        String text;
        String date;
        String videoUrl;
        String title;
        String imageUrl;
        Bitmap bitmapToolbar;
       // Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
/*
        public Map<String, Drawable> getDrawableHashMap() {
            return drawableHashMap;
        }

        public void setDrawableHashMap(Map<String, Drawable> drawableHashMap) {
            this.drawableHashMap = drawableHashMap;
        }*/

        public Bitmap getBitmaToolbar() {
            return bitmapToolbar;
        }

        public void setBitmapToolbar(Bitmap bitmap) {
            this.bitmapToolbar = bitmap;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getVideoUrl() {
            return videoUrl;
        }

        public String getTitle() {
            return title;
        }

        public String getAnnotation() {
            return annotation;
        }

        public void setAnnotation(String annotation) {
            this.annotation = annotation;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}