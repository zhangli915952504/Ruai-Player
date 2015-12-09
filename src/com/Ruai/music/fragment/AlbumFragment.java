package com.Ruai.music.fragment;

import com.Ruai.music.R;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * * * * * * * * * * * * * * * * * * * * * * *
 * Created by zhaoyiding
 * Date: 15/10/16
 * * * * * * * * * * * * * * * * * * * * * * *
 **/
public class AlbumFragment extends BaseFragment {


    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container) {
        return inflater.inflate(R.layout.fragment_album, container, false);
    }

    @Override
    protected void onLoading() {

    }

    @Override
    protected void onLoadFinish() {

    }
}
