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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;


import com.devfill.liganet.R;
import com.devfill.liganet.adapter.AllNewsAdapter;
import com.devfill.liganet.adapter.EconomicAdapter;
import com.devfill.liganet.helper.OnLoadMoreListener;
import com.devfill.liganet.helper.OnScrollingListener;
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

public class EconomicFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private static final String LOG_TAG = "EconomicFragmentTag";

    private List<News> economicList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EconomicAdapter economicAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int count_bitmap = 0;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtarget = null;

    int start = 0,end = 21;
    ProgressBar progressBarEconomic;
    public static boolean listIsShowed = false;
    Picasso picasso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_economic, container, false);

        Log.i(LOG_TAG, "onCreateView ");

        picasso = Picasso.with(getContext());

        progressBarEconomic = (ProgressBar) rootView.findViewById(R.id.progressBarEconomic);
        progressBarEconomic.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_economics);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        economicAdapter = new EconomicAdapter(getContext(),getActivity(),economicList,recyclerView);
        recyclerView.setAdapter(economicAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_economic);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);

        initLoadMoreListener();
        initOnScrollingListener();
        initRetrofit ();
        initTargetPicasso();
/*
       if(!listIsShowed){
            getEconomicList();
            listIsShowed = true;
        }*/


        return rootView;
    }

    public void pauseLoadImage(){

        picasso.pauseTag("load");

    }

    public void resumeLoadImage(){

        picasso.resumeTag("load");

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

    private void initOnScrollingListener(){

        economicAdapter.setOnScrollingListener(new OnScrollingListener() {
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

    private void initLoadMoreListener(){

        economicAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                economicList.add(null);
                economicAdapter.notifyItemInserted(economicList.size() - 1);


                economicList.remove(economicList.size() - 1);
                economicAdapter.notifyItemRemoved(economicList.size());
                //add items one by one
                start = economicList.size();
                end = start + 21;

                progressBarEconomic.setVisibility(View.VISIBLE);
                getEconomicList();


            }
        });
    }

    public void getEconomicList (){

        listIsShowed = true;

        if(start == 0){
            swipeRefreshLayout.setRefreshing(true);
        }

        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);

        }
        else {
            try {

                serverAPI.getEconomicNews(Integer.toString(start),Integer.toString(end)).enqueue(new Callback<ListNews>() {
                    @Override
                    public void onResponse(Call<ListNews> call, Response<ListNews> response) {

                        ListNews listNews = response.body();

                        try {

                            Collections.reverse(listNews.getNews());
                            economicList.addAll(listNews.getNews());
                            economicAdapter.notifyDataSetChanged();
                            economicAdapter.setLoaded();
                            swipeRefreshLayout.setRefreshing(false);
                            progressBarEconomic.setVisibility(View.INVISIBLE);

                        }
                        catch(Exception e){
                            swipeRefreshLayout.setRefreshing(false);
                            progressBarEconomic.setVisibility(View.INVISIBLE);
                            Toast.makeText(getActivity(), "Нет новостей на сервере!", Toast.LENGTH_LONG).show();

                        }

                        loadNextImage();

                        Log.i(LOG_TAG, "onResponse getListNews ");

                    }

                    @Override
                    public void onFailure(Call<ListNews> call, Throwable t) {

                        swipeRefreshLayout.setRefreshing(false);
//                        Toast.makeText(getActivity(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

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
        economicList.clear();

        start = 0;
        end = 21;
        getEconomicList();
        listIsShowed = true;

    }

    private void loadNextImage(){

        int height = 90;
        int width = 120;

        try{

            final float scale = getContext().getResources().getDisplayMetrics().density;
            height = (int) (90 * scale + 0.5f);
            width = (int) (120 * scale + 0.5f);
        }
        catch(Exception e){

            Log.d(LOG_TAG, "Не удалось загрузить ресурсы " + e.getMessage());

        }

        if(count_bitmap == economicList.size()) {
            count_bitmap = 0;
        }
        else{

            try {
                if(!economicList.get(count_bitmap).getImgUrl().equals(""))
                    picasso.load(economicList.get(count_bitmap).getImgUrl()).
                            tag("load").
                            resize(width, height).
                            into(loadtarget);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
            }
        }

    }

    void initTargetPicasso(){

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                if (economicList.size() > 0) {
                    economicList.get(count_bitmap).setBitmap(bitmap);
                    economicAdapter.notifyDataSetChanged();

                    count_bitmap++;
                    loadNextImage();
                }

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                count_bitmap++;
                loadNextImage();

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(LOG_TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        listIsShowed = false;
        Log.i(LOG_TAG, " onDestroy");
    }

}