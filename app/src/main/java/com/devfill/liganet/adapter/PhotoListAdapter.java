package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.devfill.liganet.R;
import com.devfill.liganet.model.News;
import com.devfill.liganet.ui.fragment_photo.PhotoFragment;

import java.util.List;


public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "PhotoListAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private List<News> mListNewsShort;
    FragmentTransaction ft;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView time,title;
        private View card_view;
        private ImageView image;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_new_photo);
            this.title = (TextView) v.findViewById(R.id.title_news_photo);
            this.card_view = v.findViewById(R.id.card_view_photo);
            this.image = (ImageView) v.findViewById(R.id.image_photo);

        }
    }


    public PhotoListAdapter(Context context, Activity activity,FragmentTransaction ft, List<News> list) {

        mContext = context;
        myActivity = activity;
        mListNewsShort = list;
        this.ft = ft;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.photo_card_view, viewGroup, false);
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

                PhotoFragment photoFragment = new PhotoFragment();

                Bundle bundle = new Bundle();
                bundle.putString("linkHref",news.getlinkHref());
                photoFragment.setArguments(bundle);

                ft.replace(R.id.container_photo,photoFragment);
                ft.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListNewsShort.size();
    }

}
