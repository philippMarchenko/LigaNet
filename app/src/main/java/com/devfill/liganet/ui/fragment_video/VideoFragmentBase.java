package com.devfill.liganet.ui.fragment_video;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.liganet.R;

public class VideoFragmentBase extends Fragment {


    public String LOG_TAG = "PhotoFragmentBaseTag";
    FragmentTransaction ft;

    VideoListFragment videoListFragment = new VideoListFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_video_base, container, false);

        ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.container_video, videoListFragment);
        ft.commit();

        return  rootview;
    }

}
