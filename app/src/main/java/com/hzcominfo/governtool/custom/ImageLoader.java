package com.hzcominfo.governtool.custom;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.lxj.xpopup.interfaces.XPopupImageLoader;

import java.io.File;

/**
 * Create by Ljw on 2020/11/23 17:47
 * https://github.com/li-xiaojun/XPopup/wiki/2.-内置的弹窗实现
 */
public class ImageLoader implements XPopupImageLoader {

    @Override
    public void loadImage(int position, @NonNull Object url, @NonNull ImageView imageView) {
        //必须指定Target.SIZE_ORIGINAL，否则无法拿到原图，就无法享用天衣无缝的动画
        Glide.with(imageView).load(url).apply(new RequestOptions().override(Target.SIZE_ORIGINAL)).into(imageView);
    }

    //必须实现这个方法，返回uri对应的缓存文件，可参照下面的实现，内部保存图片会用到。如果你不需要保存图片这个功能，可以返回null。
    @Override
    public File getImageFile(@NonNull Context context, @NonNull Object uri) {
        try {
            return Glide.with(context).downloadOnly().load(uri).submit().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
