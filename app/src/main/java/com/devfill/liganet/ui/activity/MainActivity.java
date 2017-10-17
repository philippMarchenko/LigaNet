package com.devfill.liganet.ui.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.devfill.liganet.R;

import com.devfill.liganet.ui.fragment_articles.ArticlesFragment;
import com.devfill.liganet.ui.fragment_photo.PhotoFragmentBase;
import com.devfill.liganet.ui.fragment_video.VideoFragmentBase;
import com.devfill.liganet.ui.helper.CustomViewPager;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = "mainActivityLigaNet";


    BottomNavigationView navigationView;
    private CustomViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (CustomViewPager) findViewById(R.id.viewpagerMainActivity);
        viewPager.setPagingEnabled(false);


        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        //navigationView.swi


        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.articles) {
                    viewPager.setCurrentItem(0);

                    return true;
                }
                else if (item.getItemId() == R.id.photo) {
                    viewPager.setCurrentItem(1);

                    return true;
                }
                else if (item.getItemId() == R.id.video) {
                    viewPager.setCurrentItem(2);

                    return true;
                }
                return false;
            }
        });


    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ArticlesFragment());
        adapter.addFragment(new PhotoFragmentBase());
        adapter.addFragment(new VideoFragmentBase());
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

        public void addFragment(android.support.v4.app.Fragment fragment) {
            mFragmentList.add(fragment);
          //  mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);

        }
    }

    @Override
    public void onBackPressed() {                           //для работоспособносьти бекстака вложеных фрагментов

        FragmentManager fm = getSupportFragmentManager();
        for (Fragment frag : fm.getFragments()) {
            if (frag.isVisible()) {
                FragmentManager childFm = frag.getChildFragmentManager();
                if (childFm.getBackStackEntryCount() > 0) {
                    childFm.popBackStack();
                    return;
                }
            }
        }
        super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();

        setupViewPager(viewPager);

        Log.i(LOG_TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        viewPager.removeAllViews();

        Log.i(LOG_TAG, " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, " onDestroy");
    }
}
