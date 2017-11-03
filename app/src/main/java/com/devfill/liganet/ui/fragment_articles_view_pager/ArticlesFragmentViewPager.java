package com.devfill.liganet.ui.fragment_articles_view_pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.network.ServerAPI;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesFragmentViewPager extends android.support.v4.app.Fragment{


    private static final String LOG_TAG = "ArticlesViewPager";

    private TextView dateAtricle;
    private TextView anotation;
    private TextView textArticle;;
    private ImageView backdrop;
    private ProgressBar progressArticle;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtargetArrayImage = null;

    private Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    private List<String> imageUrls = new ArrayList<>();

    private int count_bitmap = 0;
    private NewsContent newsContent;
    private Html.ImageGetter igLoader;

    private String link;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_article_news, container, false);

        Log.d(LOG_TAG,"onCreateView ");

        dateAtricle = (TextView) rootView.findViewById(R.id.dateAtricle);
        anotation = (TextView) rootView.findViewById(R.id.anotation);
        textArticle = (TextView) rootView.findViewById(R.id.textArticle);
        backdrop = (ImageView) rootView.findViewById(R.id.backdrop);
        progressArticle = (ProgressBar) rootView.findViewById(R.id.progressArticle);

        Typeface typefaceRI = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-RI.ttf");
        Typeface typefaceR = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-B.ttf");
        anotation.setTypeface(typefaceB);
        textArticle.setTypeface(typefaceR);
        dateAtricle.setTypeface(typefaceRI);

        dateAtricle.setVisibility(View.INVISIBLE);
        anotation.setVisibility(View.INVISIBLE);
        textArticle.setVisibility(View.INVISIBLE);
        progressArticle.setVisibility(View.VISIBLE);

        link = getArguments().getString("linkHref");

        initRetrofit();
        initTargetPicassoArrayImage();
        initIloader();


        return rootView;
    }

    private void getNewsContent (final String linkHref){

      //  Log.i(LOG_TAG, "getNewsContent " + linkHref);

        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getNewsContent(linkHref).enqueue(new Callback<NewsContent>() {
                    @Override
                    public void onResponse(Call<NewsContent> call, Response<NewsContent> response) {

                        //Log.i(LOG_TAG, "onResponse getNewsContent ");

                        newsContent = response.body();

                        try {

                            imageUrls = newsContent.getUrls();

                            if (newsContent.getUrls().size() > 0) {

                                  dateAtricle.setText(newsContent.getData().getDate());
                                  anotation.setText(Html.fromHtml(newsContent.getData().getAnnotation()));
                                  loadImageInToolbar(newsContent.getData().getImageUrl());
                                  loadNextImage();
                            }
                            else{

                                dateAtricle.setText(newsContent.getData().getDate());
                                anotation.setText(Html.fromHtml(newsContent.getData().getAnnotation()));
                                textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));
                                loadImageInToolbar(newsContent.getData().getImageUrl());
                            }

                            progressArticle.setVisibility(View.INVISIBLE);
                            dateAtricle.setVisibility(View.VISIBLE);
                            anotation.setVisibility(View.VISIBLE);
                            textArticle.setVisibility(View.VISIBLE);

                        }
                        catch(Exception e){

                            Toast.makeText(getContext(), "Не удалось распознать статью!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<NewsContent> call, Throwable t) {

                              progressArticle.setVisibility(View.INVISIBLE);


//                        Toast.makeText(getContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
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

    private void loadNextImage(){
        Log.i(LOG_TAG, "loadNextImage  count_bitmap " + count_bitmap);

        if(count_bitmap == imageUrls.size()) {
            Log.i(LOG_TAG, "Загрузили все картинки ");
            count_bitmap = 0;
        }
        else{
            try {
                Picasso.with(getContext()).load(imageUrls.get(count_bitmap)).into(loadtargetArrayImage);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;
            }
        }

    }

    private void initTargetPicassoArrayImage(){

        loadtargetArrayImage = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                //   Log.d(LOG_TAG, "onBitmapLoaded  ");

                try{

                    if (imageUrls.size() > 0) { //это качаются картинки в саму статью

                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        drawableHashMap.put(imageUrls.get(count_bitmap),drawable);

                        count_bitmap++;
                        loadNextImage();

                        textArticle.setVisibility(View.VISIBLE);
                        anotation.setVisibility(View.VISIBLE);
                        progressArticle.setVisibility(View.INVISIBLE);

                        textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));
                    }
                }
                catch (IllegalStateException e){

                       Log.d(LOG_TAG, "ошибка загрузки картинки   " + e.getMessage());
                }



            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }

    private void loadImageInToolbar(String url){

        try {

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int height = (int) (220 * scale + 0.5f);
            int width = (int) (295 * scale + 0.5f);

            Picasso.with(getContext()).load(url).resize(width,height-20).into(backdrop);    //загружаем картинку в тулбар
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error load image " + e.getMessage());
        }
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
    public void onResume() {
        super.onResume();

        getNewsContent(link);

        Log.i(LOG_TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        onStop();
        Log.i(LOG_TAG, " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, " onDestroy");
    }

}