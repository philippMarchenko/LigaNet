package com.devfill.liganet.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.view.PagerAdapter;
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

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtarget = null;

    private List<NewsContent> list = new ArrayList<>();

    private TextView dateAtricle,anotation,textArticle;
    private ImageView backdrop;
    private ProgressBar progressArticle;

    private Html.ImageGetter igLoader;

    private static final String LOG_TAG = "ArticleNewsAdapterTag";

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
        View imageLayout = inflater.inflate(R.layout.activity_article_news, view, false);

        Log.d(LOG_TAG, "instantiateItem ");

        initIloader(position);

        dateAtricle = (TextView) imageLayout.findViewById(R.id.dateAtricle);
        anotation = (TextView) imageLayout.findViewById(R.id.anotation);
        textArticle = (TextView) imageLayout.findViewById(R.id.textArticle);
        backdrop = (ImageView) imageLayout.findViewById(R.id.backdrop);
        progressArticle = (ProgressBar) imageLayout.findViewById(R.id.progressArticle);


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


}