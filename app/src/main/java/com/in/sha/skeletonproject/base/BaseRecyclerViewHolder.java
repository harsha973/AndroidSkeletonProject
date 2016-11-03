package com.in.sha.skeletonproject.base;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sreepolavarapu on 10/03/16.
 */
public class BaseRecyclerViewHolder extends RecyclerView.ViewHolder{

    protected BaseActivity mContext;

    protected BaseRecyclerViewHolder(BaseActivity context, ViewGroup parent, int layoutResID)
    {
        super(LayoutInflater.from(context).inflate(layoutResID, parent, false));
        mContext = context;
    }

    public View getItemView()
    {
        return itemView;
    }
}
