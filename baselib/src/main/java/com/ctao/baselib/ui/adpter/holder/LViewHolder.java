package com.ctao.baselib.ui.adpter.holder;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.StringRes;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctao.baselib.Global;

/**
 * Created by A Miracle on 2016/11/3.
 */
public class LViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private View mRootView;

    private LViewHolder(@LayoutRes int layoutId, int position, ViewGroup parent) {
        mPosition = position;
        mViews = new SparseArray<>();
        mRootView = LayoutInflater.from(Global.getContext()).inflate(layoutId, parent, false);
        mRootView.setTag(this);
    }

    public static LViewHolder get(View convertView, @LayoutRes int layoutId, int position, ViewGroup parent) {
        if (convertView == null) {
            return new LViewHolder(layoutId, position, parent);
        } else {
            LViewHolder viewHolder = (LViewHolder) convertView.getTag();
            viewHolder.mPosition = position;
            return viewHolder;
        }
    }

    public View getRootView() {
        return mRootView;
    }

    public int getPosition() {
        return mPosition;
    }
    /** 通过ViewId获取控件 */
    public <T extends View> T getView(@IdRes int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mRootView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> LViewHolder setImageBitmap(@IdRes int viewId, Bitmap bm) {
        T imageView = getView(viewId);
        imageView.setImageBitmap(bm);
        return this;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> LViewHolder setImageDrawable(@IdRes int viewId, Drawable drawable) {
        T imageView = getView(viewId);
        imageView.setImageDrawable(drawable);
        return this;
    }

    /** ImageView及子类专用 */
    public <T extends ImageView> LViewHolder setImageResource(@IdRes int viewId, @DrawableRes int resId) {
        T imageView = getView(viewId);
        imageView.setImageResource(resId);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> LViewHolder setText(@IdRes int viewId, CharSequence text) {
        T textView = getView(viewId);
        textView.setText(text);
        return this;
    }

    /** TextView及子类专用 */
    public <T extends TextView> LViewHolder setText(@IdRes int viewId, @StringRes int resId) {
        T textView = getView(viewId);
        textView.setText(resId);
        return this;
    }

    /** CheckBox及子类专用 */
    public <T extends CheckBox> LViewHolder setChecked(@IdRes int viewId, boolean checked) {
        T checkBox = getView(viewId);
        checkBox.setChecked(checked);
        return this;
    }

    public LViewHolder setBackgroundResource(@IdRes int viewId, @DrawableRes int resId) {
        getView(viewId).setBackgroundResource(resId);
        return this;
    }
}
