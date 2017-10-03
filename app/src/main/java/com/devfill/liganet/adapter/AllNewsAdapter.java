package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.model.News;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;
import com.devfill.liganet.ui.activity.ArticleVideoActivity;

import java.util.List;


public class AllNewsAdapter extends RecyclerView.Adapter<AllNewsAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "AllNewsAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<News> mListNewsShort;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView time,title;
        private View card_view;
        private ImageView image;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_news);
            this.title = (TextView) v.findViewById(R.id.title_news);
            this.card_view = v.findViewById(R.id.card_view_all_news);
            this.image = (ImageView) v.findViewById(R.id.image_all_news);


            Typeface face = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/10626.ttf");
           // title.setTypeface(face);


        }
    }


    public AllNewsAdapter(Context context, Activity activity, List<News> list) {

        mContext = context;
        myActivity = activity;
        mListNewsShort = list;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.news_card_view, viewGroup, false);
            return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final News news = mListNewsShort.get(position);

        viewHolder.time.setText(news.getTime());
        viewHolder.title.setText(Html.fromHtml(news.getTitle()));
        viewHolder.image.setImageBitmap(news.getBitmap());

        // define an on click listener to open PlaybackFragment
        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
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
                else{
                    try {

                        Intent intent = new Intent(mContext, ArticleVideoActivity.class);
                        intent.putExtra("videoUrl", news.getVideoUrl());
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

}
