package com.hzcominfo.governtool.custom.audio;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import com.hzcominfo.governtool.R;
import com.hzcominfo.governtool.utils.ToastUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Create by Ljw on 2020/11/25 17:37
 * 自定义按钮 实现录音等功能
 */
@SuppressLint("AppCompatCustomView")
public class AudioRecorderButton extends Button implements AudioManager.AudioStateListener {

    //手指滑动 距离
    private static final int DISTANCE_Y_CANCEL = 50;
    //状态
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    //当前状态
    private int mCurState = STATE_NORMAL;
    //已经开始录音
    private boolean isRecording = false;

    private DialogManager mDialogManager;
    private AudioManager mAudioManager;

    private float mTime; //录音时长
    //是否触发onlongclick
    private boolean mReady;

    private static final int MSG_AUDIO_PREPARED = 0X110;
    private static final int MSG_VOICE_CHANGED = 0X111;
    private static final int MSG_DIALOG_DIMISS = 0X112;

    private AudioFinishRecorderListener mListener;

    private TimerTask timerTask;
    private Timer timer;
    private int mAudioTime;

    public AudioRecorderButton(Context context) {
        this(context, null);
    }

    public void setAudioFinishRecorderListener (AudioFinishRecorderListener listener){
        mListener = listener;
    }

    public AudioRecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDialogManager = new DialogManager(getContext());
//        String dir = Environment.getExternalStorageDirectory() + "/audio";
        String dir = context.getExternalMediaDirs()[0].getAbsolutePath() + "/audio";
        mAudioManager = new AudioManager(dir);
        mAudioManager.setOnAudioStateListener(this);
        //按钮长按 准备录音 包括start
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mReady = true;
                mAudioManager.prepareAudio();
                return false;
            }
        });
    }

    /**
     * 录音回调接口
     */
    public interface AudioFinishRecorderListener{
        //时长 和 文件路径
        void onFinish(float seconds, String filePath);
        //录音时长
        void startAudio(int audioTime);
        //录音取消
        void cancelAudio();
    }

    //获取音量大小的Runnable
    private Runnable mGetVoiceLevelRunnable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                try {
                    Thread.sleep(100);
                    mTime += 0.1;
                    mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_AUDIO_PREPARED :
                    //TODO 真正现实应该在audio end prepared以后
                    mDialogManager.showRecordingDialog();
                    isRecording = true;
                    new Thread(mGetVoiceLevelRunnable).start();
                    break;
                case MSG_VOICE_CHANGED :
                    mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));
                    break;
                case MSG_DIALOG_DIMISS :
                    mDialogManager.dimissDialog();
                    break;
            }
        }
    };

    @Override
    public void wellPrepared() {
        mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isRecording = true;
                changeState(STATE_RECORDING);
                startTimer();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    //根据想x,y的坐标，判断是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                        stopTimer();
                        mListener.cancelAudio();
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                stopTimer();
                //如果longclick 没触发
                if (!mReady) {
                    reset();
                    return super.onTouchEvent(event);
                }
                //触发了onlongclick 没准备好，但是已经prepared 已经start
                //所以消除文件夹
                if(!isRecording || mTime<0.6f){
                    mDialogManager.tooShort();
                    mAudioManager.cancel();
                    mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 500);
                    mListener.cancelAudio();
                }else if(mCurState == STATE_RECORDING){//正常录制结束
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime,mAudioManager.getCurrentFilePath());
                    }
                } else if (mCurState == STATE_WANT_TO_CANCEL) {
                    mDialogManager.dimissDialog();
                    mAudioManager.cancel();
                }
                reset();
                break;
        }
        return super.onTouchEvent(event);
    }

    /**
     * 恢复状态 标志位
     */
    private void reset() {
        isRecording = false;
        mReady = false;
        changeState(STATE_NORMAL);
        mTime = 0;
    }

    private boolean wantToCancel(int x, int y) {
        //如果左右滑出 button
        if (x < 0 || x > getWidth()) {
            return true;
        }
        //如果上下滑出 button  加上我们自定义的距离
        if (y < -DISTANCE_Y_CANCEL || y > getHeight() + DISTANCE_Y_CANCEL) {
            return true;
        }
        return false;
    }

    //改变状态
    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    setBackgroundResource(R.drawable.btn_recorder_normal);
                    setText(R.string.str_recorder_normal);
                    break;
                case STATE_RECORDING:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_recording);
                    if (isRecording) {
                        mDialogManager.recording();
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    setBackgroundResource(R.drawable.btn_recording);
                    setText(R.string.str_recorder_want_cancel);
                    mDialogManager.wantToCancel();
                    break;
            }
        }
    }

    private void startTimer(){
        mAudioTime = 0;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (mAudioTime > 60){
                    stopTimer();
                    mDialogManager.dimissDialog();
                    mAudioManager.release();
                    if (mListener != null) {
                        mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
                    }
                    reset();
                    return;
                }
                mAudioTime ++;
                if (mListener != null){
                    mListener.startAudio(mAudioTime);
                }
            }
        };
        timer.schedule(timerTask,0,1000);
    }

    private void stopTimer(){
        if (null != timerTask && !timerTask.cancel()) {
            timerTask.cancel();
            timer.cancel();
        }
    }
}
