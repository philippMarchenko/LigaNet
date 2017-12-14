package com.devfill.liganet.ui.activity.holders;


import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.model.VideoContent;
import com.devfill.liganet.network.ServerAPI;
import com.devfill.liganet.ui.fragment_video.VideoFragment;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
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

public class VideoViewHolder extends RecyclerView.ViewHolder{

    private static final String LOG_TAG = "VideoViewHolder";

    private TextView text_video,annotation_video,date;
    private ProgressBar progressBar;
    private YouTubePlayer mPlayer;

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    private VideoContent videoContent;
    private Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    private List<String> imageUrls = new ArrayList<>();

    private int count_bitmap = 0;
    private Html.ImageGetter igLoader;
    private Target loadtarget = null;

    private YouTubePlayerView youTubeView;

    String videoUrl;

    Context context;
    Activity activity;

    public VideoViewHolder(View itemView, Context context, Activity activity) {
        super(itemView);

        this.activity = activity;
        this.context = context;
        youTubeView = (YouTubePlayerView) itemView.findViewById(R.id.youtube_view);
        text_video = (TextView) itemView.findViewById(R.id.text_video);
        annotation_video = (TextView) itemView.findViewById(R.id.annotation_video);
        progressBar = (ProgressBar) itemView.findViewById(R.id.progressVideo);
        date = (TextView) itemView.findViewById(R.id.dateAtricle);
        Typeface typefaceR = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(context.getAssets(),
                "fonts/UbuntuMono-B.ttf");

        annotation_video.setTypeface(typefaceB);
        text_video.setTypeface(typefaceR);

        text_video.setVisibility(View.INVISIBLE);
        annotation_video.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        youTubeView.setVisibility(View.INVISIBLE);
        date.setVisibility(View.INVISIBLE);

        initRetrofit();
        initLoaderImage();
        initTargetPicasso();
    }

    private void initLoaderImage(){

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

    public void initYouTube(final String linkHref){
        Log.i(LOG_TAG, "initYouTube ");

        if(youTubeView != null){
            youTubeView.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM",new YouTubePlayer.OnInitializedListener(){

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    Log.i(LOG_TAG, "onInitializationSuccess");

                  //  mWasRestored = wasRestored;
                    mPlayer = player;
                    mPlayer.setFullscreen(false);
                    mPlayer.setShowFullscreenButton(true);



                    getVideoContent(linkHref);

                }


                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                    String errorMessage = error.toString();

                    if(error == YouTubeInitializationResult.SERVICE_MISSING){

                        Toast.makeText(activity, " Вам нужно установить приложение YouTube", Toast.LENGTH_LONG).show();
                    }
                    else{

                        Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
        else{
            //  Toast.makeText(getActivity(), "NULL", Toast.LENGTH_LONG).show();
            Log.i(LOG_TAG, "NULL");

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

    private void getVideoContent (String linkHref){

        String netType = getNetworkType(context);
        if(netType == null){
            Toast.makeText(context, "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getVideoContent(linkHref).enqueue(new Callback<VideoContent>() {
                    @Override
                    public void onResponse(Call<VideoContent> call, Response<VideoContent> response) {

                        Log.i(LOG_TAG, "onResponse getVideoContent ");

                        videoContent = response.body();

                        try {

                            annotation_video.setText(Html.fromHtml(videoContent.getData().getAnnotation()));
                            videoUrl = videoContent.getData().getVideo_url();
                            imageUrls = videoContent.getUrls();
                            date.setText(videoContent.getData().getDate());

                            if (videoContent.getUrls().size() > 0) {

                                Log.i(LOG_TAG, "videoContent size " + videoContent.getUrls().size());
                                loadNextImage();
                            }
                            else{
                                text_video.setVisibility(View.VISIBLE);
                                annotation_video.setVisibility(View.VISIBLE);
                                progressBar.setVisibility(View.INVISIBLE);
                                youTubeView.setVisibility(View.VISIBLE);
                                date.setVisibility(View.VISIBLE);

                                text_video.setText(Html.fromHtml(videoContent.getData().getText()));
                            }

                            mPlayer.cueVideo(videoUrl);
                        }
                        catch(Exception e){
                            Toast.makeText(activity, "Не удалось распознать видео статью!" + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                        Log.i(LOG_TAG, "onResponse getVideoContent ");
                    }
                    @Override
                    public void onFailure(Call<VideoContent> call, Throwable t) {
                        Toast.makeText(context, "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {
                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
    }

    private void loadNextImage(){

        if(count_bitmap == imageUrls.size()) {
            Log.i(LOG_TAG, "Загрузили все картинки ");
            count_bitmap = 0;
        }
        else{
            try {
                Picasso.with(context).load(imageUrls.get(count_bitmap)).into(loadtarget);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;
                Picasso.with(context).load(imageUrls.get(count_bitmap)).into(loadtarget);
            }
        }
    }

    private void initTargetPicasso(){

        loadtarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                Log.d(LOG_TAG, "onBitmapLoaded  ");

                try{
                    if (imageUrls.size() > 0) {

                        Drawable drawable = new BitmapDrawable(context.getResources(),Bitmap.createBitmap(bitmap, 0, 0,bitmap.getWidth(), bitmap.getHeight()-18)); //обрежем сколько нужно нам пикселей) //создали драврэбл из битмап
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        drawableHashMap.put(imageUrls.get(count_bitmap),drawable);  //ложим картинку с ключем адресом в хэшмап

                        count_bitmap++;
                        loadNextImage();

                        text_video.setVisibility(View.VISIBLE);
                        annotation_video.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        youTubeView.setVisibility(View.VISIBLE);
                        date.setVisibility(View.VISIBLE);

                        text_video.setText(Html.fromHtml(videoContent.getData().getText(), igLoader, null));
                    }
                }
                catch(Exception e){
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                count_bitmap++;
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
    }

    public void pauseVideo(){

        mPlayer.pause();

    }
}
