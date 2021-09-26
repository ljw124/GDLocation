package com.hzcominfo.governtool.databinding;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class ActivityWalkFinishBindingImpl extends ActivityWalkFinishBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.iv_icon, 5);
        sViewsWithIds.put(R.id.tv_finish, 6);
        sViewsWithIds.put(R.id.btn_look_over, 7);
        sViewsWithIds.put(R.id.btn_again, 8);
    }
    // views
    @NonNull
    private final androidx.constraintlayout.widget.ConstraintLayout mboundView0;
    @NonNull
    private final android.widget.TextView mboundView1;
    @NonNull
    private final android.widget.TextView mboundView2;
    @NonNull
    private final android.widget.TextView mboundView3;
    @NonNull
    private final android.widget.TextView mboundView4;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public ActivityWalkFinishBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 9, sIncludes, sViewsWithIds));
    }
    private ActivityWalkFinishBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 5
            , (android.widget.Button) bindings[8]
            , (android.widget.Button) bindings[7]
            , (android.widget.ImageView) bindings[5]
            , (android.widget.TextView) bindings[6]
            );
        this.mboundView0 = (androidx.constraintlayout.widget.ConstraintLayout) bindings[0];
        this.mboundView0.setTag(null);
        this.mboundView1 = (android.widget.TextView) bindings[1];
        this.mboundView1.setTag(null);
        this.mboundView2 = (android.widget.TextView) bindings[2];
        this.mboundView2.setTag(null);
        this.mboundView3 = (android.widget.TextView) bindings[3];
        this.mboundView3.setTag(null);
        this.mboundView4 = (android.widget.TextView) bindings[4];
        this.mboundView4.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x20L;
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
        updateRegistration(0, Model);
        this.mModel = Model;
        synchronized(this) {
            mDirtyFlags |= 0x1L;
        }
        notifyPropertyChanged(BR.model);
        super.requestRebind();
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
            case 0 :
                return onChangeModel((com.hzcominfo.governtool.model.WalkModel) object, fieldId);
            case 1 :
                return onChangeModelWalkName((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 2 :
                return onChangeModelWalkDistance((androidx.databinding.ObservableField<java.lang.Double>) object, fieldId);
            case 3 :
                return onChangeModelRecordTime((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
            case 4 :
                return onChangeModelWalkTime((androidx.databinding.ObservableField<java.lang.String>) object, fieldId);
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
    private boolean onChangeModelWalkName(androidx.databinding.ObservableField<java.lang.String> ModelWalkName, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x2L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeModelWalkDistance(androidx.databinding.ObservableField<java.lang.Double> ModelWalkDistance, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x4L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeModelRecordTime(androidx.databinding.ObservableField<java.lang.String> ModelRecordTime, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x8L;
            }
            return true;
        }
        return false;
    }
    private boolean onChangeModelWalkTime(androidx.databinding.ObservableField<java.lang.String> ModelWalkTime, int fieldId) {
        if (fieldId == BR._all) {
            synchronized(this) {
                    mDirtyFlags |= 0x10L;
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
        com.hzcominfo.governtool.model.WalkModel model = mModel;
        androidx.databinding.ObservableField<java.lang.String> modelWalkName = null;
        androidx.databinding.ObservableField<java.lang.Double> modelWalkDistance = null;
        java.lang.String modelWalkTimeGet = null;
        java.lang.String mboundView1AndroidStringWalkTimeModelWalkTime = null;
        java.lang.String mboundView2AndroidStringWalkDistanceModelWalkDistance = null;
        java.lang.String mboundView2AndroidStringWalkDistanceModelWalkDistanceMboundView2AndroidStringKm = null;
        androidx.databinding.ObservableField<java.lang.String> modelRecordTime = null;
        java.lang.String mboundView3AndroidStringRecordTimeModelRecordTime = null;
        java.lang.String modelRecordTimeGet = null;
        java.lang.String modelWalkNameGet = null;
        androidx.databinding.ObservableField<java.lang.String> modelWalkTime = null;
        java.lang.Double modelWalkDistanceGet = null;

        if ((dirtyFlags & 0x3fL) != 0) {


            if ((dirtyFlags & 0x23L) != 0) {

                    if (model != null) {
                        // read model.walkName
                        modelWalkName = model.walkName;
                    }
                    updateRegistration(1, modelWalkName);


                    if (modelWalkName != null) {
                        // read model.walkName.get()
                        modelWalkNameGet = modelWalkName.get();
                    }
            }
            if ((dirtyFlags & 0x25L) != 0) {

                    if (model != null) {
                        // read model.walkDistance
                        modelWalkDistance = model.walkDistance;
                    }
                    updateRegistration(2, modelWalkDistance);


                    if (modelWalkDistance != null) {
                        // read model.walkDistance.get()
                        modelWalkDistanceGet = modelWalkDistance.get();
                    }


                    // read (@android:string/walk_distance) + (model.walkDistance.get())
                    mboundView2AndroidStringWalkDistanceModelWalkDistance = (mboundView2.getResources().getString(R.string.walk_distance)) + (modelWalkDistanceGet);


                    // read ((@android:string/walk_distance) + (model.walkDistance.get())) + (@android:string/km)
                    mboundView2AndroidStringWalkDistanceModelWalkDistanceMboundView2AndroidStringKm = (mboundView2AndroidStringWalkDistanceModelWalkDistance) + (mboundView2.getResources().getString(R.string.km));
            }
            if ((dirtyFlags & 0x29L) != 0) {

                    if (model != null) {
                        // read model.recordTime
                        modelRecordTime = model.recordTime;
                    }
                    updateRegistration(3, modelRecordTime);


                    if (modelRecordTime != null) {
                        // read model.recordTime.get()
                        modelRecordTimeGet = modelRecordTime.get();
                    }


                    // read (@android:string/record_time) + (model.recordTime.get())
                    mboundView3AndroidStringRecordTimeModelRecordTime = (mboundView3.getResources().getString(R.string.record_time)) + (modelRecordTimeGet);
            }
            if ((dirtyFlags & 0x31L) != 0) {

                    if (model != null) {
                        // read model.walkTime
                        modelWalkTime = model.walkTime;
                    }
                    updateRegistration(4, modelWalkTime);


                    if (modelWalkTime != null) {
                        // read model.walkTime.get()
                        modelWalkTimeGet = modelWalkTime.get();
                    }


                    // read (@android:string/walk_time) + (model.walkTime.get())
                    mboundView1AndroidStringWalkTimeModelWalkTime = (mboundView1.getResources().getString(R.string.walk_time)) + (modelWalkTimeGet);
            }
        }
        // batch finished
        if ((dirtyFlags & 0x31L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView1, mboundView1AndroidStringWalkTimeModelWalkTime);
        }
        if ((dirtyFlags & 0x25L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView2, mboundView2AndroidStringWalkDistanceModelWalkDistanceMboundView2AndroidStringKm);
        }
        if ((dirtyFlags & 0x29L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView3, mboundView3AndroidStringRecordTimeModelRecordTime);
        }
        if ((dirtyFlags & 0x23L) != 0) {
            // api target 1

            androidx.databinding.adapters.TextViewBindingAdapter.setText(this.mboundView4, modelWalkNameGet);
        }
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): model
        flag 1 (0x2L): model.walkName
        flag 2 (0x3L): model.walkDistance
        flag 3 (0x4L): model.recordTime
        flag 4 (0x5L): model.walkTime
        flag 5 (0x6L): null
    flag mapping end*/
    //end
}