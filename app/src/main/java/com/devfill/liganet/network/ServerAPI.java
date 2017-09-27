package com.devfill.liganet.network;


import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.NewsContent;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPI {

    public static final String BASE_URL = "http://api.mkdeveloper.ru/";

    @GET("/liga_net/get_news_content.php")
    Call<NewsContent> getNewsContent(@Query(value = "link_href") String link_href);

    @GET("/liga_net/get_all_news.php")
    Call<ListNews> getAllNews();
    @GET("/liga_net/get_politic_news.php")
    Call<ListNews> getPoliticNews();
    @GET("/liga_net/get_economic_news .php")
    Call<ListNews> getEconomicNews();
    @GET("/liga_net/get_world_news.php")
    Call<ListNews> getWorldNews();

}

