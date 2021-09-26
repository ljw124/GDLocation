package com.hzcominfo.governtool;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.blankj.utilcode.util.Utils;
import com.dcdz.loglibrary.CrashHandler;
import com.dcdz.loglibrary.Log4jConfigure;
import com.hzcominfo.governtool.db.DBUpgradeHelper;
import com.hzcominfo.governtool.db.DaoMaster;
import com.hzcominfo.governtool.db.DaoSession;
import com.lzy.okgo.OkGo;
import com.taobao.sophix.SophixManager;
import com.tencent.bugly.crashreport.CrashReport;
import org.apache.log4j.Logger;

/**
 * Create by Ljw on 2020/11/16 14:00
 * https://www.jianshu.com/p/53083f782ea2
 * https://www.jianshu.com/p/95167d419e85
 * https://blog.csdn.net/csdn_aiyang/article/details/80665185
 * https://github.com/yuweiguocn/GreenDaoUpgradeHelper
 */
public class MyApplication extends Application {

    private static MyApplication context;
    protected static Logger logger = Logger.getLogger(MyApplication.class);
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        // 初始化记录日志的工具类
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
        new Thread(){
            @Override
            public void run() {
                Log4jConfigure.configure(getFilesDir().getAbsolutePath());
                logger.info("configure log4j ok");
            }
        }.start();

        //初始化工具类
        Utils.init(this);

        //初始化本地数据库greenDao——原始的，数据库保存在默认位置、数据库升级数据清除
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "govern.db"); //创建一个数据库
//        SQLiteDatabase db = helper.getWritableDatabase(); //获得一个db
//        DaoMaster daoMaster = new DaoMaster(db); //新建一个DaoMaster，获得master
//        daoSession = daoMaster.newSession(); //通过master new一个Daosession

        //初始化本地数据库greenDao——升级后不清除数据
        DBUpgradeHelper dbUpgradeHelper = new DBUpgradeHelper(context, "govern.db", null);
        SQLiteDatabase db = dbUpgradeHelper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db); //新建一个DaoMaster，获得master
        daoSession = daoMaster.newSession(); //通过master new一个Daosession

        //bugly上报
        CrashReport.initCrashReport(getApplicationContext(), "1f6407a422", true);

        //这里是请求服务器的补丁（后期正式应用这里可以做个开关）
        // queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();

        //网络初始化
        OkGo.getInstance().init(this);
    }

    public static Context getContext(){
        return context;
    }

    public DaoSession getDaoSession() { return daoSession; }
}
