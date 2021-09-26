package com.hzcominfo.governtool.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.SettingBean;
import com.hzcominfo.governtool.databinding.ActivityMyBinding;
import com.hzcominfo.governtool.db.DaoSession;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

public class MyActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityMyBinding binding;
    private DaoSession daoSession;
    private boolean isAutoPhoto, isWalk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my);
        daoSession = ((MyApplication)getApplication()).getDaoSession();

        initView();
    }

    private void initView() {
        QueryBuilder<SettingBean> settingBeans = daoSession.queryBuilder(SettingBean.class);
        List<SettingBean> list = settingBeans.list();
        if (null != list && list.size() > 0){
            SettingBean settingBean = list.get(0);
            if (settingBean.getIsAutoPhoto()){
                binding.rbYes.setChecked(true);
                binding.etPhotoInterval.setText(settingBean.getPhotoInterval());
            } else {
                binding.rbNo.setChecked(true);
            }
            if (settingBean.getIsWalk()){
                binding.rbWalk.setChecked(true);
            } else {
                binding.rbDrive.setChecked(true);
            }
        }

        binding.ibBack.setOnClickListener(this);
        binding.btnSave.setOnClickListener(this);
        binding.rgAutoPhoto.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch (checkedID){
                    case R.id.rb_yes:
                        isAutoPhoto = true;
                        binding.etPhotoInterval.setEnabled(true);
                        break;
                    case R.id.rb_no:
                        isAutoPhoto = false;
                        binding.etPhotoInterval.setText("0");
                        binding.etPhotoInterval.setEnabled(false);
                        break;
                }
            }
        });
        binding.rgCollectType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedID) {
                switch (checkedID){
                    case R.id.rb_walk:
                        isWalk = true;
                        break;
                    case R.id.rb_drive:
                        isWalk = false;
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_save:
                daoSession.deleteAll(SettingBean.class);
                SettingBean settingBean = new SettingBean();
                settingBean.setIsAutoPhoto(isAutoPhoto);
                settingBean.setIsWalk(isWalk);
                settingBean.setPhotoInterval(binding.etPhotoInterval.getText().toString().trim());
                daoSession.insertOrReplace(settingBean);
                finish();
                break;
        }
    }
}
