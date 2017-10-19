package com.devfill.liganet.network;


import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.model.PhotoContent;
import com.devfill.liganet.model.VideoContent;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServerAPI {

    public static final String BASE_URL = "http://api.mkdeveloper.ru/";

    @GET("/liga_net/get_news_content.php")
    Call<NewsContent> getNewsContent(@Query(value = "link_href") String link_href);

    @GET("/liga_net/get_photo_content.php")
    Call<PhotoContent> getPhotoContent(@Query(value = "link_href") String link_href);
    @GET("/liga_net/get_video_content.php")
    Call<VideoContent> getVideoContent(@Query(value = "link_href") String link_href);

    @GET("/liga_net/get_all_news.php")
    Call<ListNews> getAllNews(@Query(value = "start") String start,
                              @Query(value = "end") String end);
    @GET("/liga_net/get_politic_news.php")
    Call<ListNews> getPoliticNews(@Query(value = "start") String start,
                                  @Query(value = "end") String end);
    @GET("/liga_net/get_economic_news .php")
    Call<ListNews> getEconomicNews(@Query(value = "start") String start,
                                   @Query(value = "end") String end);
    @GET("/liga_net/get_world_news.php")
    Call<ListNews> getWorldNews(@Query(value = "start") String start,
                                @Query(value = "end") String end);

    @GET("/liga_net/get_photo_list.php")
    Call<ListNews> getPhotoNews(@Query(value = "start") String start,
                                @Query(value = "end") String end);
    @GET("/liga_net/get_video_list.php")
    Call<ListNews> getVideoNews(@Query(value = "start") String start,
                                @Query(value = "end") String end);

}

