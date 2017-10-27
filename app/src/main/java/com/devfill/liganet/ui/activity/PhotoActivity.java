package com.devfill.liganet.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.ImageSliderAdapter;
import com.devfill.liganet.model.PhotoContent;
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

public class PhotoActivity extends AppCompatActivity{


    private static final String LOG_TAG = "PhotoActivityTag";

    private static ViewPager mPager;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtarget = null;

    private TextView title_photo_news,text_article_photo;
    private int count_bitmap = 0;
    private ImageSliderAdapter imageSliderAdapter;
    private List<String> imgUrls;
    private List<Bitmap> bitmapList = new ArrayList<>();
    private ProgressBar progressBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_activity);

        Log.d(LOG_TAG, "onCreate ArticleNewsFragment");

        title_photo_news  = (TextView) findViewById(R.id.title_photo_news);
        text_article_photo  = (TextView) findViewById(R.id.text_article_photo);
        progressBar = (ProgressBar) findViewById(R.id.progressPhoto);

        Typeface typefaceR = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(getAssets(),
                "fonts/UbuntuMono-B.ttf");
        title_photo_news.setTypeface(typefaceB);
        text_article_photo.setTypeface(typefaceR);


        imageSliderAdapter = new ImageSliderAdapter(getBaseContext(),bitmapList);
        mPager = (ViewPager) findViewById(R.id.pagerArticles);
        mPager.setAdapter(imageSliderAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout_photo_slider);   //индикатор страниц(кружочки)
        tabLayout.setupWithViewPager(mPager, true);

        title_photo_news.setVisibility(View.INVISIBLE);
        text_article_photo.setVisibility(View.INVISIBLE);
        mPager.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);



        initRetrofit();

        initTargetPicasso();

        String linkHref = getIntent().getStringExtra("linkHref");

        getPhotoContent(linkHref);
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

    private void getPhotoContent (String linkHref){
        String netType = getNetworkType(getBaseContext());
        if(netType == null){
            Toast.makeText(getBaseContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getPhotoContent(linkHref).enqueue(new Callback<PhotoContent>() {
                    @Override
                    public void onResponse(Call<PhotoContent> call, Response<PhotoContent> response) {

                        PhotoContent photoContent = response.body();


                        try {

                            Log.i(LOG_TAG, "Annotation  " +  photoContent.getData().getAnnotation());
                            Log.i(LOG_TAG, "Text  " +  photoContent.getData().getText());

                            for(int i = 0; i <   photoContent.getUrls().size(); i ++){

                                Log.i(LOG_TAG, "url  " + photoContent.getUrls().get(i));
                            }

                            title_photo_news.setVisibility(View.VISIBLE);
                            text_article_photo.setVisibility(View.VISIBLE);
                            mPager.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);

                            title_photo_news.setText(Html.fromHtml(photoContent.getData().getAnnotation()));
                            text_article_photo.setText(Html.fromHtml(photoContent.getData().getText()));
                            imgUrls = photoContent.getUrls();
                        }
                        catch(Exception e){

                            Toast.makeText(getBaseContext(), "Не удалось распознать фото!" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        loadNextImage();

                        Log.i(LOG_TAG, "onResponse getNewsContent ");

                    }

                    @Override
                    public void onFailure(Call<PhotoContent> call, Throwable t) {


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

                bitmapList.add(Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight()-18)); //обрежем сколько нужно нам пикселей
                imageSliderAdapter.notifyDataSetChanged();

                count_bitmap++;
                loadNextImage();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

                Log.d(LOG_TAG, "onBitmapFailed  ");
                count_bitmap++;
                loadNextImage();
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }

    private void loadNextImage() {

        if (count_bitmap == imgUrls.size()) {
            count_bitmap = 0;
        } else {

            try {
                Picasso.with(getBaseContext()).load(imgUrls.get(count_bitmap)).into(loadtarget);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
                Picasso.with(getBaseContext()).load(imgUrls.get(count_bitmap)).into(loadtarget);

            }

        }
    }

}