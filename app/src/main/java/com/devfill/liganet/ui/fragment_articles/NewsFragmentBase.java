package com.devfill.liganet.ui.fragment_articles;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.devfill.liganet.R;

import java.util.ArrayList;
import java.util.List;


public class NewsFragmentBase extends android.support.v4.app.Fragment {


    private static final String LOG_TAG = "NewsFragmentBase";
    ViewPagerAdapter adapter;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.articles_fragment, container, false);

        adapter = new ViewPagerAdapter(getChildFragmentManager());

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();

        appCompatActivity.setSupportActionBar(toolbar);

        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appCompatActivity.getSupportActionBar().hide();

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerArticles);
       // setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {

        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_all_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Все новости");

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_economics_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Экономика");

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_world_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Мир");

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_politic_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Политика");

        viewPager.setAdapter(adapter);

    }


    @Override
    public void onResume() {
        super.onResume();

        Log.i(LOG_TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.i(LOG_TAG, " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, " onDestroy");
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(android.support.v4.app.Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }

        public void clearAdapter(){

            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

    }

}