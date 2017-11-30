package com.devfill.liganet.model;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Valera on 29.11.2017.
 */

public class MoreNews {




    @SerializedName("Articles")
    @Expose
    private List<Article> articleList;


    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public class Article {

        private String date;
        private String title_text;
        private String title_href;
        private String imgUrl;

        private Bitmap bitmap;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle_text() {
            return title_text;
        }

        public void setTitle_text(String title_text) {
            this.title_text = title_text;
        }

        public String getTitle_href() {
            return title_href;
        }

        public void setTitle_href(String title_href) {
            this.title_href = title_href;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }

        public void setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public String getImgUrl() {
            return imgUrl;
        }

        public void setImgUrl(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }
}
