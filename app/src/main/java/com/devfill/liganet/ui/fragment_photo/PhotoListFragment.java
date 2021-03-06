package com.devfill.liganet.ui.fragment_photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import com.devfill.liganet.adapter.PhotoListAdapter;
import com.devfill.liganet.listeners.OnLoadMoreListener;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.ServerAPI;
import com.devfill.liganet.ui.fragment_articles.SavedFragment;
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

public class PhotoListFragment extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "PhotoListFragmentTag";

    private List<News> photoList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PhotoListAdapter photoListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int count_bitmap = 0;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtarget = null;
    private Picasso picasso;


    FragmentTransaction ft;

    private int start = 0,end = 21;
    private ProgressBar progressBarPhotoList;
    private boolean listIsShowed = false;

    private SavedFragment savedFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setUserVisibleHint(false);

        View rootView = inflater.inflate(R.layout.fragment_photo_list, container, false);

        Log.i(LOG_TAG, "onCreateView ");

        savedFragment = (SavedFragment) getFragmentManager().findFragmentByTag("photo_list");

        if (savedFragment != null){
            photoList = savedFragment.getNews();
        }
        else{
            savedFragment = new SavedFragment();
            getFragmentManager().beginTransaction()
                    .add(savedFragment, "photo_list")
                    .commit();
        }



        picasso = Picasso.with(getContext());

        progressBarPhotoList = (ProgressBar) rootView.findViewById(R.id.progressBarPhotoList);
        progressBarPhotoList.setVisibility(View.INVISIBLE);

        ft = getFragmentManager().beginTransaction();

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_photo);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        photoListAdapter = new PhotoListAdapter(getContext(),getActivity(),ft,photoList,recyclerView);
        recyclerView.setAdapter(photoListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_photo);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);

        initLoadMoreListener();
        initRetrofit ();
        initTargetPicasso();

     /*   if(!listIsShowed){
            getPhotoList();
            listIsShowed = true;
        }*/

        return rootView;
    }

    private void initLoadMoreListener(){

                photoListAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                //add null , so the adapter will check view_type and show progress bar at bottom
                photoList.add(null);
                photoListAdapter.notifyItemInserted(photoList.size() - 1);


                photoList.remove(photoList.size() - 1);
                photoListAdapter.notifyItemRemoved(photoList.size());
                //add items one by one
                start = photoList.size();
                end = start + 21;

                progressBarPhotoList.setVisibility(View.VISIBLE);
                getPhotoList();


            }
        });
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

    public void getPhotoList (){

        try{
            swipeRefreshLayout.setRefreshing(true);




            String netType = getNetworkType(getContext());
            if(netType == null){
                Toast.makeText(getActivity(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);

            }
            else {
                try {

                    serverAPI.getPhotoNews(Integer.toString(start),Integer.toString(end)).enqueue(new Callback<ListNews>() {
                        @Override
                        public void onResponse(Call<ListNews> call, Response<ListNews> response) {

                            ListNews listNews = response.body();

                            try {
                                Collections.reverse(listNews.getNews());

                                photoList.addAll(listNews.getNews());
                                photoListAdapter.notifyDataSetChanged();
                                photoListAdapter.setLoaded();
                                swipeRefreshLayout.setRefreshing(false);
                                progressBarPhotoList.setVisibility(View.INVISIBLE);

                            }
                            catch(Exception e){
                                swipeRefreshLayout.setRefreshing(false);
                                progressBarPhotoList.setVisibility(View.INVISIBLE);
                                Toast.makeText(getActivity(), "Нет новостей на сервере!", Toast.LENGTH_LONG).show();

                            }

                            loadNextImage();

                            Log.i(LOG_TAG, "onResponse getListNews ");

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
        catch(Exception e){

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
        photoList.clear();

        start = 0;
        end = 21;
        getPhotoList();
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

        if(count_bitmap == photoList.size()) {
            count_bitmap = 0;
        }
        else{

            try {
                picasso.load(photoList.get(count_bitmap).getImgUrl())
                        .tag("load")
                        .resize(width, height).
                        into(loadtarget);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
              //  Picasso.with(getContext()).load(photoList.get(count_bitmap).getImgUrl()).resize(width, height).into(loadtarget);
            }
        }

    }

    void initTargetPicasso(){

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                if (photoList.size() > 0) {
                    photoList.get(count_bitmap).setBitmap(Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight()-18));
                    photoListAdapter.notifyDataSetChanged();

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

    public void onPause() {
        super.onPause();

        savedFragment.setNews(photoList);

    }


    public void pauseLoadImage(){

        try{

            picasso.pauseTag("load");
        }
        catch(Exception e){

        }
    }

    public void resumeLoadImage(){
        try{


            picasso.resumeTag("load");
        }
        catch(Exception e){

        }
    }

    public boolean isListIsShowed() {
        return listIsShowed;
    }

    public void setListIsShowed(boolean listIsShowed) {
        this.listIsShowed = listIsShowed;
    }

   /* @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(LOG_TAG, "setUserVisibleHint ");

        try {
            if (isVisibleToUser) {
                Log.d(LOG_TAG, "isVisibleToUser ");
                if (!listIsShowed) {
                    getPhotoList();
                    listIsShowed = true;
                }
                resumeLoadImage();
            }
            else {
                pauseLoadImage();

            }
        }
        catch (Exception e){
            Log.d(LOG_TAG, "error " + e.getMessage());

        }
    }*/
}