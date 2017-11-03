package com.devfill.liganet.ui.fragment_articles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.devfill.liganet.R;
import com.devfill.liganet.adapter.NewsAdapter;
import com.devfill.liganet.listeners.OnLoadMoreListener;
import com.devfill.liganet.listeners.OnScrollingListener;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.ServerAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = "NewsFragmentTag";

    private List<News> newsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBarAllNews;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtarget = null;
    private Picasso picasso;

    private int count_bitmap = 0;
    private int start = 0,end = 21;
    private int height = 90;
    private int width = 120;

    private boolean listIsShowed = false;

    private String url_news = "";

    private SavedFragment savedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_news, container, false);

        url_news = getArguments().getString("url_news");

        savedFragment = (SavedFragment) getFragmentManager().findFragmentByTag(url_news);

        if (savedFragment != null){
            newsList = savedFragment.getNews();
        }
        else{
            savedFragment = new SavedFragment();
            getFragmentManager().beginTransaction()
                    .add(savedFragment, url_news)
                    .commit();
        }


        picasso = Picasso.with(getContext());

        progressBarAllNews = (ProgressBar) rootView.findViewById(R.id.progressBarAllNews);
        progressBarAllNews.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_all_news);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        newsAdapter = new NewsAdapter(getContext(),getActivity(),newsList,recyclerView);
        recyclerView.setAdapter(newsAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_all_news);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);


        try{

            final float scale = getContext().getResources().getDisplayMetrics().density;
            height = (int) (90 * scale + 0.5f);
            width = (int) (120 * scale + 0.5f);
        }
        catch(Exception e){

            Log.d(LOG_TAG, "Не удалось загрузить ресурсы " + e.getMessage());

        }

        initLoadMoreListener();
        initOnScrollingListener();
        initRetrofit();
        initTargetPicasso();


        if(url_news.equals("http://api.mkdeveloper.ru/liga_net/get_all_news.php")){
             getNewsList();          //если это первый фрагмент,то запросим данные с сервера
             listIsShowed = true;    //флаг что мы уже показали список
        }

        return rootView;
    }

    private void initRetrofit (){

       HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
       interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

       OkHttpClient client = new OkHttpClient.Builder()
               .readTimeout(60, TimeUnit.SECONDS)
               .connectTimeout(60, TimeUnit.SECONDS)
               .addInterceptor(interceptor)
               .build();

       retrofit = new Retrofit.Builder()
               .baseUrl(ServerAPI.BASE_URL)
               .client(client)
               .addConverterFactory(GsonConverterFactory.create())
               .build();

       serverAPI = retrofit.create(ServerAPI.class);
   }

    private void initLoadMoreListener(){

        newsAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
               /* allNewsList.add(null);
                newsAdapter.notifyItemInserted(allNewsList.size() - 1);


                allNewsList.remove(allNewsList.size() - 1);
                newsAdapter.notifyItemRemoved(allNewsList.size());*/
                //add items one by one
                start = newsList.size();
                end = start + 21;

                progressBarAllNews.setVisibility(View.VISIBLE);
                getNewsList();


            }
        });
    }

    private void initOnScrollingListener(){

        newsAdapter.setOnScrollingListener(new OnScrollingListener() {
            @Override
            public void onScrollNow() {
                Log.d(LOG_TAG, "onScrollNow ");

                picasso.pauseTag("load");
            }

            @Override
            public void onStopScrolling() {
                Log.d(LOG_TAG, "onStopScrolling ");

                picasso.resumeTag("load");
            }
        });

    }

    public void getNewsList (){

        try{
            if(start == 0){
                swipeRefreshLayout.setRefreshing(true);
            }
        }
        catch (Exception e){

        }



        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);

        }
        else {
            try {

                serverAPI.getNews(url_news,Integer.toString(start),Integer.toString(end)).enqueue(new Callback<ListNews>() {
                    @Override
                    public void onResponse(Call<ListNews> call, Response<ListNews> response) {

                        ListNews listNews = response.body();

                        try {

                            Collections.reverse(listNews.getNews());
                            newsList.addAll(listNews.getNews());
                            newsAdapter.notifyDataSetChanged();
                            newsAdapter.setLoaded();
                            swipeRefreshLayout.setRefreshing(false);
                            progressBarAllNews.setVisibility(View.INVISIBLE);

                        }
                        catch(Exception e){
                            swipeRefreshLayout.setRefreshing(false);
                            progressBarAllNews.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Нет новостей на сервере!", Toast.LENGTH_LONG).show();

                        }

                        loadNextImage();



                     //   Log.i(LOG_TAG, "onResponse getListNews. url_news " + url_news);

                      //  Log.i(LOG_TAG, "onResponse getListNews. start " + start + " end " + end);

                    }

                    @Override
                    public void onFailure(Call<ListNews> call, Throwable t) {

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getListNews " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getListNews " + e.getMessage());
            }
        }
    }

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        newsList.clear();

        start = 0;
        end = 21;
        getNewsList();
        listIsShowed = true;

    }

    private void loadNextImage(){

        if(count_bitmap == newsList.size()) {
            count_bitmap = 0;
        }
        else{

            try {
                picasso.load(newsList.get(count_bitmap).getImgUrl()).
                        tag("load").
                        resize(width, height).
                        into(loadtarget);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;

                picasso.load(newsList.get(count_bitmap).getImgUrl()).
                        tag("load").
                        resize(width, height).
                        into(loadtarget);
            }
        }

    }

    void initTargetPicasso(){

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

               // Log.d(LOG_TAG, "onBitmapLoaded  ");

                if (newsList.size() > 0) {
                    newsList.get(count_bitmap).setBitmap(bitmap);
                    newsAdapter.notifyDataSetChanged();

                    count_bitmap++;
                    loadNextImage();
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                count_bitmap++;
                loadNextImage();
                // Log.d(LOG_TAG, "onBitmapFailed " + errorDrawable.toString());

            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    public void pauseLoadImage(){

        try{
            Log.i(LOG_TAG, " pauseLoadImage " + url_news);
            picasso.pauseTag("load");
        }
        catch(Exception e){

        }
    }

    public void resumeLoadImage(){
        try{

            Log.i(LOG_TAG, " resumeLoadImage " + url_news);
            picasso.resumeTag("load");
        }
        catch(Exception e){

        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();

        savedFragment.setNews(newsList);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

      //  Log.i(LOG_TAG, " onDestroy");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        try {
            if (isVisibleToUser) {
                if (!listIsShowed) {
                    getNewsList();
                    listIsShowed = true;
                }
                resumeLoadImage();
            }
            else {
                pauseLoadImage();

            }
        }
        catch (Exception e){
        }
    }
    }

