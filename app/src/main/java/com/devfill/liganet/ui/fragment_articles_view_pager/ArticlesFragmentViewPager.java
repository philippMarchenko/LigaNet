package com.devfill.liganet.ui.fragment_articles_view_pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devfill.liganet.R;
import com.devfill.liganet.adapter.MoreNewsAdapter;
import com.devfill.liganet.model.ListNews;
import com.devfill.liganet.model.MoreNews;
import com.devfill.liganet.model.NewsContent;
import com.devfill.liganet.network.ServerAPI;
import com.devfill.liganet.ui.activity.ArticleNewsActivity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ArticlesFragmentViewPager extends android.support.v4.app.Fragment{

    private static final String LOG_TAG = "ArticlesViewPager";

    private TextView dateAtricle;
    private TextView anotation;
    private TextView textArticle;;
    private ImageView backdrop;
    private ProgressBar progressArticle;

    private Retrofit retrofit;
    private ServerAPI serverAPI;
    private Target loadtargetArrayImage = null;

    private Map<String, Drawable> drawableHashMap = new HashMap<String, Drawable>();
    private List<String> imageUrls = new ArrayList<>();

    private int count_bitmap = 0;
    private NewsContent newsContent;
    private Html.ImageGetter igLoader;

    private String link;

    private List<MoreNews.Article> articleList = new ArrayList<>();
    private RecyclerView recyclerView;
    private MoreNewsAdapter moreNewsAdapter;
    private LinearLayout read_more;
    private TextView readMoreTV;

    private MoreNewsHelper moreNewsHelper;

    NestedScrollView neestedscroll;
    AppBarLayout.Behavior behavior;
    AppBarLayout appbar;
    CoordinatorLayout coordinatorlayout;
    View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_article_news, container, false);

        Log.d(LOG_TAG,"onCreateView ");

        read_more = (LinearLayout) rootView.findViewById(R.id.read_more);

        readMoreTV = (TextView) rootView.findViewById(R.id.readMoreTV);
        dateAtricle = (TextView) rootView.findViewById(R.id.dateAtricle);
        anotation = (TextView) rootView.findViewById(R.id.anotation);
        textArticle = (TextView) rootView.findViewById(R.id.textArticle);
        backdrop = (ImageView) rootView.findViewById(R.id.backdrop);
        progressArticle = (ProgressBar) rootView.findViewById(R.id.progressArticle);
        coordinatorlayout = (CoordinatorLayout) rootView.findViewById(R.id.coordinatorlayout);

        neestedscroll = (NestedScrollView) rootView.findViewById(R.id.neestedscroll);
        appbar = (AppBarLayout) rootView.findViewById(R.id.appbar);

        Typeface typefaceRI = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-RI.ttf");
        Typeface typefaceR = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-R.ttf");
        Typeface typefaceB = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/UbuntuMono-B.ttf");

        readMoreTV.setTypeface(typefaceR);
        anotation.setTypeface(typefaceB);
        textArticle.setTypeface(typefaceR);
        dateAtricle.setTypeface(typefaceRI);

        dateAtricle.setVisibility(View.INVISIBLE);
        anotation.setVisibility(View.INVISIBLE);
        textArticle.setVisibility(View.INVISIBLE);
        progressArticle.setVisibility(View.VISIBLE);
        read_more.setVisibility(View.INVISIBLE);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_more_news);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        moreNewsAdapter = new MoreNewsAdapter(getContext(),getActivity(),articleList,recyclerView);


        moreNewsAdapter.setOnItemClick(new MoreNewsAdapter.OnItemClick() {
            @Override
            public void onItemClickListener(String href) {

                getNewsContent(href);
                hideAllViews();
            }
        });

        recyclerView.setAdapter(moreNewsAdapter);

        link = getArguments().getString("linkHref");

        initRetrofit();
        initTargetPicassoArrayImage();
        initIloader();

        moreNewsHelper = new MoreNewsHelper(articleList,getContext());
        moreNewsHelper.getImagesObservable().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver() {
                    @Override
                    public void onNext(Object value) {
                        moreNewsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Throwable e) {
                        //Handle error
                    }

                    @Override
                    public void onComplete() {

                    }
                });


        return rootView;
    }

    public void expandToolbar(){
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) appbar.getLayoutParams();
        behavior = (AppBarLayout.Behavior) params.getBehavior();
        if(behavior!=null) {
            behavior.setTopAndBottomOffset(0);
            behavior.onNestedPreScroll(coordinatorlayout, appbar, null, 0, 1, new int[2]);
        }
    }

    void hideAllViews(){

        dateAtricle.setVisibility(View.INVISIBLE);
        anotation.setVisibility(View.INVISIBLE);
        textArticle.setVisibility(View.INVISIBLE);
        progressArticle.setVisibility(View.VISIBLE);
        read_more.setVisibility(View.INVISIBLE);
    }

    private void getNewsContent (final String linkHref){

        String netType = getNetworkType(getContext());
        if(netType == null){
            Toast.makeText(getContext(), "Подключение к сети отсутствует!", Toast.LENGTH_LONG).show();
        }
        else {
            try {

                serverAPI.getNewsContent(linkHref).enqueue(new Callback<NewsContent>() {
                    @Override
                    public void onResponse(Call<NewsContent> call, Response<NewsContent> response) {

                        //Log.i(LOG_TAG, "onResponse getNewsContent ");

                        newsContent = response.body();

                        try {

                            imageUrls = newsContent.getUrls();  //получим список адресов картинок в самой статье

                            if (newsContent.getUrls().size() > 0) { //если они есть

                                  dateAtricle.setText(newsContent.getData().getDate());                 //покажем дату
                                  anotation.setText(Html.fromHtml(newsContent.getData().getTitle()));   //покажем аннотацию
                                  loadImageInToolbar(newsContent.getData().getImageUrl());              //запустим загрузку картинки тулбара
                                  loadNextImage();
                            }
                            else{

                                dateAtricle.setText(newsContent.getData().getDate());
                                anotation.setText(Html.fromHtml(newsContent.getData().getTitle()));
                                textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));    //покажем текст
                                loadImageInToolbar(newsContent.getData().getImageUrl());                //запустим загрузку картинки тулбара
                            }

                            progressArticle.setVisibility(View.INVISIBLE);  //покажем все наши елементы
                            dateAtricle.setVisibility(View.VISIBLE);
                            anotation.setVisibility(View.VISIBLE);
                            textArticle.setVisibility(View.VISIBLE);

                            getMoreNews(linkHref);                  //запрос статей "читайте также"
                            neestedscroll.scrollTo(0, 0);           //переместим фокус neestedscroll в начало
                            expandToolbar();                        //развернем тулбар
                        }
                        catch(Exception e){

                            Toast.makeText(getContext(), "Не удалось распознать статью!", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<NewsContent> call, Throwable t) {
                              progressArticle.setVisibility(View.INVISIBLE);
                        Log.i(LOG_TAG, "onFailure. Ошибка REST запроса getNewsContent " + t.toString());
                    }
                });
            } catch (Exception e) {

                Log.i(LOG_TAG, "Ошибка REST запроса к серверу  getNewsContent " + e.getMessage());
            }
        }
    }

    private void getMoreNews(final String linkHref){

        serverAPI.getMoreNews("http://api.mkdeveloper.ru/liga_net/get_more_content_article.php?link_href=" + linkHref).enqueue(new Callback<MoreNews>() {
            @Override
            public void onResponse(Call<MoreNews> call, Response<MoreNews> response) {

                MoreNews moreNews = response.body();

                  if(moreNews.getArticleList().size() > 0){
                      articleList.clear();                              //очистим предыдущие статьи
                      articleList.addAll(moreNews.getArticleList());    //добавим полученые с сервера
                      moreNewsAdapter.notifyDataSetChanged();           //обновим адаптер
                      read_more.setVisibility(View.VISIBLE);            //покажем пользователю

                      moreNewsHelper.loadNextImage(moreNews.getArticleList());  //запустим процес загрузки картинок.передав массим статей
                  }
            }

            @Override
            public void onFailure(Call<MoreNews> call, Throwable t) {
                Log.i(LOG_TAG, "onFailure. Ошибка getMoreNews " + t.toString());

            }
        });

    }

    private void initRetrofit (){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(ServerAPI.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        serverAPI = retrofit.create(ServerAPI.class);
    }

    private void loadNextImage(){
        Log.i(LOG_TAG, "loadNextImage  count_bitmap " + count_bitmap);

        if(count_bitmap == imageUrls.size()) {
            Log.i(LOG_TAG, "Загрузили все картинки ");
            count_bitmap = 0;
        }
        else{
            try {
                Picasso.with(getContext()).load(imageUrls.get(count_bitmap)).into(loadtargetArrayImage);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;
            }
        }

    }

    private void initTargetPicassoArrayImage(){

        loadtargetArrayImage = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                //   Log.d(LOG_TAG, "onBitmapLoaded  ");

                try{

                    if (imageUrls.size() > 0) { //это качаются картинки в саму статью

                        Drawable drawable = new BitmapDrawable(getResources(), bitmap); //преобр из bitmap в drawable
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                                drawable.getIntrinsicHeight());
                        drawableHashMap.put(imageUrls.get(count_bitmap),drawable);  //ложим в хешмап ,ключ это ссылка и картинка это значение

                        count_bitmap++;                                             //счетчик загруженых картинок
                        loadNextImage();                                            //запуск загрузки следующей картинки

                        textArticle.setVisibility(View.VISIBLE);                    //сделаем все видимым
                        anotation.setVisibility(View.VISIBLE);
                        progressArticle.setVisibility(View.INVISIBLE);

                        textArticle.setText(Html.fromHtml(newsContent.getData().getText(), igLoader, null));    //покажем текст передав туда лоадер картинок в ШТМЛ
                    }
                }
                catch (IllegalStateException e){
                       Log.d(LOG_TAG, "ошибка загрузки картинки   " + e.getMessage());
                }
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }

        };
    }

    private void loadImageInToolbar(String url){

        try {

            final float scale = getContext().getResources().getDisplayMetrics().density;
            int height = (int) (220 * scale + 0.5f);
            int width = (int) (295 * scale + 0.5f);

            Picasso.with(getContext()).load(url).resize(width,height).into(backdrop);    //загружаем картинку в тулбар
        }
        catch (Exception e){
            Log.d(LOG_TAG, "Error load image " + e.getMessage());
        }
    }

    private void initIloader(){

        igLoader = new Html.ImageGetter() {
            public Drawable getDrawable(String source) {

                Drawable drawable = null;

                for(int i = 0 ; i < imageUrls.size(); i++){     //переберем ссылки на картинки

                    if(source.equals(imageUrls.get(i))){        //если в статье есть ссылка на картинку
                        drawable = drawableHashMap.get(imageUrls.get(i));   //достаем ее из хешмапа
                    }
                }
                Log.d(LOG_TAG, "getDrawable  " + drawable);
                return drawable;
            }
        };
    }

    private String getNetworkType(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return activeNetwork.getTypeName();
        }
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();

        getNewsContent(link);

        Log.i(LOG_TAG, " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();

        onStop();
        Log.i(LOG_TAG, " onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.i(LOG_TAG, " onDestroy");
    }

}