package com.devfill.liganet.ui;


import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.PoliticAdapter;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.GetListNews;

import java.util.ArrayList;
import java.util.List;

public class WorldNewsFragment extends android.support.v4.app.Fragment implements GetListNews.IGetListNewsListener,
        SwipeRefreshLayout.OnRefreshListener{

        private static final String LOG_TAG = "WorldNewsFragmentTag";

        private List<News> worldList = new ArrayList<>();
        private RecyclerView recyclerView;
        private PoliticAdapter worldAdapter;
        private SwipeRefreshLayout swipeRefreshLayout;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_world, container, false);

            recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_world);
            worldAdapter = new PoliticAdapter(getContext(),getActivity(),worldList);
            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(worldAdapter);

            swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout_world);
            swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));

            swipeRefreshLayout.setOnRefreshListener(this);

            initWorldList();

            return rootView;
        }
        public  void initWorldList (){

            GetListNews getListNews = new GetListNews(this);
            getListNews.execute("http://news.liga.net/all/world/");
        }

        @Override
        public void onGetListNewsFinished(List<News> news) {

            worldList.clear();
            worldList.addAll(news);
            worldAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);

        }
    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        initWorldList();
    }
}
