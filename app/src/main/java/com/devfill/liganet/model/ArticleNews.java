package com.devfill.liganet.model;




public class ArticleNews{

    private String imgUrl;
    private String text;
    private String title;
    private String date;


    private String annotation;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public ArticleNews(String imgUrl, String title, String annotation, String text, String date){

        this.annotation = annotation;
        this.imgUrl = imgUrl;
        this.title = title;
        this.text = text;
        this.date = date;

    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
