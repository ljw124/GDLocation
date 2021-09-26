package com.hzcominfo.governtool.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create by Ljw on 2019/12/17 15:09
 * 一些比较杂的工具类集合
 */
public class CommonUtils {

    private CommonUtils() {
        throw new UnsupportedOperationException("you can't instantiate me...");
    }

    /**
     * 判断身份证号是否正确（其中包括了月份和日期进行了判断）
     * @param text
     * @return
     */
    public static boolean isIDcards(String text) {
        Pattern p = Pattern
                .compile("^\\d{6}(18|19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|[xX])$");
        Matcher m = p.matcher(text);
        return m.matches();
    }

    /**
     * 根据时间生成随机字符串
     */
    public static String getTimeRandomStr() {
        Calendar ca = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
        String data = sdf.format(ca.getTimeInMillis());
        String[] split = data.split("-");
        String year = split[0];
        String month = split[1].length() < 2 ? "0" + split[1] : split[1];
        String day = split[2].length() < 2 ? "0" + split[2] : split[2];
        String hour = split[3].length() < 2 ? "0" + split[3] : split[3];
        String minute = split[4].length() < 2 ? "0" + split[4] : split[4];
        String second = split[5].length() < 2 ? "0" + split[5] : split[5];

        return year + month + day + hour + minute + second;
    }

    /**
     * fragment关闭软键盘
     * @param v
     */
    public static void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) v.getContext( ).getSystemService( Context.INPUT_METHOD_SERVICE );
        if ( imm.isActive()) {
            imm.hideSoftInputFromWindow( v.getApplicationWindowToken( ) , 0 );
        }
    }

    /**
     * 获取版本号
     */
    public static String getVersionName(Context context) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 当前时间
     * @return
     */
    public static String getCurrentTime(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(new Date());
    }

    /**
     * 判断号码是否是手机号
     *
     * @param mobiles
     * @return
     */
    public static boolean isPhoneNumber(String mobiles) {
        if (mobiles.length() != 11) {
            return false;
        }
        Pattern p = Pattern
                .compile("^((13[0-9])|(14[0-9])|(15[0-9])|(16[0-9])|(17[0-9])|(18[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 保留几位小数点
     * @param v
     * @param scale
     * @return
     */
    public static double round(Double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 固定宽度情况下，自适应文本字体大小
     * @param tv
     * @param maxWidth
     * @param text
     * @return
     */
    public static float adjustTvTextSize(TextView tv, int maxWidth, String text) {
        int avaiWidth = maxWidth - tv.getPaddingLeft() - tv.getPaddingRight() - 10;

        if (avaiWidth <= 0 || TextUtils.isEmpty(text)) {
            return tv.getPaint().getTextSize();
        }

        TextPaint textPaintClone = new TextPaint(tv.getPaint());
        // note that Paint text size works in px not sp
        float trySize = textPaintClone.getTextSize();

        while (textPaintClone.measureText(text) > avaiWidth) {
            trySize--;
            textPaintClone.setTextSize(trySize);
        }

        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, trySize);
        tv.setText(text);
        return trySize;
    }

    //空数据时候，占位图
    public static void checkEmpty(int size, View view) {
        if (size > 0) {
            view.setVisibility(View.GONE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 判断车牌格式是否正确
     * https://blog.csdn.net/LIsmooth/article/details/80981490
     * @param plateNumber
     * @return
     */
    public static boolean checkPlateNumberFormat(String plateNumber) {
//        String pattern = "([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼]{1}(([A-HJ-Z]{1}[A-HJ-NP-Z0-9]{5})|([A-HJ-Z]{1}(([DF]{1}[A-HJ-NP-Z0-9]{1}[0-9]{4})|([0-9]{5}[DF]{1})))|([A-HJ-Z]{1}[A-D0-9]{1}[0-9]{3}警)))|([0-9]{6}使)|((([沪粤川云桂鄂陕蒙藏黑辽渝]{1}A)|鲁B|闽D|蒙E|蒙H)[0-9]{4}领)|(WJ[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼·•]{1}[0-9]{4}[TDSHBXJ0-9]{1})|([VKHBSLJNGCE]{1}[A-DJ-PR-TVY]{1}[0-9]{5})";
        String pattern = "^(([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z](([0-9]{5}[DF])|([DF]([A-HJ-NP-Z0-9])[0-9]{4})))|([京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳使领]))$";
        return Pattern.matches(pattern, plateNumber);
    }
}
