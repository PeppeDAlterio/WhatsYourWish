package com.peppedalterio.whatsyourwish.activity.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.peppedalterio.whatsyourwish.activity.fragment.ContactsListFragment;
import com.peppedalterio.whatsyourwish.activity.fragment.MyWishlistFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionsPageAdapter extends FragmentPagerAdapter {

    private static final int FRAGMENTS_NUMBER = 2;
    //private final List<Fragment> fragmentList = new ArrayList<>();
    private final List<String> fragmentTitleList = new ArrayList<>();

    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    /*public void addFragment(Fragment fragment, String title) {
        fragmentList.add(fragment);
        fragmentTitleList.add(title);
    }*/

    public void addFragmentTitle(int pos, String title) {
        fragmentTitleList.add(pos, title);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int i) {
        return fragmentTitleList.get(i);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return ContactsListFragment.newInstance();
                //break;
            case 1:
                return MyWishlistFragment.newInstance();
                //break;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return FRAGMENTS_NUMBER;
    }
}
