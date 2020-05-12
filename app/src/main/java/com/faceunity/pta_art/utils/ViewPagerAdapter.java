package com.faceunity.pta_art.utils;


import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public class ViewPagerAdapter extends PagerAdapter {

    private List<View> mViews;
    private List<String> mTitles;

    public ViewPagerAdapter(List<View> mViews, List<String> mTitles) {
        this.mViews = mViews;
        this.mTitles = mTitles;
    }

    public List<View> getmViews() {
        return mViews;
    }

    public void setmViews(List<View> mViews) {
        this.mViews = mViews;
    }

    public List<String> getmTitles() {
        return mTitles;
    }

    public void setmTitles(List<String> mTitles) {
        this.mTitles = mTitles;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        container.addView(mViews.get(position));
        return mViews.get(position);
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView(mViews.get(position));
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.size() > 0 ? mTitles.get(position) : "";
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object obj) {
        return view == obj;
    }

}
