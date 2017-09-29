package com.devfill.liganet.ui;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.devfill.liganet.R;


public class VideoFragment extends android.support.v4.app.Fragment{


    private static final String LOG_TAG = "PhotoFragmentTag";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);





        return rootView;
    }


}