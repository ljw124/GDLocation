package com.hzcominfo.governtool.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.hzcominfo.governtool.MyApplication;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.custom.audio.AudioRecorderButton;
import com.hzcominfo.governtool.databinding.ActivityRecordBinding;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.utils.ToastUtils;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.ArrayList;
import java.util.List;

public class AudioRecordActivity extends AppCompatActivity {

    private ActivityRecordBinding binding;
    private String latLng;
    private DaoSession daoSession;
    private QueryBuilder<MarkerBean> queryBuilder;
    List<String> voice = new ArrayList<>();
    List<String> voiceTime = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_record);
        daoSession = ((MyApplication)getApplication()).getDaoSession();

        init();
    }

    private void init() {
        double latitude = getIntent().getDoubleExtra("latitude", 30.210515);
        double longitude = getIntent().getDoubleExtra("longitude", 120.22263);
        latLng = latitude + "," + longitude;
        // 录音完毕回调
        binding.idRecorderButton.setAudioFinishRecorderListener(new AudioRecorderButton.AudioFinishRecorderListener() {
            @Override
            public void startAudio(int audioTime) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.pbProgress.setProgress(audioTime);
                    }
                });
            }

            @Override
            public void cancelAudio() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.pbProgress.setProgress(0);
                    }
                });
            }

            @Override
            public void onFinish(float seconds, String filePath) {
                int time = (int)(seconds + 0.5f); //加 0.5 向下取整
                queryBuilder = daoSession.queryBuilder(MarkerBean.class).where(MarkerBeanDao.Properties.LatLngs.eq(latLng));
                List<MarkerBean> markerBeans = queryBuilder.list();
                if (markerBeans != null && markerBeans.size() > 0){
                    MarkerBean markerBean = markerBeans.get(0);
                    if (null != markerBean.getVoices() && markerBean.getVoices().size()>0){
                        List newVoices = new ArrayList(markerBean.getVoices());
                        newVoices.add(filePath);
                        markerBean.setVoices(newVoices);
                        List newVoiceTime = new ArrayList(markerBean.getVoiceTime());
                        newVoiceTime.add(time + "");
                        markerBean.setVoiceTime(newVoiceTime);
                    } else {
                        voice.add(filePath);
                        voiceTime.add(time + "");
                        markerBean.setVoices(voice);
                        markerBean.setVoiceTime(voiceTime);
                    }
                    daoSession.update(markerBean);
                }
                finish();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtils.showToast("语音已保存");
                    }
                });
            }
        });
    }
}
