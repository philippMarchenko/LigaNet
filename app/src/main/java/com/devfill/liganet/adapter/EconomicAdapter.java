package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.helper.OnLoadMoreListener;
import com.devfill.liganet.model.News;
import com.devfill.liganet.model.PhotoContent;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;
import com.devfill.liganet.ui.activity.PhotoActivity;
import com.devfill.liganet.ui.activity.VideoActivity;


import java.util.List;


public class EconomicAdapter extends RecyclerView.Adapter<EconomicAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "AllNewsAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<News> mListNewsShort;

    private OnLoadMoreListener onLoadMoreListener;

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    private boolean loading;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView time,title;
        private View card_view;
        private ImageView image;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_news_economic);
            this.title = (TextView) v.findViewById(R.id.title_news_economic);
            this.card_view = v.findViewById(R.id.card_view_economic);
            this.image = (ImageView) v.findViewById(R.id.image_economic);

            Typeface typefaceRI = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-RI.ttf");
            Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-R.ttf");
            this.title.setTypeface(typefaceR);
            this.time.setTypeface(typefaceRI);
        }
    }


    public EconomicAdapter(Context context, Activity activity, List<News> list,RecyclerView recyclerView) {

        mContext = context;
        myActivity = activity;
        mListNewsShort = list;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();


            recyclerView
                    .addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrolled(RecyclerView recyclerView,
                                               int dx, int dy) {
                            super.onScrolled(recyclerView, dx, dy);

                            totalItemCount = linearLayoutManager.getItemCount();
                            lastVisibleItem = linearLayoutManager
                                    .findLastVisibleItemPosition();
                            if (!loading
                                    && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                                // End has been reached
                                // Do something
                                if (onLoadMoreListener != null) {
                                    onLoadMoreListener.onLoadMore();
                                }
                                loading = true;
                            }
                        }
                    });
        }
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.economic_card_view, viewGroup, false);
        return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {


        final News news = mListNewsShort.get(position);

        myViewHolder.time.setText(news.getTime());
        myViewHolder.title.setText(Html.fromHtml(news.getTitle()));

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap croppedBmp =  Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap;

        try{
            croppedBmp = Bitmap.createBitmap(news.getBitmap(), 0, 0, news.getBitmap().getWidth(), news.getBitmap().getHeight()-18);

        }
        catch (Exception e){

        }

        if(!news.getVideoUrl().equals("")){

            myViewHolder.image.setImageDrawable(mContext.getDrawable(R.drawable.video2));

        }
        else if (news.getIs_photo().equals("1")) {

            myViewHolder.image.setImageDrawable(mContext.getDrawable(R.drawable.foto));
        }
        else{

            myViewHolder.image.setImageBitmap(croppedBmp);
        }



        // define an on click listener to open PlaybackFragment
        myViewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(news.getVideoUrl().equals("") && !news.getIs_photo().equals("1")){   //если это не видео и не фото

                    Log.d(LOG_TAG, "Это не видео");
                    try {
                        Intent intent = new Intent(mContext, ArticleNewsActivity.class);    //ЭТО СТАТЬЯ
                        intent.putExtra("linkHref", news.getlinkHref());
                        intent.putExtra("imgHref", news.getImgUrl());
                        mContext.startActivity(intent);


                    } catch (Exception e) {
                        Log.d(LOG_TAG, "exception", e);
                    }
                }
                else if (news.getIs_photo().equals("1")) {          //это фото

                    try {

                        Intent intent = new Intent(mContext, PhotoActivity.class);
                        intent.putExtra("linkHref", news.getlinkHref());
                        mContext.startActivity(intent);


                    } catch (Exception e) {
                        Log.d(LOG_TAG, "exception", e);
                    }
                    Log.d(LOG_TAG, " А это фото");
                }

                else{
                    try {

                        Intent intent = new Intent(mContext, VideoActivity.class);      // а это видео
                        intent.putExtra("linkHref", news.getlinkHref());
                        mContext.startActivity(intent);


                    } catch (Exception e) {
                        Log.d(LOG_TAG, "exception", e);
                    }
                    Log.d(LOG_TAG, " А это видео");
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mListNewsShort.size();
    }


    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setLoaded() {
        loading = false;
    }
}
