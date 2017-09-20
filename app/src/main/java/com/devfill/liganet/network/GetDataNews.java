package com.devfill.liganet.network;

import android.os.AsyncTask;
import android.util.Log;

import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.model.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetDataNews extends AsyncTask<String, Void, ArticleNews> {

    private static final String LOG_TAG = "GetDataNewsTag";

    private String date;
    private String url;
    private String text;

    public IGetDataNewsListener mIGetDataNewsListener;

    public GetDataNews(IGetDataNewsListener iGetDataNewsListener){
        mIGetDataNewsListener = iGetDataNewsListener;
    }

    @Override
    protected ArticleNews doInBackground(String... params) {

        ArticleNews articleNews = null;
        String imgUrl;
        String title;
        String annotationStr;
        String dateStr ;
        String textStr;

        Log.d(LOG_TAG, "params 0 = " + params[0]);

        Document doc = null;    //Здесь хранится будет разобранный html документ

        try {

            doc = Jsoup.connect(params[0]).get();
        } catch (IOException e) {
            //Если не получилось считать
            e.printStackTrace();
        }

        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
        if (doc!=null && !params[1].equals("img")){


            Elements news_content = doc.select("div.news_content");
            Elements h1Tag = news_content.select("h1");
            Elements annotation = news_content.select("div.annotation");
            Elements date = news_content.select("div.date");
            Elements text = news_content.select("div.text");


            Elements img = news_content.select("div.img");
            Elements image = img.select("img");
            imgUrl = "http://news.liga.net" + image.attr("src");
            title = h1Tag.text();
            annotationStr = annotation.text();
            dateStr = date.text();
            textStr = text.text();

            if(dateStr == ""){

                Log.d(LOG_TAG,"Другой тип статьи ");

                Elements mH1Tag = doc.select("h1");
                Elements mText = doc.select("div.text");
                Elements mImg = doc.select("div.img");
                Elements mImage = mImg.select("img");
                Elements mAnnotation = doc.select("div.annotation");
                Elements mDate = doc.select("div.date");

                imgUrl = params[2] + mImage.attr("src");
                title = mH1Tag.text();
                annotationStr = mAnnotation.text();
                dateStr = mDate.text();
                textStr = mText.text();
            }

            Log.d(LOG_TAG,"h1Tag " + title);
            Log.d(LOG_TAG,"date " + dateStr);
            Log.d(LOG_TAG,"annotation " + annotationStr);
            Log.d(LOG_TAG,"text " + textStr);
            Log.d(LOG_TAG,"imgUrl " + imgUrl);
            articleNews = new ArticleNews(imgUrl,textStr,annotationStr,textStr,dateStr,params[3]);

        }
        else if(params[1].equals("img")){

            Elements img = doc.select("div.img");
            Elements image = img.select("img");
            imgUrl = params[2] + image.attr("src");
            Log.d(LOG_TAG,"Достаем только ссылку на картинку imgUrl " + imgUrl);
            articleNews = new ArticleNews(imgUrl,"","","","",params[3]);
        }
        else
            text = "Ошибка";

        return articleNews;
    }
    public interface IGetDataNewsListener {
        public void onGetDataNewsFinished(ArticleNews articleNews);
    }

    @Override
    protected void onPostExecute(ArticleNews articleNews) {
        super.onPostExecute(articleNews);

        mIGetDataNewsListener.onGetDataNewsFinished(articleNews);
    }
}