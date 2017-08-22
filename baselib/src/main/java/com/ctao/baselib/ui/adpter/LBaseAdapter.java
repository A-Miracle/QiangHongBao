package com.ctao.baselib.ui.adpter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A Miracle on 2016/11/30.
 */
public abstract class LBaseAdapter<T> extends BaseAdapter {

    protected List<T> mList = new ArrayList<>();

    public List<T> getData(){
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        return getView(type, position, convertView, parent);
    }

    public abstract View getView(int type, int position, View convertView, ViewGroup parent);
}
