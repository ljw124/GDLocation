package com.hzcominfo.governtool.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.github.yuweiguocn.library.greendao.MigrationHelper;
import org.greenrobot.greendao.database.Database;

/**
 * Create by Ljw on 2020/12/1 16:17
 * 数据库升级辅助
 */
public class DBUpgradeHelper extends DaoMaster.OpenHelper {

    public DBUpgradeHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    //这里重写onUpgrade方法
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //切记不要调用super.onUpgrade(db,oldVersion,newVersion)
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, true);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, true);
            }
        }, PolylineBeanDao.class, MarkerBeanDao.class);  //具体操作数据库的dao类, 如果需要多个,用逗号隔开
    }
}