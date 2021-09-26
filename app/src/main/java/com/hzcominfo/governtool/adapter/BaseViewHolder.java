package com.hzcominfo.governtool.adapter;

import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by Ljw on 2020/11/18 18:43
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {

    public ViewDataBinding binding;

    public BaseViewHolder(ViewDataBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }
}
