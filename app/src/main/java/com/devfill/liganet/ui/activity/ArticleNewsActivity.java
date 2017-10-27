package com.devfill.liganet.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.ArticleNewsAdapter;
import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.model.VideoContent;
import com.devfill.liganet.network.GetDataNews;
import com.devfill.liganet.network.ServerAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticleNewsActivity extends AppCompatActivity {


    private static final String LOG_TAG = "ArticleNewsActivityTag";

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtargetArrayImage = null;
    Target loadtargetToolbar = null;


    public static Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    List<String> imageUrls = new ArrayList<>();
    NewsContent newsContent;

    private int positionPage = 0;       //позиция текущей страницы
    private int positionArticle = 0;    //позиция статьи по которой нажали

    private int count_bitmap = 0;

    private List<NewsContent> listContent = new ArrayList<>();
    ArrayList<String> myList;

    ArticleNewsAdapter articleNewsAdapter;
    ViewPager mPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_news_pager);


        myList = (ArrayList<String>) getIntent().getSerializableExtra("newsList");



        
        for(int i = 0; i < myList.size(); i++){

            NewsContent newsContent = new NewsContent();
            newsContent.setData(new NewsContent.Data("","",""));
            listContent.add(newsContent);

            Log.d(LOG_TAG, myList.get(i));
        }

        mPager = (ViewPager) findViewById(R.id.pagerArticles);
        articleNewsAdapter = new ArticleNewsAdapter(getBaseContext(),listContent);
        mPager.setAdapter(articleNewsAdapter);

        positionArticle = getIntent().getIntExtra("position",0);

        initPagerListener();
        initRetrofit();                                                    //настроем ретрофит
        initTargetPicassoArrayImage();                                               //настроим пикассо
        initTargetPicassoToolbar();                                               //настроим пикассо
        getNewsContent(myList.get(positionArticle));                             //запрос к серверу по контент статьи

    }

    private void initPagerListener(){

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {


                positionPage = position;

                getNewsContent(myList.get(positionArticle + positionPage));               //запрос к серверу по контент статьи

                Log.d(LOG_TAG, "onPageSelected " + position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

    private void loadImageInToolbar(String url){

        try {

            final float scale = getBaseContext().getResources().getDisplayMetrics().density;
            int height = (int) (220 * scale + 0.5f);
            int width = (int) (295 * scale + 0.5f);

            Picasso.with(getBaseContext()).load(url).resize(width,height-20).into(loadtargetToolbar);    //загружаем картинку в тулбар
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error load image " + e.getMessage());
        }
    }

    private void getNewsContent (String linkHref){

        Log.i(LOG_TAG, "getNewsContent " + linkHref);


        String netType = getNetworkType(this);
        if(netType == null){
            Toast.makeText(this, "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getNewsContent(linkHref).enqueue(new Callback<NewsContent>() {
                    @Override
                    public void onResponse(Call<NewsContent> call, Response<NewsContent> response) {

                        Log.i(LOG_TAG, "onResponse getNewsContent ");

                        NewsContent newsContent = response.body();

                        try {

                            imageUrls = newsContent.getUrls();

                            if (newsContent.getUrls().size() > 0) {

                                listContent.set(positionPage,newsContent);
                                loadImageInToolbar(newsContent.getData().getImageUrl());
                                loadNextImage();
                            }
                            else{

                                listContent.set(positionPage,newsContent);
                                loadImageInToolbar(newsContent.getData().getImageUrl());

                                articleNewsAdapter.notifyDataSetChanged();
                            }
                        }
                        catch(Exception e){

                            Toast.makeText(getBaseContext(), "Не удалось распознать статью!", Toast.LENGTH_LONG).show();

                        }

                    }

                    @Override
                    public void onFailure(Call<NewsContent> call, Throwable t) {


                        Toast.makeText(getBaseContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
    }

    private void loadNextImage(){
        Log.i(LOG_TAG, "loadNextImage  count_bitmap " + count_bitmap);

        if(count_bitmap == imageUrls.size()) {
            Log.i(LOG_TAG, "Загрузили все картинки ");

            count_bitmap = 0;

            //listContent.get(positionPage).getData().setDrawableHashMap(drawableHashMap);

            articleNewsAdapter.notifyDataSetChanged();
        }
        else{

            try {
                Picasso.with(getBaseContext()).load(imageUrls.get(count_bitmap)).into(loadtargetArrayImage);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;
              //  Picasso.with(getBaseContext()).load(imageUrls.get(count_bitmap)).into(loadtarget);

            }
        }

    }

    void initTargetPicassoArrayImage(){

        loadtargetArrayImage = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                if (imageUrls.size() > 0) { //это качаются картинки в саму статью

                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    drawableHashMap.put(imageUrls.get(count_bitmap),drawable);

                    count_bitmap++;
                    loadNextImage();

                 /*   progressArticle.setVisibility(View.INVISIBLE);
                    dateAtricle.setVisibility(View.VISIBLE);
                    anotation.setVisibility(View.VISIBLE);
                    textArticle.setVisibility(View.VISIBLE);*/

                    //textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));
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

    void initTargetPicassoToolbar(){

        loadtargetToolbar = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  loadtargetToolbar");

                    listContent.get(positionPage).getData().setBitmapToolbar(bitmap);
                    articleNewsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }

    boolean checkForWord(String line, String word){
        return line.contains(word);
    }
}