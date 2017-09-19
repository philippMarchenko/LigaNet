package com.devfill.liganet.model;


public class News {

    private String time;
    private String title;
    private String linkHref;



    public News(String time, String title,String linkHref){

        this.time = time;
        this.title = title;
        this.linkHref = linkHref;
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
}