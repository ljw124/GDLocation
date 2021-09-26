package com.hzcominfo.governtool.databinding;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityWalkLineBindingImpl extends ActivityWalkLineBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.map, 1);
        sViewsWithIds.put(R.id.ib_back, 2);
        sViewsWithIds.put(R.id.btn_finish, 3);
        sViewsWithIds.put(R.id.rl_gps, 4);
        sViewsWithIds.put(R.id.tv_satellite_num, 5);
        sViewsWithIds.put(R.id.iv_gps, 6);
        sViewsWithIds.put(R.id.tv_num, 7);
        sViewsWithIds.put(R.id.ib_control, 8);
        sViewsWithIds.put(R.id.ib_location, 9);
        sViewsWithIds.put(R.id.ll_walk_info, 10);
        sViewsWithIds.put(R.id.tv_time, 11);
        sViewsWithIds.put(R.id.tv_distance, 12);
        sViewsWithIds.put(R.id.rl_tab, 13);
        sViewsWithIds.put(R.id.ll_marker, 14);
        sViewsWithIds.put(R.id.ll_voice, 15);
        sViewsWithIds.put(R.id.btn_photo, 16);
        sViewsWithIds.put(R.id.fl_camera_frame, 17);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityWalkLineBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 18, sIncludes, sViewsWithIds));
    }
    private ActivityWalkLineBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 1
            , (android.widget.Button) bindings[3]
            , (android.widget.Button) bindings[16]
            , (android.widget.FrameLayout) bindings[17]
            , (android.widget.ImageButton) bindings[2]
            , (android.widget.ImageButton) bindings[8]
            , (android.widget.ImageButton) bindings[9]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.LinearLayout) bindings[14]
            , (android.widget.LinearLayout) bindings[15]
            , (android.widget.LinearLayout) bindings[10]
            , (com.amap.api.maps.MapView) bindings[1]
            , (android.widget.RelativeLayout) bindings[4]
            , (android.widget.RelativeLayout) bindings[13]
            , (android.widget.TextView) bindings[12]
            , (android.widget.TextView) bindings[7]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[11]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x2L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
        if (BR.model == variableId) {
            setModel((com.hzcominfo.governtool.model.WalkModel) variable);
        }
        else {
            variableSet = false;
        }
            return variableSet;
    }

    public void setModel(@Nullable com.hzcominfo.governtool.model.WalkModel Model) {
        this.mModel = Model;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeModel((com.hzcominfo.governtool.model.WalkModel) object, fieldId);
        }
        return false;
    }
    private boolean onChangeModel(com.hzcominfo.governtool.model.WalkModel Model, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x1L;
            }
            return true;
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): null
    flag mapping end*/
    //end
}