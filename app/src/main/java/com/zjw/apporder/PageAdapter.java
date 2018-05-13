package com.zjw.apporder;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class PageAdapter extends FragmentStatePagerAdapter {

    private List<String> tabNames;
    private List<MyFragemnt> fragments;
    private Fragment mCurrentPrimaryItem;

    public PageAdapter(FragmentManager fm, List<String> tabNames, List<MyFragemnt> fragments) {
        super(fm);
        this.tabNames = tabNames;
        this.fragments = fragments;
    }
    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }
    @Override
    public int getCount() {
        return fragments.size();
    }
    /**
    *这个函数就是给TabLayout的Tab设定Title
    */
    @Override
    public CharSequence getPageTitle(int position) {
        return tabNames.get(position);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container,position,object);
        Fragment fragment = (Fragment)object;
        mCurrentPrimaryItem = fragment;
    }

    public Fragment getCurrentPrimaryItem() {
        return mCurrentPrimaryItem;
    }
}