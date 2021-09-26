package com.hzcominfo.governtool.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.databinding.ItemRouteInfoBinding;
import com.hzcominfo.governtool.utils.StringUtils;

/**
 * Create by Ljw on 2020/11/18 16:10
 */
public class LocationInfoAdapter extends BaseAdapter<MarkerBean> {

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        ItemRouteInfoBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_route_info, viewGroup, false);
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolder;
        ItemRouteInfoBinding binding = (ItemRouteInfoBinding) baseViewHolder.binding;
        MarkerBean markerBean = dataList.get(position);
        binding.tvName.setText(StringUtils.isEmpty(markerBean.getDescribe()) ? markerBean.getLatLngs(): markerBean.getDescribe());
        binding.tvTime.setText(markerBean.getRecordTime());
        if (markerBean.getIsNew()){
            binding.ivNew.setVisibility(View.VISIBLE);
        } else {
            binding.ivNew.setVisibility(View.GONE);
        }
    }
}
