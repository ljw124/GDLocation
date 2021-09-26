// Generated by data binding compiler. Do not edit!
package com.hzcominfo.governtool.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.Bindable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.model.WalkInfoModel;
import java.lang.Deprecated;
import java.lang.Object;

public abstract class ActivityWalkInfoBinding extends ViewDataBinding {
  @NonNull
  public final ImageButton ibReturn;

  @NonNull
  public final LinearLayout llNoInfo;

  @NonNull
  public final RecyclerView rvInfo;

  @NonNull
  public final TabLayout tabInfo;

  @Bindable
  protected WalkInfoModel mModel;

  protected ActivityWalkInfoBinding(Object _bindingComponent, View _root, int _localFieldCount,
      ImageButton ibReturn, LinearLayout llNoInfo, RecyclerView rvInfo, TabLayout tabInfo) {
    super(_bindingComponent, _root, _localFieldCount);
    this.ibReturn = ibReturn;
    this.llNoInfo = llNoInfo;
    this.rvInfo = rvInfo;
    this.tabInfo = tabInfo;
  }

  public abstract void setModel(@Nullable WalkInfoModel model);

  @Nullable
  public WalkInfoModel getModel() {
    return mModel;
  }

  @NonNull
  public static ActivityWalkInfoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot) {
    return inflate(inflater, root, attachToRoot, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_walk_info, root, attachToRoot, component)
   */
  @NonNull
  @Deprecated
  public static ActivityWalkInfoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup root, boolean attachToRoot, @Nullable Object component) {
    return ViewDataBinding.<ActivityWalkInfoBinding>inflateInternal(inflater, R.layout.activity_walk_info, root, attachToRoot, component);
  }

  @NonNull
  public static ActivityWalkInfoBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, DataBindingUtil.getDefaultComponent());
  }

  /**
   * This method receives DataBindingComponent instance as type Object instead of
   * type DataBindingComponent to avoid causing too many compilation errors if
   * compilation fails for another reason.
   * https://issuetracker.google.com/issues/116541301
   * @Deprecated Use DataBindingUtil.inflate(inflater, R.layout.activity_walk_info, null, false, component)
   */
  @NonNull
  @Deprecated
  public static ActivityWalkInfoBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable Object component) {
    return ViewDataBinding.<ActivityWalkInfoBinding>inflateInternal(inflater, R.layout.activity_walk_info, null, false, component);
  }

  public static ActivityWalkInfoBinding bind(@NonNull View view) {
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
  public static ActivityWalkInfoBinding bind(@NonNull View view, @Nullable Object component) {
    return (ActivityWalkInfoBinding)bind(component, view, R.layout.activity_walk_info);
  }
}
