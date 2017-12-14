package com.devfill.liganet.ui.activity.holders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
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


public class PhotoViewHolder extends RecyclerView.ViewHolder{

    private static final String LOG_TAG = "PhotoFragmentTag";

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


    Context context;
    Activity activity;

    public PhotoViewHolder(View itemView,Context context, Activity activity) {
        super(itemView);

        this.activity = activity;
        this.context = context;

        title_photo_news  = (TextView) itemView.findViewById(R.id.title_photo_news);
        text_article_photo  = (TextView) itemView.findViewById(R.id.text_article_photo);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressPhoto);

        Typeface typefaceR = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuMono-B.ttf");
        title_photo_news.setTypeface(typefaceB);
        text_article_photo.setTypeface(typefaceR);

        imageSliderAdapter = new ImageSliderAdapter(context,bitmapList);
        mPager = (ViewPager) itemView.findViewById(R.id.pagerArticles);
        mPager.setAdapter(imageSliderAdapter);

        TabLayout tabLayout = (TabLayout) itemView.findViewById(R.id.tab_layout_photo_slider);
        tabLayout.setupWithViewPager(mPager, true);

        title_photo_news.setVisibility(View.INVISIBLE);
        text_article_photo.setVisibility(View.INVISIBLE);
        mPager.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        initRetrofit();

        initTargetPicasso();

        mPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ViewParent parent = v.getParent();
                parent.requestDisallowInterceptTouchEvent(true);

                return false;
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

    public void getPhotoContent (String linkHref){

        String netType = getNetworkType(context);
        if(netType == null){
            Toast.makeText(context, "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
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

                            Toast.makeText(context, "Не удалось распознать фото!" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        loadNextImage();

                        Log.i(LOG_TAG, "onResponse getNewsContent ");

                    }

                    @Override
                    public void onFailure(Call<PhotoContent> call, Throwable t) {


                        Toast.makeText(context, "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

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
                Picasso.with(context).load(imgUrls.get(count_bitmap)).into(loadtarget);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
                Picasso.with(context).load(imgUrls.get(count_bitmap)).into(loadtarget);

            }

        }
    }
}
