package com.hzcominfo.governtool.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.databinding.ItemPhotoBinding;

/**
 * Create by Ljw on 2020/12/1 17:08
 */
public class DetailPhotoAdapter extends BaseAdapter<String> {

    private Activity context;

    public void setContext(Activity context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        ItemPhotoBinding binding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_photo, viewGroup, false);
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolder;
        ItemPhotoBinding binding = (ItemPhotoBinding) baseViewHolder.binding;
        // 动态设置条目布局的宽度
        DisplayMetrics outMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        int screenWidth = outMetrics.widthPixels;
        int width = screenWidth/3 - 48;
        ViewGroup.LayoutParams layoutParams = binding.clRoot.getLayoutParams();
        layoutParams.width = width;
        binding.clRoot.setLayoutParams(layoutParams);
        //设置数据
        binding.ibDelete.setVisibility(View.GONE);
        String src = dataList.get(position);
        String end = src.substring(src.length() - 3);
        if (end.equals("peg") || end.equals("jpg")){ //照片
            Bitmap bitmap = BitmapFactory.decodeFile(dataList.get(position));
            if (null != bitmap) {
                binding.ivPhoto.setImageBitmap(bitmap);
                binding.ivPreview.setVisibility(View.GONE);
            }
        } else if (end.equals("mp4")){ //视频
            //显示视频的第一帧
            MediaMetadataRetriever media = new MediaMetadataRetriever();
            media.setDataSource(src);
            Bitmap bitmap = media.getFrameAtTime();
            binding.ivPhoto.setImageBitmap(bitmap);
            binding.ivPreview.setVisibility(View.VISIBLE);
        }
    }
}
