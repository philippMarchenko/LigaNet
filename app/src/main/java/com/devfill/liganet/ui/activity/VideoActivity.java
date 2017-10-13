package com.devfill.liganet.ui.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.LevelListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.ContactsContract;
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
import com.devfill.liganet.model.VideoContent;
import com.devfill.liganet.network.GetDataNews;
import com.devfill.liganet.network.ServerAPI;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URL;
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

public class VideoActivity extends YouTubeBaseActivity  {


    private static final String LOG_TAG = "VideoActivityTag";

    private TextView text_video,annotation_video;
    private ProgressBar progressBar;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private YouTubePlayer mPlayer;
    private YouTubePlayerView youTubePlayerView;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    Target loadtarget = null;

    Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    List<String> imageUrls = new ArrayList<>();

    private int count_bitmap = 0;

    VideoContent videoContent;
    Html.ImageGetter igLoader;

    String videoUrl,linkHref;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_activity);

        Log.d(LOG_TAG, "onCreate ArticleNewsFragment");


        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube);
        text_video = (TextView) findViewById(R.id.text_video);
        annotation_video = (TextView) findViewById(R.id.annotation_video);
        progressBar = (ProgressBar) findViewById(R.id.progressVideo);

        text_video.setVisibility(View.INVISIBLE);
        annotation_video.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        youTubePlayerView.setVisibility(View.INVISIBLE);


        linkHref = getIntent().getStringExtra("linkHref");
        Log.d(LOG_TAG, "linkHref " + linkHref);

        initRetrofit();
        initTargetPicasso();

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




        if(youTubePlayerView != null){
            youTubePlayerView.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM",new YouTubePlayer.OnInitializedListener(){


                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
                    if (!wasRestored) {
                        player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                        player.play();
                        mPlayer = player;
                        getVideoContent(linkHref);  // когда инициализировался плеерб отправляем запрос к серверу на парсинг страниці с виде
                    }
                }


                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
                    // YouTube error
                    String errorMessage = error.toString();
                    Toast.makeText(getBaseContext(),errorMessage, Toast.LENGTH_LONG).show();
                    Log.d("errorMessage:", errorMessage);
                }
            });

        }
        else{
            Toast.makeText(this, "NULL", Toast.LENGTH_LONG).show();
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

        String netType = getNetworkType(this);
        if(netType == null){
            Toast.makeText(this, "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getVideoContent(linkHref).enqueue(new Callback<VideoContent>() {
                    @Override
                    public void onResponse(Call<VideoContent> call, Response<VideoContent> response) {

                         videoContent = response.body();

                        try {

                            text_video.setVisibility(View.VISIBLE);
                            annotation_video.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            youTubePlayerView.setVisibility(View.VISIBLE);

                           // text_video.setText(Html.fromHtml(videoContent.getData().getText()));
                            annotation_video.setText(Html.fromHtml(videoContent.getData().getAnnotation()));
                            videoUrl = videoContent.getData().getVideo_url();

                            imageUrls = videoContent.getUrls();

                            if (videoContent.getUrls().size() > 0) {

                                for (int i = 0; i < videoContent.getUrls().size(); i++) {

                                    Log.i(LOG_TAG, "urlPhoto " + videoContent.getUrls().get(i));

                                    try {
                                        int height = 90;
                                        int width = 120;
                                        final float scale = getBaseContext().getResources().getDisplayMetrics().density;
                                        height = (int) (200 * scale + 0.5f);
                                        width = (int) (380 * scale + 0.5f);

                                        Picasso.with(getBaseContext()).load(videoContent.getUrls().get(i)).into(loadtarget);
                                    } catch (Exception e) {

                                        Log.d(LOG_TAG, "Error load image " + e.getMessage());
                                    }
                                }
                            }
                            else{

                                text_video.setText(Html.fromHtml(videoContent.getData().getText()));
                            }

                            youTubePlayerView.setVisibility(View.VISIBLE);


                            mPlayer.cueVideo(videoUrl);

                        }
                        catch(Exception e){

                            Toast.makeText(getBaseContext(), "Не удалось распознать видео статью!" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        Log.i(LOG_TAG, "onResponse getVideoContent ");

                    }

                    @Override
                    public void onFailure(Call<VideoContent> call, Throwable t) {


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
                text_video.setText(Html.fromHtml(videoContent.getData().getText(), igLoader, null));
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
}