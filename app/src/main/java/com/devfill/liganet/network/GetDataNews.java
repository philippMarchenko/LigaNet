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
        //List<ArticleNews> listNews = new ArrayList<>();
        Log.d(LOG_TAG, "params = " + params[0]);

        Document doc = null;    //Здесь хранится будет разобранный html документ

        try {

            doc = Jsoup.connect(params[0]).get();
        } catch (IOException e) {
            //Если не получилось считать
            e.printStackTrace();
        }

        //Если всё считалось, что вытаскиваем из считанного html документа заголовок
        if (doc!=null){


            Elements news_content = doc.select("div.news_content");
            Elements h1Tag = news_content.select("h1");
            Elements annotation = news_content.select("div.annotation");
            Elements date = news_content.select("div.date");
            Elements text = news_content.select("div.text");


            Elements img = news_content.select("div.img");
            Elements image = img.select("img");
            String imgUrl = "http://news.liga.net" + image.attr("src");
            String title = h1Tag.text();
            String annotationStr = annotation.text();
            String dateStr = date.text();
            String textStr = text.text();


            Log.d(LOG_TAG,"h1Tag " + h1Tag.text());
            Log.d(LOG_TAG,"date " + date.text());
            Log.d(LOG_TAG,"annotation " + annotation.text());
            Log.d(LOG_TAG,"text " + text.text());
            Log.d(LOG_TAG,"imgUrl " + imgUrl);


            articleNews = new ArticleNews(imgUrl,textStr,annotationStr,textStr,dateStr);
           // listNews.add(articleNews);

            /*Element all_news = doc.select("div.articles-list").first();     //достаем все дивы с новыми новостями

            Elements ul = doc.select("div.articles-list > ul");
            Elements li = ul.select("li"); // select all li from ul

            for(int i = 0; i < li.size(); i++){                       //проходимся по массиву новостей

                Elements date = li.get(i).select("div.date"); //достаем дату из дива
                Element title = li.get(i).select("div.title").first();

                Elements link = title.select("a");
                String linkHref = link.attr("href");
                Element t_link = title.child(0);

                News news = new News(date.text(),t_link.text());
                listNews.add(news);

            }*/
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