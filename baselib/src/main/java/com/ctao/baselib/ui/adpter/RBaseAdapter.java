package com.ctao.baselib.ui.adpter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.ctao.baselib.ui.adpter.holder.RViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by A Miracle on 2016/12/1.
 */
public abstract class RBaseAdapter<T> extends RecyclerView.Adapter<RViewHolder>{
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;
    protected List<T> mList = new ArrayList<>();
    public List<T> getData() {
        return mList;
    }


    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }
    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public final RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateRViewHolder(parent, viewType);
    }

    public abstract RViewHolder onCreateRViewHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(final RViewHolder holder, final int position) {
        onBindRViewHolder(holder, position, getItemViewType(position));
        if(null != mOnItemClickListener){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(RBaseAdapter.this, holder, holder.itemView, position);
                }
            });
        }
        if(null != mOnItemLongClickListener){
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemLongClickListener.onItemLongClick(RBaseAdapter.this, holder, holder.itemView, position);
                    return true;
                }
            });
        }
    }

    public abstract void onBindRViewHolder(RViewHolder holder, int position, int viewType);

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.Adapter<RViewHolder> parent, RViewHolder holder, View view, int position);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(RecyclerView.Adapter<RViewHolder> parent, RViewHolder holder, View view, int position);
    }
}