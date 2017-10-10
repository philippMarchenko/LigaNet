package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import com.devfill.liganet.ui.activity.VideoActivity;


import java.util.List;


public class AllNewsAdapter extends RecyclerView.Adapter<AllNewsAdapter.MyViewHolder>  {

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
            this.time = (TextView) v.findViewById(R.id.time_news_all_news);
            this.title = (TextView) v.findViewById(R.id.title_news_all_news);
            this.card_view = v.findViewById(R.id.card_view_all_news);
            this.image = (ImageView) v.findViewById(R.id.image_all_news);


        }
    }


    public AllNewsAdapter(Context context, Activity activity, List<News> list,RecyclerView recyclerView) {

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
                .inflate(R.layout.news_card_view, viewGroup, false);
        return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {


            final News news = mListNewsShort.get(position);

            myViewHolder.time.setText(news.getTime());
            myViewHolder.title.setText(Html.fromHtml(news.getTitle()));

            if(!news.getVideoUrl().equals("")){

                myViewHolder.image.setImageDrawable(mContext.getDrawable(R.drawable.video));

            }
            else{

                myViewHolder.image.setImageBitmap(news.getBitmap());
            }



            if(news.getVideoUrl().equals("") && !news.getIs_photo().equals("")){

                //viewHolder.video.setVisibility(View.VISIBLE);

            }



            // define an on click listener to open PlaybackFragment
            myViewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(news.getVideoUrl().equals("")){

                        Log.d(LOG_TAG, "Это не видео");
                        try {
                            Intent intent = new Intent(mContext, ArticleNewsActivity.class);
                            intent.putExtra("linkHref", news.getlinkHref());
                            intent.putExtra("imgHref", news.getImgUrl());
                            mContext.startActivity(intent);


                        } catch (Exception e) {
                            Log.d(LOG_TAG, "exception", e);
                        }
                    }
                    else if (news.getIs_photo().equals("1")) {

                        try {

                            Intent intent = new Intent(mContext, PhotoContent.class);
                            intent.putExtra("linkHref", news.getlinkHref());
                            mContext.startActivity(intent);


                        } catch (Exception e) {
                            Log.d(LOG_TAG, "exception", e);
                        }
                        Log.d(LOG_TAG, " А это фото");
                    }

                    else{
                        try {

                            Intent intent = new Intent(mContext, VideoActivity.class);
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
