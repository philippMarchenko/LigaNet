package com.devfill.liganet.network;


import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.NewsContent;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPI {

    public static final String BASE_URL = "http://api.mkdeveloper.ru/";


    @GET("/liga_net_api.php")
    Call<ListNews> getListNews(
            @Query(value = "event") String event);

    @GET("/liga_net_api.php")
    Call<NewsContent> getNewsContent(
            @Query(value = "event") String event,
            @Query(value = "link_href") String link_href);
}

