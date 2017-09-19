package com.devfill.liganet.adapter;

import android.app.Activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import android.widget.TextView;
import com.devfill.liganet.R;
import com.devfill.liganet.model.News;


import java.util.List;


public class EconomicAdapter extends RecyclerView.Adapter<EconomicAdapter.MyViewHolder>  {

    private static final String LOG_TAG = "EconomicAdapterTag";

    private static Context mContext;
    private Activity myActivity;
    private static List<News> mEconomicNewsList;



    public class MyViewHolder extends RecyclerView.ViewHolder{

        public TextView time,title;

        public MyViewHolder(View v) {
            super(v);
            this.time = (TextView) v.findViewById(R.id.time_economic);
            this.title = (TextView) v.findViewById(R.id.title_economic);


        }
    }


    public EconomicAdapter(Context context, Activity activity,List<News> list) {

        mEconomicNewsList = list;
        mContext = context;
        myActivity = activity;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.economic_card_view, viewGroup, false);
            return new MyViewHolder(v);

    }
    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {

        final News news = mEconomicNewsList.get(position);

        viewHolder.time.setText(news.getTime());
        viewHolder.title.setText(news.getTitle());
    }

    @Override
    public int getItemCount() {
        return mEconomicNewsList.size();
    }

}
