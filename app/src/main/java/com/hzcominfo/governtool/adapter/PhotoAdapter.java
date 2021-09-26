package com.hzcominfo.governtool.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.databinding.ItemPhotoBinding;
import java.io.File;

/**
 * Create by Ljw on 2020/11/19 16:38
 * https://github.com/donkingliang/ImageSelector
 * https://www.jianshu.com/p/bb0148a65820 -- 解决Android 10无法加载图片
 */
public class PhotoAdapter extends BaseAdapter<String> {

    private Activity context;
    private IItemDelete iItemDelete;

    public void setContext(Activity context){
        this.context = context;
    }
    public void setItemDelete(IItemDelete iItemDelete){
        this.iItemDelete = iItemDelete;
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
        if (position == 0) {
            binding.ivPhoto.setImageResource(R.mipmap.ic_add);
            binding.ivPreview.setVisibility(View.GONE);
            binding.ibDelete.setVisibility(View.GONE);
        } else {
            binding.ibDelete.setVisibility(View.VISIBLE);
            String src = dataList.get(position);
            String end = src.substring(src.length() - 3);
            if (end.equals("peg") || end.equals("jpg")){ //照片
                //适配Android10
                Bitmap bitmap = BitmapFactory.decodeFile(src);
                if (null != bitmap) {
                    binding.ivPhoto.setImageBitmap(bitmap);
                    binding.ivPreview.setVisibility(View.GONE);
                } else { //android10必须要通过此方式来加载图片
                    Uri imageUri = getImageContentUri(context, src);
                    if (imageUri != null){
                        binding.ivPhoto.setImageURI(imageUri);
                        binding.ivPreview.setVisibility(View.GONE);
                    } else { // TODO: 2020/12/8  本地图片被删除了如何处理
                        binding.ivPreview.setVisibility(View.GONE);
                    }
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

    /**
     * 将图片路径转换成uri —— 视频Android10
     */
    private static Uri getImageContentUri(Context context, String path) {
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID }, MediaStore.Images.Media.DATA + "=? ",
                new String[] { path }, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            // 如果图片不在手机的共享图片数据库，就先把它插入。
            if (new File(path).exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, path);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

}
