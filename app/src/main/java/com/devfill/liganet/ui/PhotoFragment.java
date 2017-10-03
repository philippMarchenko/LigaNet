package com.devfill.liganet.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.devfill.liganet.R;
import com.devfill.liganet.adapter.ImageSliderAdapter;
import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.model.PhotoContent;
import com.devfill.liganet.network.ServerAPI;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import me.relex.circleindicator.CircleIndicator;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PhotoFragment extends android.support.v4.app.Fragment{


    private static final String LOG_TAG = "PhotoFragmentTag";

    private static ViewPager mPager;
    private static int currentPage = 0;
    private static final Integer[] XMEN= {R.drawable.ic_assignment_black_48dp,R.drawable.ic_videocam_black_48dp,R.drawable.ic_photo_camera_black_48dp};
    private ArrayList<Integer> XMENArray = new ArrayList<Integer>();

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtarget = null;

    TextView title_photo_news,text_article_photo;
    private int count_bitmap = 0;
    ImageSliderAdapter imageSliderAdapter;
    List<String> imgUrls;
    List<Bitmap> bitmapList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_photo, container, false);

        title_photo_news  = (TextView) rootView.findViewById(R.id.title_photo_news);
        text_article_photo  = (TextView) rootView.findViewById(R.id.text_article_photo);


        imageSliderAdapter = new ImageSliderAdapter(getContext(),bitmapList);
        mPager = (ViewPager) rootView.findViewById(R.id.pager);
        mPager.setAdapter(imageSliderAdapter);
        CircleIndicator indicator = (CircleIndicator) rootView.findViewById(R.id.indicator);
        indicator.setViewPager(mPager);

        initRetrofit();

        initTargetPicasso();

        getPhotoContent("http://news.liga.net/photo/incident/14831006-na_skladakh_v_kalinovke_prekratilis_pozhary_i_vzryvy_spasateli.htm");

        return rootView;
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
        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
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

                            title_photo_news.setText(Html.fromHtml(photoContent.getData().getAnnotation()));
                            text_article_photo.setText(Html.fromHtml(photoContent.getData().getText()));
                            imgUrls = photoContent.getUrls();
                        }
                        catch(Exception e){

                            Toast.makeText(getContext(), "Не удалось распознать фото!" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        loadNextImage();

                        Log.i(LOG_TAG, "onResponse getNewsContent ");

                    }

                    @Override
                    public void onFailure(Call<PhotoContent> call, Throwable t) {


                        Toast.makeText(getContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

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

                bitmapList.add(bitmap);
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
                Picasso.with(getContext()).load(imgUrls.get(count_bitmap)).into(loadtarget);
            } catch (Exception e) {
                Log.d(LOG_TAG, "Error load image " + e.getMessage());

                count_bitmap++;
                Picasso.with(getContext()).load(imgUrls.get(count_bitmap)).into(loadtarget);

            }

        }
    }
}