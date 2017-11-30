package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
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
import com.devfill.liganet.model.MoreNews;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Philipp on 29.11.2017.
 */

public class MoreNewsAdapter extends RecyclerView.Adapter<MoreNewsAdapter.MyViewHolder>{


    private static final String LOG_TAG = "MoreNewsAdapter";

    private static Context mContext;
    private Activity myActivity;
    private  List<MoreNews.Article> articleList;
    private OnItemClick onItemClick;

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


            Typeface typefaceRI = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-RI.ttf");
            Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-R.ttf");
            this.title.setTypeface(typefaceR);
            this.time.setTypeface(typefaceRI);



        }
    }

    public MoreNewsAdapter(Context context, Activity activity, List<MoreNews.Article> articleList, RecyclerView recyclerView) {

        mContext = context;
        myActivity = activity;
        this.articleList = articleList;
        Log.i(LOG_TAG, "MoreNewsAdapter " );

    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Log.i(LOG_TAG, "onCreateViewHolder " );

        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.news_card_view, viewGroup, false);
        return new MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final MyViewHolder myViewHolder, final int position) {


        final MoreNews.Article article = articleList.get(position);

        myViewHolder.time.setText(article.getDate());

        myViewHolder.title.setText(Html.fromHtml(article.getTitle_text()));

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap croppedBmp =  Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap;

        try{
            croppedBmp = Bitmap.createBitmap(article.getBitmap(), 0, 0, article.getBitmap().getWidth(), article.getBitmap().getHeight()-18);

        }
        catch (Exception e){

        }
        Log.i(LOG_TAG, "onBindViewHolder " + article.getTitle_text());



        myViewHolder.image.setImageBitmap(croppedBmp);

        myViewHolder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                    onItemClick.onItemClickListener(articleList.get(position).getTitle_href());

                } catch (Exception e) {
                    Log.d(LOG_TAG, "exception", e);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articleList.size();
    }


    public interface OnItemClick{

        void onItemClickListener(String href);
    }

    public void setOnItemClick(OnItemClick onItemClick){

        this.onItemClick = onItemClick;
    }

}
