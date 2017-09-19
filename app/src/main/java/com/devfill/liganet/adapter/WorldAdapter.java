package com.devfill.liganet.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.devfill.liganet.R;
import com.devfill.liganet.model.News;

import java.util.List;


public class WorldAdapter extends RecyclerView.Adapter<WorldAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "WorldAdapter";

    public static Context mContext;
    private Activity myActivity;
    public static List<News> mWorldList;

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView time,title;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_world);
            this.title = (TextView) v.findViewById(R.id.title_world);
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
        viewHolder.title.setText(news.getTitle());

    }

    @Override
    public int getItemCount() {
        return mWorldList.size();
    }

}
