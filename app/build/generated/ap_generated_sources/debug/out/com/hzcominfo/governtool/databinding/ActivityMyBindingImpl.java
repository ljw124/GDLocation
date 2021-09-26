package com.hzcominfo.governtool.databinding;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityMyBindingImpl extends ActivityMyBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.rl_title, 1);
        sViewsWithIds.put(R.id.ib_back, 2);
        sViewsWithIds.put(R.id.btn_save, 3);
        sViewsWithIds.put(R.id.view, 4);
        sViewsWithIds.put(R.id.tv_auto_photo, 5);
        sViewsWithIds.put(R.id.rg_auto_photo, 6);
        sViewsWithIds.put(R.id.rb_yes, 7);
        sViewsWithIds.put(R.id.rb_no, 8);
        sViewsWithIds.put(R.id.tv_photo_interval, 9);
        sViewsWithIds.put(R.id.et_photo_interval, 10);
        sViewsWithIds.put(R.id.tv_collect_type, 11);
        sViewsWithIds.put(R.id.rg_collect_type, 12);
        sViewsWithIds.put(R.id.rb_walk, 13);
        sViewsWithIds.put(R.id.rb_drive, 14);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityMyBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 15, sIncludes, sViewsWithIds));
    }
    private ActivityMyBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.Button) bindings[3]
            , (android.widget.EditText) bindings[10]
            , (android.widget.ImageButton) bindings[2]
            , (android.widget.RadioButton) bindings[14]
            , (android.widget.RadioButton) bindings[8]
            , (android.widget.RadioButton) bindings[13]
            , (android.widget.RadioButton) bindings[7]
            , (android.widget.RadioGroup) bindings[6]
            , (android.widget.RadioGroup) bindings[12]
            , (android.widget.RelativeLayout) bindings[1]
            , (android.widget.TextView) bindings[5]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[9]
            , (android.view.View) bindings[4]
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