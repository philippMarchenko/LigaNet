package com.devfill.liganet.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.AllNewsAdapter;
import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.GetDataNews;
import com.devfill.liganet.network.GetListNews;

import java.util.ArrayList;
import java.util.List;

public class AllNewsFragment extends android.support.v4.app.Fragment implements GetListNews.IGetListNewsListener,
        SwipeRefreshLayout.OnRefreshListener{


    private static final String LOG_TAG = "AllNewsFragmentTag";

    private List<News> allNewsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllNewsAdapter allNewsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_news, container, false);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_all_news);
        allNewsAdapter = new AllNewsAdapter(getContext(),getActivity(),allNewsList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(allNewsAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_all_news);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);

        initAllNewsList();

        return rootView;
    }

    public  void initAllNewsList (){

        allNewsList.clear();
        GetListNews getLigaNet = new GetListNews(this);
        getLigaNet.execute("http://news.liga.net/all/");
    }

    @Override
    public void onGetListNewsFinished(List<News> news) {


        allNewsList.addAll(news);
        allNewsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);



       // URL url = new

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        initAllNewsList();
    }


}