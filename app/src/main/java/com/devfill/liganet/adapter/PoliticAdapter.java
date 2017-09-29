package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.devfill.liganet.R;
import com.devfill.liganet.model.News;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;

import java.util.List;


public class PoliticAdapter extends RecyclerView.Adapter<PoliticAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "ReminderTag";

    public static Context mContext;
    private Activity myActivity;
    public static List<News> mPoliticNewsList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView time,title;
        private View card_view;
        private ImageView image;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_news_politics);
            this.title = (TextView) v.findViewById(R.id.title_news_politics);
            this.card_view = v.findViewById(R.id.card_view_politics);
            this.image = (ImageView) v.findViewById(R.id.image_politics);
        }
    }

    public PoliticAdapter(Context context, Activity activity, List<News> list) {
        mPoliticNewsList = list;
        mContext = context;
        myActivity = activity;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.politic_card_view, viewGroup, false);
            return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final News news = mPoliticNewsList.get(position);

        viewHolder.time.setText(news.getTime());
        viewHolder.title.setText(Html.fromHtml(news.getTitle()));
        viewHolder.image.setImageBitmap(news.getBitmap());

        viewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(mContext, ArticleNewsActivity.class);
                    intent.putExtra("linkHref",news.getlinkHref());
                    intent.putExtra("imgHref",news.getImgUrl());
                    mContext.startActivity(intent);

                    Log.d(LOG_TAG, "viewHolder.card_view.setOnClickListener");
                    Log.d(LOG_TAG, "news.getlinkHref()" + news.getlinkHref());

                } catch (Exception e) {
                    Log.d(LOG_TAG, "exception", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPoliticNewsList.size();
    }

}
