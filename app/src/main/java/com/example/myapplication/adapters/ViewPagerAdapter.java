package com.example.myapplication.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.example.myapplication.module.TagSaver;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment createdFrag = (Fragment) super.instantiateItem(container, position);
        switch (position) {
            case 0:
                String firstTag = createdFrag.getTag();
                TagSaver.setTag1(firstTag);
                break;
            case 1:
                String secondTag = createdFrag.getTag();
                TagSaver.setTag2(secondTag);
                break;
            case 2:
                String thirdTag = createdFrag.getTag();
                TagSaver.setTag3(thirdTag);
                break;
        }
        return createdFrag;

    }
}