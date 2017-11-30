package com.devfill.liganet.ui.fragment_articles_view_pager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.devfill.liganet.model.MoreNews;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Created by Philipp on 29.11.2017.
 */

public class MoreNewsHelper {

    public static int count_bitmap = 0;

    private RecyclerView recyclerView;
    private List<MoreNews.Article> articleList;
    private Target loadtarget = null;
    private Picasso picasso;

    private int height = 90;
    private int width = 120;

    private Context context;

    private static final String LOG_TAG = "MoreNewsHelper";


    public MoreNewsHelper(List<MoreNews.Article> articleList, Context context){

        this.articleList = articleList;
        this.context = context;
        picasso = Picasso.with(context);

        try{

            final float scale = context.getResources().getDisplayMetrics().density;
            height = (int) (90 * scale + 0.5f);
            width = (int) (120 * scale + 0.5f);
        }
        catch(Exception e){

            Log.d(LOG_TAG, "Не удалось загрузить ресурсы " + e.getMessage());

        }
    }

    public void loadNextImage(List<MoreNews.Article> articleList){


        this.articleList = articleList;

        if(count_bitmap == articleList.size()) {
            count_bitmap = 0;
        }
        else{

            try {
                picasso.load(this.articleList.get(count_bitmap).getImgUrl()).
                        tag("load").
                        resize(width, height).
                        into(loadtarget);
            }
            catch (Exception e){
                Log.d(LOG_TAG, "Error load image " + e.getMessage());
                count_bitmap++;

                picasso.load(this.articleList.get(count_bitmap).getImgUrl()).
                        tag("load").
                        resize(width, height).
                        into(loadtarget);
            }
        }

    }


    public Observable getImagesObservable() {

        Observable downloadObservable = Observable.create(new ObservableOnSubscribe() {
            @Override
            public void subscribe(final ObservableEmitter emitter) throws Exception {

                loadtarget = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        // Log.d(LOG_TAG, "onBitmapLoaded  ");

                        if (articleList.size() > 0) {
                            articleList.get(count_bitmap).setBitmap(bitmap);
                            // newsAdapter.notifyDataSetChanged();

                            count_bitmap++;
                            loadNextImage(articleList);
                            emitter.onNext(bitmap);
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                        count_bitmap++;
                        loadNextImage(articleList);
                        // Log.d(LOG_TAG, "onBitmapFailed " + errorDrawable.toString());

                    }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };

            }
        });

        return downloadObservable;
    }

}
