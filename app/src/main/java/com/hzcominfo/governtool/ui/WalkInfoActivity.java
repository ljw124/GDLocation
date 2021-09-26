package com.hzcominfo.governtool.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.adapter.BaseAdapter;
import com.hzcominfo.governtool.adapter.LocationInfoAdapter;
import com.hzcominfo.governtool.adapter.RouteInfoAdapter;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.databinding.ActivityWalkInfoBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.model.ModelProvider;
import com.hzcominfo.governtool.model.WalkInfoModel;
import com.hzcominfo.governtool.utils.ActivityCollector;
import com.hzcominfo.governtool.utils.StringUtils;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.lxj.xpopup.core.CenterPopupView;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WalkInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityWalkInfoBinding binding;
    private RouteInfoAdapter routeInfoAdapter;
    private LocationInfoAdapter locationInfoAdapter;
    private RecyclerView rvInfo;
    private DaoSession daoSession;
    private BasePopupView deletePopupView;
    private PolylineBean deletePolyline;
    private boolean isDeleteMarker;
    private MarkerBean deleteMarker;
    private WalkInfoModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_info);
        daoSession = ((MyApplication)getApplication()).getDaoSession();
        binding.setModel(ModelProvider.getInstance().get(WalkInfoModel.class));
        model = binding.getModel();

        init();
    }

    private void init() {
        rvInfo = binding.rvInfo;
        routeInfoAdapter = new RouteInfoAdapter();
        locationInfoAdapter = new LocationInfoAdapter();
        rvInfo.setLayoutManager(new LinearLayoutManager(this));

        binding.ibReturn.setOnClickListener(this);

        String className = model.className.get();
        if (StringUtils.isNotEmpty(className) && className.equals("Marker")){
            binding.tabInfo.getTabAt(1).select();
            getMarkersInfo();
        } else {
            binding.tabInfo.getTabAt(0).select();
            getPolylinesInfo();
        }

        routeInfoAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<PolylineBean>() {
            @Override
            public void onItemClick(PolylineBean item, int position) {
                model.className.set("Polyline");
                Intent intent = new Intent(WalkInfoActivity.this, RouteDetailActivity.class);
                intent.putExtra("PolylineBean", (Serializable)item);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.fake_anim);
            }
        });
        routeInfoAdapter.setOnItemLongClickListener(new BaseAdapter.OnItemLongClickListener<PolylineBean>() {
            @Override
            public void onItemLongClick(PolylineBean item, int position) {
                deletePolyline = item;
                isDeleteMarker = false;
                deletePopupView = new XPopup.Builder(WalkInfoActivity.this)
                        .asCustom(new DeletePopup(WalkInfoActivity.this))
                        .show();
            }
        });

        locationInfoAdapter.setOnItemClickListener(new BaseAdapter.OnItemClickListener<MarkerBean>() {
            @Override
            public void onItemClick(MarkerBean item, int position) {
                model.className.set("Marker");
                Intent intent = new Intent(WalkInfoActivity.this, MarkerDetailActivity.class);
                intent.putExtra("MarkerBean", (Serializable)item);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_bottom, R.anim.fake_anim);
            }
        });
        locationInfoAdapter.setOnItemLongClickListener(new BaseAdapter.OnItemLongClickListener<MarkerBean>() {
            @Override
            public void onItemLongClick(MarkerBean item, int position) {
                deleteMarker = item;
                isDeleteMarker = true;
                deletePopupView = new XPopup.Builder(WalkInfoActivity.this)
                        .asCustom(new DeletePopup(WalkInfoActivity.this))
                        .show();
            }
        });

        binding.tabInfo.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0:
                        getPolylinesInfo();
                        break;
                    case 1:
                        getMarkersInfo();
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    /**
     * 获取行走路线信息
     */
    private void getPolylinesInfo() {
        List<PolylineBean> polylineBeans = daoSession.loadAll(PolylineBean.class);
        routeInfoAdapter.setDataList((ArrayList<PolylineBean>) polylineBeans);
        rvInfo.setAdapter(routeInfoAdapter);
        routeInfoAdapter.notifyDataSetChanged();
        if (polylineBeans.size() == 0){
            binding.llNoInfo.setVisibility(View.VISIBLE);
        } else {
            binding.llNoInfo.setVisibility(View.GONE);
        }
    }

    /**
     * 获取 Marker 信息
     */
    public void getMarkersInfo(){
        List<MarkerBean> markerBeans = daoSession.loadAll(MarkerBean.class);
        locationInfoAdapter.setDataList((ArrayList<MarkerBean>) markerBeans);
        rvInfo.setAdapter(locationInfoAdapter);
        locationInfoAdapter.notifyDataSetChanged();
        if (markerBeans.size() == 0){
            binding.llNoInfo.setVisibility(View.VISIBLE);
        } else {
            binding.llNoInfo.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_return:
                Intent intent = new Intent(WalkInfoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
                break;
        }
    }

    private class DeletePopup extends CenterPopupView {

        public DeletePopup(@NonNull Context context) {
            super(context);
        }

        @Override
        protected int getImplLayoutId() {
            return R.layout.popup_delete_ensure;
        }

        @Override
        protected void onCreate() {
            super.onCreate();
            TextView tvMsg = findViewById(R.id.tv_msg);
            if (isDeleteMarker){
                tvMsg.setText("确定删除标记信息吗？");
            } else {
                tvMsg.setText("确定删除路径信息吗？");
            }
            Button btnCancel = findViewById(R.id.btn_cancel);
            Button btnEnsure = findViewById(R.id.btn_ensure);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deletePopupView.dismiss();
                }
            });
            btnEnsure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isDeleteMarker) {
                        daoSession.delete(deleteMarker);
                        getMarkersInfo();
                    }else {
                        daoSession.delete(deletePolyline);
                        getPolylinesInfo();
                    }
                    deletePopupView.dismiss();
                }
            });
        }
        protected void onShow() {
            super.onShow();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        model.className.set("");
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            finish();
        }
        return false;
    }
}
