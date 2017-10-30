package com.devfill.liganet.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.android.youtube.player.internal.c;
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

public class ArticleNewsAdapter extends PagerAdapter {

    private LayoutInflater inflater;
    private Context mContext;

    private List<NewsContent> list = new ArrayList<>();

    private Html.ImageGetter igLoader;

    private static final String LOG_TAG = "ArticleNewsAdapterTag";

    private View imageLayout;

    public ArticleNewsAdapter(Context context,List<NewsContent> list) {
        this.mContext = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object instantiateItem(ViewGroup view, int position) {

        Log.d(LOG_TAG, "position " + position);
        Log.d(LOG_TAG, "text " + list.get(position).getData().getText());
        Log.i(LOG_TAG, "check video " + list.get(position).getData().isVideo());

        if(list.get(position).getData().isPhoto()){

            initPhotoNews(view,position);
            Log.d(LOG_TAG, "Инициализация фото ");
        }
        else if(list.get(position).getData().isVideo()){

            initVideoNews(view,position);
            Log.d(LOG_TAG, "Инициализация видео ");

        }
        else{

            initArticleNews(view,position);
            Log.d(LOG_TAG, "Инициализация статьи ");

        }

        view.addView(imageLayout, 0);
        return imageLayout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        boolean b = view.equals(object);

        return b;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void initIloader(final int position){


        igLoader = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {

                Drawable drawable = null;

                for(int i = 0 ; i < list.get(position).getUrls().size(); i++){

                    if(source.equals(list.get(position).getUrls().get(i))){
                        drawable = ArticleNewsActivity.drawableHashMap.get(list.get(position).getUrls().get(i));

                    }
                }

                Log.d(LOG_TAG, "getDrawable  " + drawable);

                return drawable;
            }
        };

    }

    private void initArticleNews(ViewGroup view,int position){

        imageLayout = inflater.inflate(R.layout.activity_article_news, view, false);


        initIloader(position);

        TextView dateAtricle = (TextView) imageLayout.findViewById(R.id.dateAtricle);
        TextView anotation = (TextView) imageLayout.findViewById(R.id.anotation);
        TextView textArticle = (TextView) imageLayout.findViewById(R.id.textArticle);
        ImageView backdrop = (ImageView) imageLayout.findViewById(R.id.backdrop);
        ProgressBar progressArticle = (ProgressBar) imageLayout.findViewById(R.id.progressArticle);


        Typeface typefaceRI = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-RI.ttf");
        Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-B.ttf");
        anotation.setTypeface(typefaceB);
        textArticle.setTypeface(typefaceR);
        dateAtricle.setTypeface(typefaceRI);



        if(list.get(position).getData().getDate().length() > 0){

            progressArticle.setVisibility(View.INVISIBLE);
            dateAtricle.setVisibility(View.VISIBLE);
            anotation.setVisibility(View.VISIBLE);
            textArticle.setVisibility(View.VISIBLE);
        }
        else {

            dateAtricle.setVisibility(View.INVISIBLE);
            anotation.setVisibility(View.INVISIBLE);
            textArticle.setVisibility(View.INVISIBLE);
            progressArticle.setVisibility(View.VISIBLE);
        }



        dateAtricle.setText(list.get(position).getData().getDate());
        anotation.setText(Html.fromHtml(list.get(position).getData().getAnnotation()));
        textArticle.setText(Html.fromHtml(list.get(position).getData().getText(), igLoader, null));

        backdrop.setImageBitmap(list.get(position).getData().getBitmaToolbar());
    }

    private void initVideoNews(ViewGroup view, final int position){

        imageLayout = inflater.inflate(R.layout.video_activity, view, false);

        YouTubePlayerView youTubePlayerView = (YouTubePlayerView) imageLayout.findViewById(R.id.youtube);
        TextView text_video = (TextView) imageLayout.findViewById(R.id.text_video);
        TextView annotation_video = (TextView) imageLayout.findViewById(R.id.annotation_video);
        ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progressVideo);

        Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-B.ttf");
        annotation_video.setTypeface(typefaceB);
        text_video.setTypeface(typefaceR);

        if(list.get(position).getData().getText().length() > 0) {

            text_video.setVisibility(View.VISIBLE);
            annotation_video.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            youTubePlayerView.setVisibility(View.VISIBLE);
        }
        else{

            text_video.setVisibility(View.INVISIBLE);
            annotation_video.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            youTubePlayerView.setVisibility(View.INVISIBLE);
        }


        annotation_video.setText(Html.fromHtml(list.get(position).getData().getAnnotation()));
        text_video.setText(Html.fromHtml(list.get(position).getData().getText(), igLoader, null));


        if(youTubePlayerView != null){
            youTubePlayerView.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM",new YouTubePlayer.OnInitializedListener(){


                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        player.play();
                        player.cueVideo(list.get(position).getData().getVideoUrl());
                    }
                }


                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                    String errorMessage = error.toString();
                    Toast.makeText(mContext,errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("errorMessage:", errorMessage);
                }
            });

        }
        else{
            Toast.makeText(mContext, "Нет плеера", Toast.LENGTH_LONG).show();
        }

    }

    private void initPhotoNews(ViewGroup view,int position){

        imageLayout = inflater.inflate(R.layout.photo_activity, view, false);


        TextView title_photo_news  = (TextView) imageLayout.findViewById(R.id.title_photo_news);
        TextView text_article_photo  = (TextView) imageLayout.findViewById(R.id.text_article_photo);
        ProgressBar progressBar = (ProgressBar) imageLayout.findViewById(R.id.progressPhoto);

        Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(mContext.getAssets(),
                "fonts/UbuntuMono-B.ttf");
        title_photo_news.setTypeface(typefaceB);
        text_article_photo.setTypeface(typefaceR);


        ImageSliderAdapter imageSliderAdapter = new ImageSliderAdapter(mContext,ArticleNewsActivity.bitmapList);
        ViewPager mPager = (ViewPager) imageLayout.findViewById(R.id.pagerArticles);
        mPager.setAdapter(imageSliderAdapter);

        TabLayout tabLayout = (TabLayout) imageLayout.findViewById(R.id.tab_layout_photo_slider);   //индикатор страниц(кружочки)
        tabLayout.setupWithViewPager(mPager, true);

        if(list.get(position).getData().getText().length() > 0) {

            title_photo_news.setVisibility(View.VISIBLE);
            text_article_photo.setVisibility(View.VISIBLE);
            mPager.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }
        else{

            title_photo_news.setVisibility(View.INVISIBLE);
            text_article_photo.setVisibility(View.INVISIBLE);
            mPager.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        title_photo_news.setText(Html.fromHtml(list.get(position).getData().getAnnotation()));
        text_article_photo.setText(Html.fromHtml(list.get(position).getData().getText(), igLoader, null));

    }

}