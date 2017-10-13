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
import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.NewsContent;
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

    private TextView dateAtricle,anotation,textArticle;
    private ImageView backdrop;
    private ProgressBar progressArticle;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtarget = null;

    Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    List<String> imageUrls = new ArrayList<>();
    NewsContent newsContent;

    static final Map<String, WeakReference<Drawable>> mDrawableCache = Collections.synchronizedMap(new WeakHashMap<String, WeakReference<Drawable>>());

    private int count_bitmap = 0;

    Html.ImageGetter igLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_news);

        Log.d(LOG_TAG, "onCreate ArticleNewsFragment");

        dateAtricle = (TextView) findViewById(R.id.dateAtricle);
        anotation = (TextView) findViewById(R.id.anotation);
        textArticle = (TextView) findViewById(R.id.textArticle);
        backdrop = (ImageView) findViewById(R.id.backdrop);
        progressArticle = (ProgressBar) findViewById(R.id.progressArticle);

        Typeface typefaceRI = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuMono-RI.ttf");
        Typeface typefaceR = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuMono-B.ttf");
        anotation.setTypeface(typefaceB);
        textArticle.setTypeface(typefaceR);
        dateAtricle.setTypeface(typefaceRI);


        dateAtricle.setVisibility(View.INVISIBLE);
        anotation.setVisibility(View.INVISIBLE);
        textArticle.setVisibility(View.INVISIBLE);
        progressArticle.setVisibility(View.VISIBLE);


        String linkHref = getIntent().getStringExtra("linkHref");           //получим ссылку на саму статью
        String imgHref = getIntent().getStringExtra("imgHref");             //получим ссылку на картинку для тулбара

        Log.d(LOG_TAG, "imgHref " + imgHref);
        Log.d(LOG_TAG, "linkHref " + linkHref);

        initRetrofit();                                                     //настроем ретрофит
        initTargetPicasso();                                                //настроим пикассо
        initImageLoader ();                                                 //настроим загружчик картинки для HTML
        getNewsContent(linkHref);                                           //запрос к серверу по контент статьи

        try {
            Picasso.with(getBaseContext()).load(imgHref).into(backdrop);    //загружаем картинку в тулбар
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error load image " + e.getMessage());
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

    private void initImageLoader (){

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

    private void getNewsContent (String linkHref){
        String netType = getNetworkType(getBaseContext());
        if(netType == null){
            Toast.makeText(getBaseContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getNewsContent(linkHref).enqueue(new Callback<NewsContent>() {
                    @Override
                    public void onResponse(Call<NewsContent> call, Response<NewsContent> response) {

                        newsContent = response.body();
                        progressArticle.setVisibility(View.INVISIBLE);
                        dateAtricle.setVisibility(View.VISIBLE);
                        anotation.setVisibility(View.VISIBLE);
                        textArticle.setVisibility(View.VISIBLE);

                        try {

                            dateAtricle.setText(newsContent.getData().getDate());
                            anotation.setText(Html.fromHtml(newsContent.getData().getAnnotation()));
                            // textArticle.setText(Html.fromHtml(newsContent.getData().getText()));

                            imageUrls = newsContent.getUrls();

                            if (newsContent.getUrls().size() > 0) {
                                for (int i = 0; i < newsContent.getUrls().size(); i++) {

                                    Log.i(LOG_TAG, "urlPhoto " + newsContent.getUrls().get(i));

                                    try {
                                        int height = 90;
                                        int width = 120;
                                        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
                                        height = (int) (200 * scale + 0.5f);
                                        width = (int) (380 * scale + 0.5f);


                                        Picasso.with(getBaseContext()).load(newsContent.getUrls().get(i)).resize(width,height).into(loadtarget);
                                    } catch (Exception e) {

                                        Log.d(LOG_TAG, "Error load image " + e.getMessage());
                                    }
                                }
                              }
                            else{

                                textArticle.setText(Html.fromHtml(newsContent.getData().getText()));

                                }
                            }
                            catch(Exception e){

                                Toast.makeText(getBaseContext(), "Не удалось распознать статью!", Toast.LENGTH_LONG).show();

                            }




                        Log.i(LOG_TAG, "onResponse getNewsContent ");

                    }

                    @Override
                    public void onFailure(Call<NewsContent> call, Throwable t) {

                        progressArticle.setVisibility(View.INVISIBLE);
                        dateAtricle.setVisibility(View.VISIBLE);
                        anotation.setVisibility(View.VISIBLE);
                        textArticle.setVisibility(View.VISIBLE);
                        Toast.makeText(getBaseContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
    }

    void initTargetPicasso(){

        loadtarget = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                if (imageUrls.size() > 0) {

                    Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                            drawable.getIntrinsicHeight());
                    drawableHashMap.put(imageUrls.get(count_bitmap),drawable);

                    count_bitmap++;

                    Log.d(LOG_TAG, "set drawable  ");
                }
                //И сразу же используем его
                textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }
}