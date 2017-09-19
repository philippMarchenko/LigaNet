package com.devfill.liganet.ui;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.PoliticAdapter;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.GetListNews;

import java.util.ArrayList;
import java.util.List;

public class PoliticFragment extends android.support.v4.app.Fragment implements GetListNews.IGetListNewsListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String LOG_TAG = "PoliticFragmentTag";

    private List<News> politicList = new ArrayList<>();
    private RecyclerView recyclerView;
    private PoliticAdapter politicAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_politic, container, false);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_politic);
        politicAdapter = new PoliticAdapter(getContext(),getActivity(),politicList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(politicAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_politic);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

        swipeRefreshLayout.setOnRefreshListener(this);

        initPoliticList();

        return rootView;
    }
    public  void initPoliticList (){

        GetListNews getListNews = new GetListNews(this);
        getListNews.execute("http://news.liga.net/all/politics/");
    }

    @Override
    public void onGetListNewsFinished(List<News> news) {

        politicList.clear();
        politicList.addAll(news);
        politicAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        initPoliticList();
    }
}