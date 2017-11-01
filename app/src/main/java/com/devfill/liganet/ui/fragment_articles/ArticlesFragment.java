package com.devfill.liganet.ui.fragment_articles;

import android.os.Bundle;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.devfill.liganet.R;
import com.devfill.liganet.adapter.WorldAdapter;
import com.devfill.liganet.ui.fragment_articles.AllNewsFragment;
import com.devfill.liganet.ui.fragment_articles.EconomicFragment;
import com.devfill.liganet.ui.fragment_articles.PoliticFragment;
import com.devfill.liganet.ui.fragment_articles.WorldNewsFragment;

import java.util.ArrayList;
import java.util.List;


public class ArticlesFragment extends android.support.v4.app.Fragment  {


    private static final String LOG_TAG = "ArticlesFragmentTag";

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.articles_fragment, container, false);

        toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);

        AppCompatActivity appCompatActivity =  (AppCompatActivity) getActivity();

        appCompatActivity.setSupportActionBar(toolbar);

        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        appCompatActivity.getSupportActionBar().hide();

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpagerArticles);
        setupViewPager(viewPager);


        tabLayout = (TabLayout) rootView.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        return rootView;
    }

    private void setupViewPager(ViewPager viewPager) {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        adapter.addFragment(new AllNewsFragment(), "Все новости");
        adapter.addFragment(new EconomicFragment(), "Экономика");
        adapter.addFragment(new WorldNewsFragment(), "Мир");
        adapter.addFragment(new PoliticFragment(), "Политика");

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                try{

                   if(position == 0){


                   /*    EconomicFragment economicFragment = (EconomicFragment) adapter.getItem(1);
                       economicFragment.pauseLoadImage();
                       WorldNewsFragment worldNewsFragment = (WorldNewsFragment) adapter.getItem(2);
                       worldNewsFragment.pauseLoadImage();
                       PoliticFragment politicFragment = (PoliticFragment) adapter.getItem(3);
                       politicFragment.pauseLoadImage();*/
                   }
                   else if(position == 1){

                        EconomicFragment economicFragment = (EconomicFragment) adapter.getItem(position);

                        if(!economicFragment.listIsShowed){
                            economicFragment.getEconomicList();

                            /*AllNewsFragment allNewsFragment = (AllNewsFragment) adapter.getItem(0);
                            allNewsFragment.pauseLoadImage();
                            WorldNewsFragment worldNewsFragment = (WorldNewsFragment) adapter.getItem(2);
                            worldNewsFragment.pauseLoadImage();
                            PoliticFragment politicFragment = (PoliticFragment) adapter.getItem(3);
                            politicFragment.pauseLoadImage();*/

                        }
                    }
                    else if(position == 2){

                        WorldNewsFragment worldNewsFragment  = (WorldNewsFragment) adapter.getItem(position);
                        if(!worldNewsFragment.listIsShowed) {
                            worldNewsFragment.getWorldNewsList();


                           /* AllNewsFragment allNewsFragment = (AllNewsFragment) adapter.getItem(0);
                            allNewsFragment.pauseLoadImage();
                            EconomicFragment economicFragment = (EconomicFragment) adapter.getItem(1);
                            economicFragment.pauseLoadImage();
                            PoliticFragment politicFragment = (PoliticFragment) adapter.getItem(3);
                            politicFragment.pauseLoadImage();*/

                        }

                    }
                    else if(position == 3){

                        PoliticFragment politicFragment = (PoliticFragment) adapter.getItem(position);
                        if(!politicFragment.listIsShowed) {
                            politicFragment.getPoliticsNewsList();

                           /* AllNewsFragment allNewsFragment = (AllNewsFragment) adapter.getItem(0);
                            allNewsFragment.pauseLoadImage();
                            EconomicFragment economicFragment = (EconomicFragment) adapter.getItem(1);
                            economicFragment.pauseLoadImage();
                            WorldNewsFragment worldNewsFragment = (WorldNewsFragment) adapter.getItem(2);
                            worldNewsFragment.pauseLoadImage();*/

                        }
                    }
                }
                catch(Exception e){


                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
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

    }



}