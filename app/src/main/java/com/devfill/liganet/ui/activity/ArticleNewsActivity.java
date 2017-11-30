package com.devfill.liganet.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.devfill.liganet.R;
import com.devfill.liganet.ui.fragment_articles_view_pager.ArticlesFragmentViewPager;
import com.devfill.liganet.ui.fragment_photo.PhotoFragment;
import com.devfill.liganet.ui.fragment_video.VideoFragment;
import com.eftimoff.viewpagertransformers.DepthPageTransformer;

import java.util.ArrayList;
import java.util.List;

public class ArticleNewsActivity extends AppCompatActivity {


    private static final String LOG_TAG = "ArticleNewsActivityTag";


    private int positionPage = 0;       //позиция текущей страницы
    private int positionArticle = 0;    //позиция статьи по которой нажали
    private boolean isPhoto = false;
    private boolean isVideo = false;

    ArrayList<String> myList;

    ViewPager mPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_news_pager);

        mPager = (ViewPager) findViewById(R.id.pagerArticles);

        myList = (ArrayList<String>) getIntent().getSerializableExtra("newsList");
        positionArticle = getIntent().getIntExtra("position",0);

        Log.d(LOG_TAG, "myList.size " + myList.size());
        Log.d(LOG_TAG, "positionArticle " + positionArticle);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for(int i = 0; i <= myList.size(); i++){

            addFragment(i);
        }

        mPager.setAdapter(adapter);
        mPager.setCurrentItem(positionArticle);
        mPager.setPageTransformer(true, new DepthPageTransformer());
        initPagerListener();


    }

    private void checkPhotoVideo(int position){

        if(checkForWord(myList.get(position),"/photo/")){

            isPhoto = true;
        }
        else{

            isPhoto = false;
        }

        if(checkForWord(myList.get(position),"/video/") || checkForWord(myList.get(position),"/videomaterialy/")){

            isVideo = true;
        }
        else{

            isVideo = false;
        }
    }

    private void addFragment(int position){

        if((position < myList.size())){

            checkPhotoVideo(position);

            if(isVideo){

                    VideoFragment videoFragment = new VideoFragment(new VideoFragment.VideoFragmentListener() {
                        @Override
                        public void videoFragmentCreate() {
                            Log.d(LOG_TAG, "videoFragmentCreate ");

                            if(adapter.getItem(positionArticle) instanceof VideoFragment){
                                VideoFragment videoFragment = (VideoFragment) adapter.getItem(positionArticle);
                                videoFragment.playVideo();
                            }
                        }
                    });
                    Bundle bundle = new Bundle();
                    bundle.putString("linkHref",myList.get(position));
                    videoFragment.setArguments(bundle);

                    adapter.addFragmentForward(videoFragment);

                    adapter.notifyDataSetChanged();
            }
            else if(isPhoto){

                    PhotoFragment photoFragment = new PhotoFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("linkHref",myList.get(position));
                    photoFragment.setArguments(bundle);

                    adapter.addFragmentForward(photoFragment);
                    adapter.notifyDataSetChanged();
            }
            else{
                    ArticlesFragmentViewPager articlesFragmentViewPager = new ArticlesFragmentViewPager();
                    Bundle bundle = new Bundle();
                    bundle.putString("linkHref",myList.get(position));
                    articlesFragmentViewPager.setArguments(bundle);
                    adapter.addFragmentForward(articlesFragmentViewPager);
                    adapter.notifyDataSetChanged();
             }
        }
    }

    private void initPagerListener(){

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.d(LOG_TAG, "onPageSelected " + position);

                if(adapter.getItem(position) instanceof VideoFragment){
                    Log.d(LOG_TAG, "Next fragment is video ");

                    VideoFragment videoFragment = (VideoFragment) adapter.getItem(position);
                    videoFragment.playVideo();
                }

                if(positionPage > position){//User Move to left
                    Log.d(LOG_TAG, "User Move to left ");
                  //  addFragmentBack(positionArticle - 2);
                  //  adapter.notifyDataSetChanged();
                }
               /* else if (positionPage < position){ //User Move to right
                    Log.d(LOG_TAG, "User Move to right");
                    addFragment(position);
                    adapter.notifyDataSetChanged();
                }

                positionPage = position;
*/

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    boolean checkForWord(String line, String word){
        return line.contains(word);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

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

        public void addFragmentForward(android.support.v4.app.Fragment fragment) {

            mFragmentList.add(fragment);

        }

        public void addFragmentBack(android.support.v4.app.Fragment fragment) {


            //make a loop to run through the array list
            for(int i = mFragmentList.size()-1; i > 0; i--)
            {
                //set the last element to the value of the 2nd to last element
                mFragmentList.set(i,mFragmentList.get(i-1));
            }

            mFragmentList.set(0,fragment);
        }

    }

}
