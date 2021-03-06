package com.hzcominfo.governtool.db;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.hzcominfo.governtool.bean.MarkerBean;
import com.hzcominfo.governtool.bean.PolylineBean;
import com.hzcominfo.governtool.bean.SettingBean;

import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.db.PolylineBeanDao;
import com.hzcominfo.governtool.db.SettingBeanDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig markerBeanDaoConfig;
    private final DaoConfig polylineBeanDaoConfig;
    private final DaoConfig settingBeanDaoConfig;

    private final MarkerBeanDao markerBeanDao;
    private final PolylineBeanDao polylineBeanDao;
    private final SettingBeanDao settingBeanDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        markerBeanDaoConfig = daoConfigMap.get(MarkerBeanDao.class).clone();
        markerBeanDaoConfig.initIdentityScope(type);

        polylineBeanDaoConfig = daoConfigMap.get(PolylineBeanDao.class).clone();
        polylineBeanDaoConfig.initIdentityScope(type);

        settingBeanDaoConfig = daoConfigMap.get(SettingBeanDao.class).clone();
        settingBeanDaoConfig.initIdentityScope(type);

        markerBeanDao = new MarkerBeanDao(markerBeanDaoConfig, this);
        polylineBeanDao = new PolylineBeanDao(polylineBeanDaoConfig, this);
        settingBeanDao = new SettingBeanDao(settingBeanDaoConfig, this);

        registerDao(MarkerBean.class, markerBeanDao);
        registerDao(PolylineBean.class, polylineBeanDao);
        registerDao(SettingBean.class, settingBeanDao);
    }
    
    public void clear() {
        markerBeanDaoConfig.clearIdentityScope();
        polylineBeanDaoConfig.clearIdentityScope();
        settingBeanDaoConfig.clearIdentityScope();
    }

    public MarkerBeanDao getMarkerBeanDao() {
        return markerBeanDao;
    }

    public PolylineBeanDao getPolylineBeanDao() {
        return polylineBeanDao;
    }

    public SettingBeanDao getSettingBeanDao() {
        return settingBeanDao;
    }

}
