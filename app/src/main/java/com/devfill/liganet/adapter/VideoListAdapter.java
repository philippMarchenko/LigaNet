package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.devfill.liganet.R;
import com.devfill.liganet.helper.OnLoadMoreListener;
import com.devfill.liganet.model.News;
import com.devfill.liganet.ui.fragment_photo.PhotoFragment;
import com.devfill.liganet.ui.fragment_video.VideoFragment;

import java.util.List;


public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "VideoListAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<News> mListNewsShort;
    FragmentTransaction ft;

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
            this.time = (TextView) v.findViewById(R.id.time_new_video);
            this.title = (TextView) v.findViewById(R.id.title_news_video);
            this.card_view = v.findViewById(R.id.card_view_video);
            this.image = (ImageView) v.findViewById(R.id.image_video);

            Typeface typefaceRI = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-RI.ttf");
            Typeface typefaceR = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/UbuntuMono-R.ttf");
            this.title.setTypeface(typefaceR);
            this.time.setTypeface(typefaceRI);
        }
    }


    public VideoListAdapter(Context context, Activity activity,FragmentTransaction ft, List<News> list,RecyclerView recyclerView) {

        mContext = context;
        myActivity = activity;
        mListNewsShort = list;
        this.ft = ft;

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
                .inflate(R.layout.video_card_view, viewGroup, false);
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

                VideoFragment videoFragment = new VideoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("linkHref",news.getLinkHref());
                videoFragment.setArguments(bundle);

                ft.replace(R.id.container_video,videoFragment);
                ft.addToBackStack(null);    //добавляем транзакцию в бэкстэк для возврата кнопкой на
                ft.commit();
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
