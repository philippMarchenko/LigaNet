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
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.listeners.OnLoadMoreListener;
import com.devfill.liganet.listeners.OnScrollingListener;
import com.devfill.liganet.model.News;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "AllNewsAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<News> mListNewsShort;

    private OnLoadMoreListener onLoadMoreListener;
    private OnScrollingListener onScrollingListener;



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


            Typeface typefaceRI = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-RI.ttf");
            Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-R.ttf");
            this.title.setTypeface(typefaceR);
            this.time.setTypeface(typefaceRI);

        }
    }

    public NewsAdapter(Context context, Activity activity, List<News> list, RecyclerView recyclerView) {

        mContext = context;
        myActivity = activity;
        mListNewsShort = list;

        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {

            final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                    .getLayoutManager();

            RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    switch (newState) {
                        case RecyclerView.SCROLL_STATE_IDLE:
                            onScrollingListener.onStopScrolling();
                            Log.d(LOG_TAG, "The RecyclerView is not scrolling ");
                            break;
                        case RecyclerView.SCROLL_STATE_DRAGGING:
                            onScrollingListener.onScrollNow();
                            Log.d(LOG_TAG, "Scrolling now ");
                            break;
                        case RecyclerView.SCROLL_STATE_SETTLING:
                            Log.d(LOG_TAG, "Scroll Settling ");
                            break;

                    }

                }

                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
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
            };


            recyclerView.addOnScrollListener(onScrollListener);
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

            SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd.MM HH:mm");
            long time = Long.parseLong(news.getTime_ms());
            Date date = new Date(time);
            String strTimeDate = simpleDateFormatDate.format(date);

            myViewHolder.time.setText(strTimeDate);
           //myViewHolder.time.setText(news.getTime());

            myViewHolder.title.setText(Html.fromHtml(news.getTitle()));

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap croppedBmp =  Bitmap.createBitmap(100, 100, conf); // this creates a MUTABLE bitmap;

        try{
            croppedBmp = Bitmap.createBitmap(news.getBitmap(), 0, 0, news.getBitmap().getWidth(), news.getBitmap().getHeight()-18);

        }
        catch (Exception e){

        }



        if(news.getIsVideo().equals("1")){

                myViewHolder.image.setImageDrawable(mContext.getDrawable(R.drawable.video2));

            }
        else if (news.getIs_photo().equals("1")) {

                myViewHolder.image.setImageDrawable(mContext.getDrawable(R.drawable.foto));
            }
            else{

                myViewHolder.image.setImageBitmap(croppedBmp);
            }

            myViewHolder.card_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        try {
                            Intent intent = new Intent(mContext, ArticleNewsActivity.class);    //ЭТО СТАТЬЯ
                            ArrayList<String> newsList = new ArrayList<String>();

                           for(int i = 0; i < mListNewsShort.size(); i ++){
                               newsList.add(mListNewsShort.get(i).getLinkHref());
                           }
                            intent.putExtra("newsList", newsList);
                            intent.putExtra("position", position);
                            mContext.startActivity(intent);

                        } catch (Exception e) {
                            Log.d(LOG_TAG, "exception", e);
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

    public void setOnScrollingListener(OnScrollingListener onScrollingListener) {
        this.onScrollingListener = onScrollingListener;
    }


    public void setLoaded() {
        loading = false;
    }
}
