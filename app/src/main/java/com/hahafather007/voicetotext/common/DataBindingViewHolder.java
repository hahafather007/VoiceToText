package com.hahafather007.voicetotext.common;

import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;

public class DataBindingViewHolder<T extends ViewDataBinding> extends RecyclerView.ViewHolder {
    public final T dataBinding;

    public DataBindingViewHolder(T binding) {
        super(binding.getRoot());

        dataBinding = binding;
    }
}
