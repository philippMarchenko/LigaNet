package com.devfill.liganet.network;

import android.content.Context;
import android.graphics.Bitmap;

import android.os.AsyncTask;
import android.util.Log;


public class GetArticleImage extends AsyncTask<String, Void, Bitmap> {

    private static final String LOG_TAG = "GetArticleImageTag";


    private IGetArticleImageListener mIGetArticleImageListener;
    private Context mContext;

    Bitmap mBitmap = null;


    public GetArticleImage(IGetArticleImageListener iGetArticleImageListener,Context context){

        mIGetArticleImageListener = iGetArticleImageListener;
        mContext = context;
    }

    @Override
    protected Bitmap doInBackground(String... params) {


        Log.d(LOG_TAG, "params = " + params[0]);

        if (params[0] != null) {

            final float scale = mContext.getResources().getDisplayMetrics().density;
            int height = (int) (100 * scale + 0.5f);
            int width = (int) (120 * scale + 0.5f);

       /*     try {
                bitmap = Glide
                        .with(mContext)
                        .asBitmap()
                        .load(params[0])
                        .into(50,50)
                        .get();

                Log.d(LOG_TAG, "image Loaded = ");


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }*/


        }
        return mBitmap;
    }
    public interface IGetArticleImageListener {
        public void onGetArticleImageFinished(Bitmap bitmap);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);

        mIGetArticleImageListener.onGetArticleImageFinished(bitmap);
    }
}