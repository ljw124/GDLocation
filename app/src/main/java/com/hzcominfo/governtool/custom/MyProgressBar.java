package com.hzcominfo.governtool.custom;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import com.hzcominfo.governtool.R;

/**
 * Create by Ljw on 2020/12/4 14:47
 */
public class MyProgressBar extends View {

    private Paint mPaint;
    private int max = 60;
    private int progress = 0;

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyProgressBar); //与属性名称一致
        max = array.getInteger(R.styleable.MyProgressBar_max, 60);//第一个是传递参数，第二个是默认值
        progress = array.getInteger(R.styleable.MyProgressBar_progress, 0);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Point left_top=new Point(0,0);
        Point right_bottom=new Point(getWidth(),getHeight());
        double rate=(double)progress/(double)max;
        drawProgressBar(canvas, left_top, right_bottom, rate);
    }

    public void setProgress(int progress){
        this.progress=progress;
        invalidate();//使得onDraw重绘
    }

    private void drawProgressBar(Canvas canvas,Point left_top,Point right_bottom,double rate){
        int width = 1;
        int rad = 10;
        mPaint.setColor(Color.BLACK);//画边框
        mPaint.setStyle(Paint.Style.STROKE);//设置空心
        mPaint.setStrokeWidth(width);
        RectF rectF = new RectF(left_top.x,left_top.y,right_bottom.x,right_bottom.y);
        canvas.drawRoundRect(rectF, rad, rad, mPaint);
        mPaint.setColor(Color.GREEN);//画progress bar
        if (rate > 0.9)
            mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
        int x_end=(int)(right_bottom.x*rate);
        RectF rectF2 = new RectF(left_top.x+width,left_top.y+width,x_end-width,right_bottom.y-width);
        canvas.drawRoundRect(rectF2, rad, rad, mPaint);
    }
}
