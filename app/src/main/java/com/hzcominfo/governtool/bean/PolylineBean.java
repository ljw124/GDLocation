package com.hzcominfo.governtool.bean;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;

import java.io.Serializable;
import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.DaoException;
import com.hzcominfo.governtool.db.DaoSession;
import com.hzcominfo.governtool.db.MarkerBeanDao;
import com.hzcominfo.governtool.db.PolylineBeanDao;

/**
 * Create by Ljw on 2020/11/17 16:43
 */
@Entity
public class PolylineBean implements Serializable {
    @Id(autoincrement = true)
    private Long id;

    static final long serialVersionUID = 42L;

    @Convert(columnType = String.class, converter = StringConverter.class)
    List<String> latLngs; //采集的经纬度数据
    String pathName; //路径名称
    Double distance; //采集距离
    Long time; //采集时长
    String recordTime; //采集时间
    Boolean isNew; //新增后是否没有预览过

    //和MarkerBean通过外键建立一对多关系
    @ToMany(referencedJoinProperty = "markerId")
    List<MarkerBean> markers;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1507929659)
    private transient PolylineBeanDao myDao;

    @Generated(hash = 536891188)
    public PolylineBean(Long id, List<String> latLngs, String pathName,
            Double distance, Long time, String recordTime, Boolean isNew) {
        this.id = id;
        this.latLngs = latLngs;
        this.pathName = pathName;
        this.distance = distance;
        this.time = time;
        this.recordTime = recordTime;
        this.isNew = isNew;
    }

    @Generated(hash = 1071740993)
    public PolylineBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<String> getLatLngs() {
        return this.latLngs;
    }

    public void setLatLngs(List<String> latLngs) {
        this.latLngs = latLngs;
    }

    public String getPathName() {
        return this.pathName;
    }

    public void setPathName(String pathName) {
        this.pathName = pathName;
    }

    public Long getTime() {
        return this.time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public Double getDistance() {
        return this.distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public String getRecordTime() {
        return this.recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public Boolean getIsNew() {
        return this.isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1714041500)
    public List<MarkerBean> getMarkers() {
        if (markers == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MarkerBeanDao targetDao = daoSession.getMarkerBeanDao();
            List<MarkerBean> markersNew = targetDao._queryPolylineBean_Markers(id);
            synchronized (this) {
                if (markers == null) {
                    markers = markersNew;
                }
            }
        }
        return markers;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 2076886870)
    public synchronized void resetMarkers() {
        markers = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1049170157)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPolylineBeanDao() : null;
    }
}
