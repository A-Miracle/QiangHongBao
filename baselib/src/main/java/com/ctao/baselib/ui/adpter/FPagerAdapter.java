package com.ctao.baselib.ui.adpter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ctao.baselib.ui.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A Miracle on 2016/12/1.
 * 该类内的每一个生成的 Fragment 都将保存在内存之中，
 * 因此适用于那些相对静态的页，数量也比较少的那种；
 * 如果需要处理有很多页，并且数据动态性较大、占用内存较多的情况，
 * 应该使用FragmentStatePagerAdapter。
 */
public class FPagerAdapter extends FragmentPagerAdapter {

    protected List<BaseFragment> mFragments = new ArrayList<>();
    protected List<String> mTitles = new ArrayList<>();

    public FPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public List<BaseFragment> getFragments(){
        return mFragments;
    }

    public List<String> getTitles(){
        return mTitles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(mTitles.size() > position){
            return mTitles.get(position);
        }
        return "";
    }
}
