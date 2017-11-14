package com.vn.fa.ui;

import android.support.v4.app.DialogFragment;

import com.vn.fa.net.RequestLoader;

/**
 * Created by binhbt on 6/22/2016.
 */
public abstract class RxDialogFragment extends DialogFragment{
    @Override
    public void onDestroyView() {
        RequestLoader.getDefault().cancelAll(this);
        super.onDestroyView();
    }
}
