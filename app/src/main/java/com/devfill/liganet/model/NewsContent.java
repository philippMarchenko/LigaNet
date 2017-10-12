package com.devfill.liganet.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewsContent {

    @SerializedName("Urls")
    @Expose
    private List<String> urls;

    @SerializedName("Data")
    @Expose
    private Data data;



    public class Data {

        String annotation;
        String text;
        String date;
        String videoUrl;
        String title;

        public String getDate() {
            return date;
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