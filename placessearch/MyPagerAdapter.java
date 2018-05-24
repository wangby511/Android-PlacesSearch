package com.example.wangboyuan.placessearch;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
    private List<String> titles;
    private List<Fragment> mFragments;
    private List<Integer> mIcons;

    public MyPagerAdapter(FragmentManager fm, List<String> mDatas, List<Fragment> fragments , List<Integer> iconList) {
        super(fm);
        this.titles = mDatas;
        this.mFragments = fragments;
        this.mIcons = iconList;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position % mFragments.size());
    }

}
