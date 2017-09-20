package com.devfill.liganet.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.network.GetDataNews;

import java.net.MalformedURLException;
import java.net.URL;

public class ArticleNewsActivity extends AppCompatActivity implements GetDataNews.IGetDataNewsListener{


    private static final String LOG_TAG = "ArticleNewsActivityTag";

    private TextView dateAtricle,anotation,textArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_news);

        URL url = null;

        Log.d(LOG_TAG, "onCreate ArticleNewsFragment");

        dateAtricle = (TextView) findViewById(R.id.dateAtricle);
        anotation = (TextView) findViewById(R.id.anotation);
        textArticle = (TextView) findViewById(R.id.textArticle);

        String linkHref = getIntent().getStringExtra("linkHref");

        GetDataNews getDataNews = new GetDataNews(this);

        if (!URLUtil.isValidUrl(linkHref)) {                           //если нет начала сайта
            try {
                url = new URL(linkHref);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            getDataNews.execute("http://news.liga.net" + linkHref,"","http://news.liga.net"); //добавим его
        }
        else{
            try {
                url = new URL(linkHref);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            getDataNews.execute(linkHref,"",url.getHost());
        }
    }

    @Override
    public void onGetDataNewsFinished(ArticleNews articleNews) {

        if(articleNews != null){
            Log.d(LOG_TAG,"getText " + articleNews.getText());
            Log.d(LOG_TAG,"getAnnotation " + articleNews.getAnnotation());
            Log.d(LOG_TAG,"getTitle " + articleNews.getTitle());
            Log.d(LOG_TAG,"getDate " + articleNews.getDate());
            Log.d(LOG_TAG,"getImgUrl " + articleNews.getImgUrl());

            dateAtricle.setText(articleNews.getDate());
            anotation.setText(articleNews.getAnnotation());
            textArticle.setText(articleNews.getText());
        }

        Log.d(LOG_TAG,"Не удалось распознать статью");

    }
}