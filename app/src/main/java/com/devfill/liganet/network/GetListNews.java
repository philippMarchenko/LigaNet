package com.devfill.liganet.network;

import android.os.AsyncTask;
import android.util.Log;

import com.devfill.liganet.model.News;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GetListNews extends AsyncTask<String, Void, List<News>> {

    private static final String LOG_TAG = "GetlistNewsTag";

    private String date;
    private String url;
    private String text;

    public IGetListNewsListener mIGetListNewsrListener;

    public GetListNews(IGetListNewsListener iGetListNewsrListener){
        mIGetListNewsrListener = iGetListNewsrListener;
    }

    @Override
    protected List<News> doInBackground(String... params) {

        List<News> listNews = new ArrayList<>();
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
            text = doc.text();

            Element all_news = doc.select("div.articles-list").first();     //достаем все дивы с новыми новостями

            Elements ul = doc.select("div.articles-list > ul");
            Elements li = ul.select("li"); // select all li from ul

            for(int i = 0; i < li.size(); i++){                       //проходимся по массиву новостей

                Elements date = li.get(i).select("div.date"); //достаем дату из дива
                Element title = li.get(i).select("div.title").first();

                Elements link = title.select("a");
                String linkHref = link.attr("href");
                Element t_link = title.child(0);

                Log.d(LOG_TAG, "linkHref = " + linkHref);


                News news = new News(date.text(),t_link.text(),linkHref);
                listNews.add(news);

            }
        }
        else
            text = "Ошибка";

        return listNews;
    }
    public interface IGetListNewsListener {
        public void onGetListNewsFinished(List<News> news);
    }

    @Override
    protected void onPostExecute(List<News> list) {
        super.onPostExecute(list);

        mIGetListNewsrListener.onGetListNewsFinished(list);
    }
}