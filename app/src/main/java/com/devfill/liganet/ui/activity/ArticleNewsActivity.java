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

        mPager = (ViewPager) findViewById(R.id.pagerArticles);  //общий пайджер для всех статей

        myList = (ArrayList<String>) getIntent().getSerializableExtra("newsList");  //принимаем список ссылок
        positionArticle = getIntent().getIntExtra("position",0);                    //принимаем позицию сттьи по которой кликнули

        adapter = new ViewPagerAdapter(getSupportFragmentManager());                //создание адаптера для статей

        for(int i = 0; i <= myList.size(); i++){                                    //перебираем все ссылки и добавляем в адаптер

            addFragment(i);
        }

        mPager.setAdapter(adapter);                                   //присвоим адаптер для пейджера
        mPager.setCurrentItem(positionArticle);                       //откроем правильную статью
        mPager.setPageTransformer(true, new DepthPageTransformer());  //тип анимации свайпа статей в пейджере

    }

    private void checkPhotoVideo(int position){

        if(checkForWord(myList.get(position),"/photo/")){   //если в ссылке есть это слово то это фото

            isPhoto = true;
        }
        else{

            isPhoto = false;
        }

        if(checkForWord(myList.get(position),"/video/") || checkForWord(myList.get(position),"/videomaterialy/")){

            isVideo = true;                      //если в ссылке есть это слово то это видео
        }
        else{

            isVideo = false;
        }
    }

    private void addFragment(int position){

        if((position < myList.size())){

            checkPhotoVideo(position);     //проверим на тип статьи

            if(isVideo){

                    VideoFragment videoFragment = new VideoFragment();      //создадим фрагмент
                    Bundle bundle = new Bundle();
                    bundle.putString("linkHref",myList.get(position));      //передадим ему ссылку
                    videoFragment.setArguments(bundle);

                    adapter.addFragmentForward(videoFragment);              //обновим адаптер
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

    }

}
