package com.hzcominfo.governtool.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.AudioBean;
import com.hzcominfo.governtool.custom.audio.MediaManager;
import com.hzcominfo.governtool.databinding.ItemAudioBinding;

/**
 * Create by Ljw on 2020/11/26 15:15
 */
public class AudioRecordAdapter extends BaseAdapter<AudioBean> {

    //item 最小最大值
    private int mMinItemWidth;
    private int mMaxIItemWidth;
    private Activity context;
    private View mAnimView;
    private IItemDelete iItemDelete;
    private boolean isEdit;

    public void setContext(Activity context, boolean isEdit){
        this.context = context;
        this.isEdit = isEdit;
    }
    public void setItemDelete(IItemDelete iItemDelete){
        this.iItemDelete = iItemDelete;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup viewGroup, int viewType) {
        ItemAudioBinding binding =  DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.item_audio, viewGroup, false);
        //获取屏幕宽度
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);

        //item 设定最小最大值
        mMinItemWidth = (int) (outMetrics.widthPixels * 0.19f);
        mMaxIItemWidth = (int) (outMetrics.widthPixels * 0.6f);
        return new BaseViewHolder(binding);
    }

    @Override
    public void onBindMyViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        viewHolder.setIsRecyclable(false);
        BaseViewHolder baseViewHolder = (BaseViewHolder) viewHolder;
        ItemAudioBinding binding = (ItemAudioBinding) baseViewHolder.binding;
        AudioBean audioBean = dataList.get(position);
        //设置背景的宽度
        ViewGroup.LayoutParams lp = binding.rlAudio.getLayoutParams();
        lp.width = (int) (mMinItemWidth + (mMaxIItemWidth / 60f * audioBean.getTime()));
        //设置时间
        binding.tvTime.setText(Math.round(audioBean.getTime()) + "S");

        //设置语音播放
        binding.ivVolumePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果第一个动画正在运行， 停止第一个播放其他的
                if (mAnimView != null) {
                    mAnimView.setBackgroundResource(R.mipmap.ic_voice_play);
                    mAnimView = null;
                }
                //播放动画
                mAnimView = binding.ivVolumePlay;
                mAnimView.setBackgroundResource(R.drawable.animation_audio_anim);
                AnimationDrawable animation = (AnimationDrawable) mAnimView.getBackground();
                animation.start();
                //播放音频  完成后改回原来的background
                MediaManager.playSound(audioBean.getFilePath(), new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mAnimView.setBackgroundResource(R.mipmap.ic_voice_play);
                    }
                });
            }
        });

        if (!isEdit){ //详情不显示删除按钮
            binding.ibDelete.setVisibility(View.GONE);
        }

        //删除语音
        binding.ibDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iItemDelete.delete(position);
            }
        });
    }

    public interface IItemDelete{
        void delete(int position);
    }
}
