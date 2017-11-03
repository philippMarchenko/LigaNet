package com.devfill.liganet.ui.fragment_articles;



import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.devfill.liganet.model.News;

import java.util.List;

public class SavedFragment extends Fragment {



    private List<News> news;


    @Override
    public void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setRetainInstance(true);
    }
    public List<News> getNews() {
        return news;
    }

    public void setNews(List<News> news) {
        this.news = news;
    }
}