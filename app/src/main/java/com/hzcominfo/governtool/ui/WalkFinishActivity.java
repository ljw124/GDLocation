package com.hzcominfo.governtool.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.databinding.ActivityWalkFinishBinding;
import com.hzcominfo.governtool.model.ModelProvider;
import com.hzcominfo.governtool.model.WalkModel;
import com.hzcominfo.governtool.utils.ActivityCollector;

import java.util.Locale;

public class WalkFinishActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivityWalkFinishBinding binding;
    private WalkModel walkModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_walk_finish);
        binding.setModel(ModelProvider.getInstance().get(WalkModel.class));
        walkModel = binding.getModel();

        init();
    }

    private void init() {
        binding.btnAgain.setOnClickListener(this);
        binding.btnLookOver.setOnClickListener(this);
        walkModel.walkTime.set(getStringTime());
    }

    private String getStringTime() {
        int time = Integer.parseInt(walkModel.walkTime.get());
        int hour = time/3600;
        int min = time % 3600 / 60;
        int second = time % 60;
        return String.format(Locale.CHINA,"%02d:%02d:%02d",hour,min,second);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_again:
                goWalkLinePage();
                break;
            case R.id.btn_look_over:
                goWalkInfoPage();
                break;
        }
    }

    private void goWalkLinePage() {
        Intent intent = new Intent(WalkFinishActivity.this, WalkLineActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
    }

    private void goWalkInfoPage() {
        Intent intent = new Intent(WalkFinishActivity.this, WalkInfoActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.fake_anim);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            return true;
        }
        return false;
    }
}
