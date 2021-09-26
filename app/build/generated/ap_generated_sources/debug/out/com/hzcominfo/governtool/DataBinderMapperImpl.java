package com.hzcominfo.governtool;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.hzcominfo.governtool.databinding.ActivityMainBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityMarkerDetailBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityMarkerEditBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityMyBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityRecordBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityRouteDetailBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityTakePhotoBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityWalkFinishBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityWalkInfoBindingImpl;
import com.hzcominfo.governtool.databinding.ActivityWalkLineBindingImpl;
import com.hzcominfo.governtool.databinding.ItemAudioBindingImpl;
import com.hzcominfo.governtool.databinding.ItemPhotoBindingImpl;
import com.hzcominfo.governtool.databinding.ItemRouteInfoBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYMAIN = 1;

  private static final int LAYOUT_ACTIVITYMARKERDETAIL = 2;

  private static final int LAYOUT_ACTIVITYMARKEREDIT = 3;

  private static final int LAYOUT_ACTIVITYMY = 4;

  private static final int LAYOUT_ACTIVITYRECORD = 5;

  private static final int LAYOUT_ACTIVITYROUTEDETAIL = 6;

  private static final int LAYOUT_ACTIVITYTAKEPHOTO = 7;

  private static final int LAYOUT_ACTIVITYWALKFINISH = 8;

  private static final int LAYOUT_ACTIVITYWALKINFO = 9;

  private static final int LAYOUT_ACTIVITYWALKLINE = 10;

  private static final int LAYOUT_ITEMAUDIO = 11;

  private static final int LAYOUT_ITEMPHOTO = 12;

  private static final int LAYOUT_ITEMROUTEINFO = 13;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(13);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_main, LAYOUT_ACTIVITYMAIN);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_marker_detail, LAYOUT_ACTIVITYMARKERDETAIL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_marker_edit, LAYOUT_ACTIVITYMARKEREDIT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_my, LAYOUT_ACTIVITYMY);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_record, LAYOUT_ACTIVITYRECORD);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_route_detail, LAYOUT_ACTIVITYROUTEDETAIL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_take_photo, LAYOUT_ACTIVITYTAKEPHOTO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_walk_finish, LAYOUT_ACTIVITYWALKFINISH);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_walk_info, LAYOUT_ACTIVITYWALKINFO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.activity_walk_line, LAYOUT_ACTIVITYWALKLINE);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.item_audio, LAYOUT_ITEMAUDIO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.item_photo, LAYOUT_ITEMPHOTO);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.hzcominfo.governtool.R.layout.item_route_info, LAYOUT_ITEMROUTEINFO);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYMAIN: {
          if ("layout/activity_main_0".equals(tag)) {
            return new ActivityMainBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_main is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMARKERDETAIL: {
          if ("layout/activity_marker_detail_0".equals(tag)) {
            return new ActivityMarkerDetailBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_marker_detail is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMARKEREDIT: {
          if ("layout/activity_marker_edit_0".equals(tag)) {
            return new ActivityMarkerEditBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_marker_edit is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYMY: {
          if ("layout/activity_my_0".equals(tag)) {
            return new ActivityMyBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_my is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYRECORD: {
          if ("layout/activity_record_0".equals(tag)) {
            return new ActivityRecordBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_record is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYROUTEDETAIL: {
          if ("layout/activity_route_detail_0".equals(tag)) {
            return new ActivityRouteDetailBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_route_detail is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYTAKEPHOTO: {
          if ("layout/activity_take_photo_0".equals(tag)) {
            return new ActivityTakePhotoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_take_photo is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYWALKFINISH: {
          if ("layout/activity_walk_finish_0".equals(tag)) {
            return new ActivityWalkFinishBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_walk_finish is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYWALKINFO: {
          if ("layout/activity_walk_info_0".equals(tag)) {
            return new ActivityWalkInfoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_walk_info is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYWALKLINE: {
          if ("layout/activity_walk_line_0".equals(tag)) {
            return new ActivityWalkLineBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_walk_line is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMAUDIO: {
          if ("layout/item_audio_0".equals(tag)) {
            return new ItemAudioBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_audio is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMPHOTO: {
          if ("layout/item_photo_0".equals(tag)) {
            return new ItemPhotoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_photo is invalid. Received: " + tag);
        }
        case  LAYOUT_ITEMROUTEINFO: {
          if ("layout/item_route_info_0".equals(tag)) {
            return new ItemRouteInfoBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for item_route_info is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(3);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "model");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(13);

    static {
      sKeys.put("layout/activity_main_0", com.hzcominfo.governtool.R.layout.activity_main);
      sKeys.put("layout/activity_marker_detail_0", com.hzcominfo.governtool.R.layout.activity_marker_detail);
      sKeys.put("layout/activity_marker_edit_0", com.hzcominfo.governtool.R.layout.activity_marker_edit);
      sKeys.put("layout/activity_my_0", com.hzcominfo.governtool.R.layout.activity_my);
      sKeys.put("layout/activity_record_0", com.hzcominfo.governtool.R.layout.activity_record);
      sKeys.put("layout/activity_route_detail_0", com.hzcominfo.governtool.R.layout.activity_route_detail);
      sKeys.put("layout/activity_take_photo_0", com.hzcominfo.governtool.R.layout.activity_take_photo);
      sKeys.put("layout/activity_walk_finish_0", com.hzcominfo.governtool.R.layout.activity_walk_finish);
      sKeys.put("layout/activity_walk_info_0", com.hzcominfo.governtool.R.layout.activity_walk_info);
      sKeys.put("layout/activity_walk_line_0", com.hzcominfo.governtool.R.layout.activity_walk_line);
      sKeys.put("layout/item_audio_0", com.hzcominfo.governtool.R.layout.item_audio);
      sKeys.put("layout/item_photo_0", com.hzcominfo.governtool.R.layout.item_photo);
      sKeys.put("layout/item_route_info_0", com.hzcominfo.governtool.R.layout.item_route_info);
    }
  }
}
