// Generated by data binding compiler. Do not edit!
package com.hzcominfo.governtool.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.amap.api.maps.MapView;
import com.hzcominfo.governtool.R;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ActivityMarkerEditBinding extends ViewDataBinding {
  @NonNull
  public final Button btnSave;

  @NonNull
  public final EditText etDescribe;

  @NonNull
  public final ImageButton ibBack;

  @NonNull
  public final ImageView ivPicture;

  @NonNull
  public final LinearLayout llPosition;

  @NonNull
  public final MapView map;

  @NonNull
  public final RelativeLayout rlTitle;

  @NonNull
  public final RecyclerView rvAudio;

  @NonNull
  public final RecyclerView rvPhoto;

  @NonNull
  public final TextView tvAddAudio;

  @NonNull
  public final TextView tvAudio;

  @NonNull
  public final TextView tvPositionInfo;

  protected ActivityMarkerEditBinding(Object _bindingComponent, View _root, int _localFieldCount,
      Button btnSave, EditText etDescribe, ImageButton ibBack, ImageView ivPicture,
      LinearLayout llPosition, MapView map, RelativeLayout rlTitle, RecyclerView rvAudio,
      RecyclerView rvPhoto, TextView tvAddAudio, TextView tvAudio, TextView tvPositionInfo) {
    super(_bindingComponent, _root, _localFieldCount);
    this.btnSave = btnSave;
    this.etDescribe = etDescribe;
    this.ibBack = ibBack;
    this.ivPicture = ivPicture;
    this.llPosition = llPosition;
    this.map = map;
    this.rlTitle = rlTitle;
    this.rvAudio = rvAudio;
    this.rvPhoto = rvPhoto;
    this.tvAddAudio = tvAddAudio;
    this.tvAudio = tvAudio;
    this.tvPositionInfo = tvPositionInfo;
  }

  @NonNull
  public static ActivityMarkerEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_marker_edit, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivityMarkerEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivityMarkerEditBinding>inflateInternal(inflater, R.layout.activity_marker_edit, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityMarkerEditBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_marker_edit, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivityMarkerEditBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivityMarkerEditBinding>inflateInternal(inflater, R.layout.activity_marker_edit, null, false, component);
  }

  public static ActivityMarkerEditBinding bind(@NonNull View view) {
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
  public static ActivityMarkerEditBinding bind(@NonNull View view, @Nullable Object component) {
    return (ActivityMarkerEditBinding)bind(component, view, R.layout.activity_marker_edit);
  }
}
