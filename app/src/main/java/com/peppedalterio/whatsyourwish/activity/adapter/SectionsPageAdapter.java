package com.peppedalterio.whatsyourwish.activity.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private final List<WeakReference<Fragment>> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        fragmentList.add(new WeakReference<>(fragment));
        fragmentTitleList.add(title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        return fragmentTitleList.get(i);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i).get();
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
