// Generated by data binding compiler. Do not edit!
package com.hzcominfo.governtool.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import com.hzcominfo.governtool.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ItemPhotoBinding extends ViewDataBinding {
  @NonNull
  public final ConstraintLayout clRoot;

  @NonNull
  public final ImageButton ibDelete;

  @NonNull
  public final ImageView ivPhoto;

  @NonNull
  public final ImageView ivPreview;

  protected ItemPhotoBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ConstraintLayout clRoot, ImageButton ibDelete, ImageView ivPhoto, ImageView ivPreview) {
    super(_bindingComponent, _root, _localFieldCount);
    this.clRoot = clRoot;
    this.ibDelete = ibDelete;
    this.ivPhoto = ivPhoto;
    this.ivPreview = ivPreview;
  }

  @NonNull
  public static ItemPhotoBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root,
      boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.item_photo, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ItemPhotoBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup root,
      boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ItemPhotoBinding>inflateInternal(inflater, R.layout.item_photo, root, attachToRoot, component);
  }

  @NonNull
  public static ItemPhotoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.item_photo, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ItemPhotoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ItemPhotoBinding>inflateInternal(inflater, R.layout.item_photo, null, false, component);
  }

  public static ItemPhotoBinding bind(@NonNull View view) {
    return bind(view, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.bind(view, component)
   */
  @Deprecated
  public static ItemPhotoBinding bind(@NonNull View view, @Nullable Object component) {
    return (ItemPhotoBinding)bind(component, view, R.layout.item_photo);
  }
}
