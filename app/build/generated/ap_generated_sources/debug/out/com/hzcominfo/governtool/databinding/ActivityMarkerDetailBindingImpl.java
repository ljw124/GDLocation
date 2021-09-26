package com.hzcominfo.governtool.databinding;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMarkerDetailBindingImpl extends ActivityMarkerDetailBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.map, 1);
        sViewsWithIds.put(R.id.ib_back, 2);
        sViewsWithIds.put(R.id.btn_edit, 3);
        sViewsWithIds.put(R.id.tv_position_info, 4);
        sViewsWithIds.put(R.id.rv_photo, 5);
        sViewsWithIds.put(R.id.ll_describe, 6);
        sViewsWithIds.put(R.id.tv_title, 7);
        sViewsWithIds.put(R.id.tv_describe, 8);
        sViewsWithIds.put(R.id.tv_audio, 9);
        sViewsWithIds.put(R.id.rv_audio, 10);
        sViewsWithIds.put(R.id.iv_picture, 11);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMarkerDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 12, sIncludes, sViewsWithIds));
    }
    private ActivityMarkerDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[3]
            , (android.widget.ImageButton) bindings[2]
            , (android.widget.ImageView) bindings[11]
            , (android.widget.RelativeLayout) bindings[6]
            , (com.amap.api.maps.MapView) bindings[1]
            , (androidx.recyclerview.widget.RecyclerView) bindings[10]
            , (androidx.recyclerview.widget.RecyclerView) bindings[5]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[7]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
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
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
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
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}