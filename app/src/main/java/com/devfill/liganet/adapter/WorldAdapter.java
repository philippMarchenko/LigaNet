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
import android.widget.ImageView;
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.model.News;
import com.devfill.liganet.model.PhotoContent;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;
import com.devfill.liganet.ui.activity.VideoActivity;


import java.util.List;


public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "WorldAdapterTag";

    public static Context mContext;
    private Activity myActivity;
    public static List<News> mWorldList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView time,title,photo;
        private View card_view;
        private ImageView image,video;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_news_world);
            this.title = (TextView) v.findViewById(R.id.title_news_world);
            this.photo = (TextView) v.findViewById(R.id.photo);
            this.card_view = v.findViewById(R.id.card_view_world);
            this.image = (ImageView) v.findViewById(R.id.image_world);
            this.video = (ImageView) v.findViewById(R.id.video);

            photo.setVisibility(View.INVISIBLE);
            video.setVisibility(View.INVISIBLE);
        }
    }

    public WorldAdapter(Context context, Activity activity, List<News> list) {
        mWorldList = list;
        mContext = context;
        myActivity = activity;

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.world_card_view, viewGroup, false);
            return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        final News news = mWorldList.get(position);

        viewHolder.time.setText(news.getTime());
        viewHolder.title.setText(Html.fromHtml(news.getTitle()));
        viewHolder.image.setImageBitmap(news.getBitmap());

        Log.d(LOG_TAG, "onBindViewHolder getVideoUrl " + news.getVideoUrl());

        if(!news.getVideoUrl().equals("")){

            viewHolder.video.setVisibility(View.VISIBLE);

        }
        if(news.getIs_photo().equals("1")){

            viewHolder.photo.setVisibility(View.VISIBLE);

        }
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
        return mWorldList.size();
    }

}
