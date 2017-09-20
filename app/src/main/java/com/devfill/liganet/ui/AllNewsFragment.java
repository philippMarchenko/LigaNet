package com.devfill.liganet.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.devfill.liganet.R;
import com.devfill.liganet.adapter.AllNewsAdapter;
import com.devfill.liganet.model.ArticleNews;
import com.devfill.liganet.model.News;
import com.devfill.liganet.network.GetArticleImage;
import com.devfill.liganet.network.GetDataNews;
import com.devfill.liganet.network.GetListNews;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AllNewsFragment extends android.support.v4.app.Fragment implements GetListNews.IGetListNewsListener,
        SwipeRefreshLayout.OnRefreshListener,
        GetDataNews.IGetDataNewsListener,
        GetArticleImage.IGetArticleImageListener{


    private static final String LOG_TAG = "AllNewsFragmentTag";

    private ImageView image_all_news;

    private List<Bitmap> bitMapList = new ArrayList<>();
    private List<News> allNewsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private AllNewsAdapter allNewsAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    private int count_bitmap = 0;

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

        swipeRefreshLayout.setRefreshing(true);
        allNewsList.clear();
        GetListNews getLigaNet = new GetListNews(this);
        getLigaNet.execute("http://news.liga.net/all/");
    }

    @Override
    public void onGetListNewsFinished(List<News> news) {

        URL url = null;
        String link = null;

        for(int i = 0; i < news.size(); i++){

            GetDataNews getDataNews = new GetDataNews(this);

            if (!URLUtil.isValidUrl(news.get(i).getlinkHref())) {

                link = "http://news.liga.net" + news.get(i).getlinkHref();
                Log.d(LOG_TAG, "link " + link);
                try {
                    url = new URL(link);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                getDataNews.execute(link,"img","http://news.liga.net",Integer.toString(i)); //добавим его
            }
            else{
                try {
                    url = new URL(news.get(i).getlinkHref());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                getDataNews.execute(news.get(i).getlinkHref(),"img","http://" + url.getHost(),Integer.toString(i));
            }
        }


        allNewsList.addAll(news);
        allNewsAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onRefresh() {

        swipeRefreshLayout.setRefreshing(true);
        initAllNewsList();
    }

    @Override
    public void onGetDataNewsFinished(ArticleNews articleNews) {

        Log.d(LOG_TAG, "onGetDataNewsFinished. getImgUrl() " + articleNews.getImgUrl());

        int positionInList = Integer.parseInt(articleNews.getItem());

        loadImage(articleNews.getImgUrl(),allNewsList.get(positionInList));

      //  GetArticleImage getArticleImage = new GetArticleImage(this,getContext());
       // getArticleImage.execute(articleNews.getImgUrl());
    }

    @Override
    public void onGetArticleImageFinished(Bitmap bitmap) {

        Log.d(LOG_TAG, "onGetArticleImageFinished. count_bitmap " + count_bitmap);

        if(count_bitmap < allNewsList.size() - 1){

            allNewsList.get(count_bitmap).setBitmap(bitmap);
            allNewsAdapter.notifyDataSetChanged();

            count_bitmap++;
        }
        else{
            count_bitmap = 0;

           /* for(int i = 0; i < bitMapList.size(); i ++){

                allNewsList.get(i).setBitmap(bitMapList.get(i));

            }

            Log.d(LOG_TAG, "bitMapList.size= " + bitMapList.size());*/
        }
    }

    private void loadImage(String imageUrl, final News news){

        Target loadtarget = null;
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int height = (int) (100 * scale + 0.5f);
        int width = (int) (120 * scale + 0.5f);


        if (loadtarget == null) loadtarget = new Target() {

            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                news.setBitmap(bitmap);
                allNewsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(LOG_TAG, "onBitmapFailed " + errorDrawable.toString());

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };

        Picasso.with(getContext()).load(imageUrl).resize(width,height).into(loadtarget);

    }

}