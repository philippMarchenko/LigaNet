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
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.EconomicAdapter;
import com.devfill.liganet.adapter.PoliticAdapter;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.GetListNews;
import com.devfill.liganet.network.ServerAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EconomicFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "EconomicFragmentTag";

    private List<News> economicList = new ArrayList<>();
    private RecyclerView recyclerView;
    private EconomicAdapter economicAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtarget = null;

    private int count_bitmap = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_economic, container, false);

        Log.i(LOG_TAG, "onCreateView ");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_economic);
        economicAdapter = new EconomicAdapter(getContext(),getActivity(),economicList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(economicAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_economic);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);

        initRetrofit();
        initTargetPicasso();

        getEconomicsNewsList();
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

    private void getEconomicsNewsList (){

        swipeRefreshLayout.setRefreshing(true);

        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
        }
        else {
            try {

                serverAPI.getEconomicNews().enqueue(new Callback<ListNews>() {
                    @Override
                    public void onResponse(Call<ListNews> call, Response<ListNews> response) {

                        ListNews listNews = response.body();

                        economicList.addAll(listNews.getNews());
                        economicAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);


                        loadNextImage();

                        Log.i(LOG_TAG, "onResponse getListNews size" + listNews.getNews().size());
                        Log.i(LOG_TAG, "onResponse getListNews getTitle()" + listNews.getNews().get(0).getTitle());

                    }

                    @Override
                    public void onFailure(Call<ListNews> call, Throwable t) {

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getListNews " + t.toString());
                    }
                });
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Ошибка запроса к серверу!" + e.getMessage(), Toast.LENGTH_LONG).show();
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
                Picasso.with(getContext()).load(economicList.get(count_bitmap).getImgUrl()).resize(width, height).into(loadtarget);
            }
            catch (Exception e){

                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
                Picasso.with(getContext()).load(economicList.get(count_bitmap).getImgUrl()).resize(width, height).into(loadtarget);
            }
        }

    }

    private void initTargetPicasso(){

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                economicList.get(count_bitmap).setBitmap(bitmap);
                economicAdapter.notifyDataSetChanged();

                count_bitmap++;
                loadNextImage();
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
    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        getEconomicsNewsList();
    }
}