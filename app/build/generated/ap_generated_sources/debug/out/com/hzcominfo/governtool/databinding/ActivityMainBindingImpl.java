package com.hzcominfo.governtool.databinding;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMainBindingImpl extends ActivityMainBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.map, 1);
        sViewsWithIds.put(R.id.ib_person, 2);
        sViewsWithIds.put(R.id.iv_new, 3);
        sViewsWithIds.put(R.id.ib_info, 4);
        sViewsWithIds.put(R.id.tv_satellite_num, 5);
        sViewsWithIds.put(R.id.iv_gps, 6);
        sViewsWithIds.put(R.id.ib_location, 7);
        sViewsWithIds.put(R.id.rl_tab, 8);
        sViewsWithIds.put(R.id.ll_path, 9);
        sViewsWithIds.put(R.id.ll_marker, 10);
        sViewsWithIds.put(R.id.ll_voice, 11);
        sViewsWithIds.put(R.id.btn_photo, 12);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMainBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 13, sIncludes, sViewsWithIds));
    }
    private ActivityMainBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[12]
            , (android.widget.ImageButton) bindings[4]
            , (android.widget.ImageButton) bindings[7]
            , (android.widget.ImageButton) bindings[2]
            , (android.widget.ImageView) bindings[6]
            , (android.widget.ImageView) bindings[3]
            , (android.widget.LinearLayout) bindings[10]
            , (android.widget.LinearLayout) bindings[9]
            , (android.widget.LinearLayout) bindings[11]
            , (com.amap.api.maps.MapView) bindings[1]
            , (android.widget.RelativeLayout) bindings[8]
            , (android.widget.TextView) bindings[5]
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