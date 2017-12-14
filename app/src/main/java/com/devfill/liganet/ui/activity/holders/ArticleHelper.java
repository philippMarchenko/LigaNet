package com.devfill.liganet.ui.activity.holders;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;

import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.network.ServerAPI;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleHelper {

    private static final String LOG_TAG = "ArticleHelper";

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    public Html.ImageGetter igLoader;
    private Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    private List<String> imageUrls = new ArrayList<>();

    private Target toolbarImage = null;

    Context context;

    public ArticleHelper(Context context) {

        this.context = context;

        initRetrofit();
        initIloader();

        Log.i(LOG_TAG, "ArticleHelper  " );

    }


    public Observable getToolbarImage(final String url,final ImageView image) {

        final float scale = context.getResources().getDisplayMetrics().density;
        int height = (int) (220 * scale + 0.5f);
        int width = (int) (295 * scale + 0.5f);
        Log.i(LOG_TAG, "getToolbarImage url " + url);
        Picasso.with(context).load(url).resize(width,height-20).into(image);    //загружаем картинку


        Observable imageObservable = Observable.create(new ObservableOnSubscribe<Bitmap>() {
            @Override
            public void subscribe(final ObservableEmitter<Bitmap> emitter) throws Exception {


                toolbarImage = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        emitter.onNext(bitmap);
                        Log.i(LOG_TAG, "onBitmapLoaded");

                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        Log.i(LOG_TAG, "onBitmapFailed");

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        Log.i(LOG_TAG, "onPrepareLoad");

                    }

                };


            }
        });

        return imageObservable;
    }

    public Observable<NewsContent> getNewsContentRx(String url) {
        return serverAPI.getNewsContentRx(url);
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();


        serverAPI = retrofit.create(ServerAPI.class);
    }

    private void initIloader(){


        igLoader = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {

                Drawable drawable = null;

                for(int i = 0 ; i < imageUrls.size(); i++){

                    if(source.equals(imageUrls.get(i))){
                        drawable = drawableHashMap.get(imageUrls.get(i));

                    }
                }

                Log.d(LOG_TAG, "getDrawable  " + drawable);

                return drawable;
            }
        };

    }

}
