package com.devfill.liganet.ui.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.devfill.liganet.R;
import com.devfill.liganet.ui.fragment_articles.NewsFragment;
import com.ogaclejapan.smarttablayout.SmartTabLayout;


import java.util.ArrayList;
import java.util.List;

public class MainActivityOnlyArticles extends AppCompatActivity {

    private String LOG_TAG = "OnlyArticles";

    ViewPagerAdapter adapter;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<Fragment> fragmentList = new ArrayList<>();

    SmartTabLayout tabs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_fragment);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().hide();

        viewPager = (ViewPager) findViewById(R.id.viewpagerArticles);

      //  tabLayout = (TabLayout) findViewById(R.id.tabs);
      //  tabLayout.setupWithViewPager(viewPager);

        setupViewPager(viewPager);

        tabs = (SmartTabLayout) findViewById(R.id.tabs);
        tabs.setViewPager(viewPager);

    }

    private void setupViewPager(ViewPager viewPager) {

        NewsFragment newsFragment = new NewsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_all_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Все новости");
        fragmentList.add(newsFragment);

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_economics_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Экономика");
        fragmentList.add(newsFragment);

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_world_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Мир");
        fragmentList.add(newsFragment);

        newsFragment = new NewsFragment();
        bundle = new Bundle();
        bundle.putString("url_news", "http://api.mkdeveloper.ru/liga_net/get_politic_news.php");
        newsFragment.setArguments(bundle);
        adapter.addFragment(newsFragment, "Политика");
        fragmentList.add(newsFragment);

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
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return mFragmentTitleList.get(position);
        }

        public void clearAdapter() {

            mFragmentList.clear();
            mFragmentTitleList.clear();
        }

    }

}
