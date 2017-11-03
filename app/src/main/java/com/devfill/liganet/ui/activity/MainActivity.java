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

import com.devfill.liganet.ui.fragment_articles.NewsFragmentBase;
import com.devfill.liganet.ui.fragment_photo.PhotoFragmentBase;
import com.devfill.liganet.ui.fragment_photo.PhotoListFragment;
import com.devfill.liganet.ui.fragment_video.VideoFragmentBase;
import com.devfill.liganet.ui.helper.CustomViewPager;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private String LOG_TAG = "mainActivityLigaNet";


    BottomNavigationView navigationView;
    private CustomViewPager viewPager;

    private int mCurrentPage = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (CustomViewPager) findViewById(R.id.viewpagerMainActivity);
        viewPager.setPagingEnabled(false);


        navigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.articles) {
                    viewPager.setCurrentItem(0);    //переключаемся между фрагментами

                    return true;
                }
                else if (item.getItemId() == R.id.photo) {
                    viewPager.setCurrentItem(1);   //переключаемся между фрагментами

                    return true;
                }
                else if (item.getItemId() == R.id.video) {
                    viewPager.setCurrentItem(2);   //переключаемся между фрагментами

                    return true;
                }
                return false;
            }
        });


    }

    private void setupViewPager(final ViewPager viewPager)
    {
        final ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new NewsFragmentBase());
        adapter.addFragment(new PhotoFragmentBase());
        adapter.addFragment(new VideoFragmentBase());


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.i(LOG_TAG, "onPageSelected " + position);


                if(position == 1){      //если выбрали раздел фото,запустим запрос к серверу
                    PhotoFragmentBase photoFragmentBase = (PhotoFragmentBase) adapter.getItem(position);
                    photoFragmentBase.getPhotoList();
                }
                else if(position == 2){  //если выбрали раздел видео,запустим запрос к серверу

                    VideoFragmentBase videoFragmentBase = (VideoFragmentBase) adapter.getItem(position);
                    videoFragmentBase.getVideoList();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        viewPager.setAdapter(adapter);

        viewPager.setCurrentItem(mCurrentPage);

        for (int i = 0; i < navigationView.getMenu().size(); i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(mCurrentPage).setChecked(true);
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

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("numberPage", viewPager.getCurrentItem());
        Log.d(LOG_TAG, "onSaveInstanceState");
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentPage = savedInstanceState.getInt("numberPage");
        Log.d(LOG_TAG, "onRestoreInstanceState");
    }
}
