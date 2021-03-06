package com.devfill.liganet.ui.fragment_photo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.devfill.liganet.R;

public class PhotoFragmentBase extends Fragment {


    public String LOG_TAG = "PhotoFragmentBaseTag";
    FragmentTransaction ft;

    PhotoListFragment photoListFragment = new PhotoListFragment();

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_photo_base, container, false);

        ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.container_photo, photoListFragment,"photoListFragment");
        ft.commit();

        return  rootview;
    }

    public void getPhotoList(){

        photoListFragment.getPhotoList();
    }

}
