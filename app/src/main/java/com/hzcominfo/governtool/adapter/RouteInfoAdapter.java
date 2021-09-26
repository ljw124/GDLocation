package com.hzcominfo.governtool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.databinding.ItemRouteInfoBinding;

/**
 * Create by Ljw on 2020/11/18 17:19
 */
public class RouteInfoAdapter extends BaseAdapter<PolylineBean> {

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        ItemRouteInfoBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_route_info, viewGroup, false);
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolder;
        ItemRouteInfoBinding binding = (ItemRouteInfoBinding) baseViewHolder.binding;
        PolylineBean polylineBean = dataList.get(position);
        binding.tvName.setText(polylineBean.getPathName());
        binding.tvTime.setText(polylineBean.getRecordTime());
        if (polylineBean.getIsNew()!=null && polylineBean.getIsNew()){
            binding.ivNew.setVisibility(View.VISIBLE);
        } else {
            binding.ivNew.setVisibility(View.GONE);
        }
    }
}
