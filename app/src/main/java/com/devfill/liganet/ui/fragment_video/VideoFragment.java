package com.devfill.liganet.ui.fragment_video;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.devfill.liganet.R;
import com.devfill.liganet.model.PhotoContent;
import com.devfill.liganet.model.VideoContent;
import com.devfill.liganet.network.ServerAPI;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class VideoFragment extends android.support.v4.app.Fragment{


    private static final String LOG_TAG = "VideoFragmentTag";

    private static final int RECOVERY_REQUEST = 1;
    private YouTubePlayerSupportFragment youTubePlayerSupportFragment;

    private TextView text_video,annotation_video;
    private ProgressBar progressBar;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    private  YouTubePlayer mPlayer;

    private Retrofit retrofit;
    private ServerAPI serverAPI;

    String videoUrl,linkHref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);


        youTubePlayerSupportFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtubesupportfragment);
        text_video = (TextView) rootView.findViewById(R.id.text_video);
        annotation_video = (TextView) rootView.findViewById(R.id.annotation_video);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progressVideo);

        text_video.setVisibility(View.INVISIBLE);
        annotation_video.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        youTubePlayerSupportFragment.getView().setVisibility(View.INVISIBLE);

        linkHref = getArguments().getString("linkHref");

        initRetrofit();

if(youTubePlayerSupportFragment != null){
    youTubePlayerSupportFragment.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM",new YouTubePlayer.OnInitializedListener(){


        @Override
        public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
            if (!wasRestored) {
                player.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
                // player.loadVideo(videoUrl);
                player.play();
                mPlayer = player;
                getVideoContent(linkHref);
            }
        }


        @Override
        public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult error) {
            // YouTube error
            String errorMessage = error.toString();
            Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
            Log.d("errorMessage:", errorMessage);
        }
    });

}
        else{
    Toast.makeText(getActivity(), "NULL", Toast.LENGTH_LONG).show();
        }

      //  youTubePlayerSupportFragment.getView().setVisibility(View.INVISIBLE);





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

    private void getVideoContent (String linkHref){
        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getVideoContent(linkHref).enqueue(new Callback<VideoContent>() {
                    @Override
                    public void onResponse(Call<VideoContent> call, Response<VideoContent> response) {

                        VideoContent videoContent = response.body();


                        try {


                            text_video.setVisibility(View.VISIBLE);
                            annotation_video.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.INVISIBLE);
                            youTubePlayerSupportFragment.getView().setVisibility(View.VISIBLE);

                            text_video.setText(Html.fromHtml(videoContent.getText()));
                            annotation_video.setText(Html.fromHtml(videoContent.getAnnotation()));
                            videoUrl = videoContent.getVideo_url();

                            youTubePlayerSupportFragment.getView().setVisibility(View.VISIBLE);

                            Log.i(LOG_TAG, "getVideoContent videoUrl " + videoUrl);
                            mPlayer.cueVideo(videoUrl);

                          //  youTubePlayerSupportFragment.initialize("AIzaSyAW4zFM9keH8D0uDd3YGbysra3Ci8Sn-tM",onInitializedListener);


                        }
                        catch(Exception e){

                            Toast.makeText(getContext(), "Не удалось распознать видео статью!" + e.getMessage(), Toast.LENGTH_LONG).show();

                        }

                        Log.i(LOG_TAG, "onResponse getVideoContent ");

                    }

                    @Override
                    public void onFailure(Call<VideoContent> call, Throwable t) {


                        Toast.makeText(getContext(), "Ошибка запроса к серверу!" + t.getMessage(), Toast.LENGTH_LONG).show();

                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
    }



}