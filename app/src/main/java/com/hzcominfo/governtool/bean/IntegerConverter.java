package com.hzcominfo.governtool.bean;

import org.greenrobot.greendao.converter.PropertyConverter;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Ljw on 2020/11/25 17:56
 */
public class IntegerConverter implements PropertyConverter<List<Integer>, Integer> {

    //将数据库中的值，转化为实体Bean类对象(比如List<String>)
    @Override
    public List<Integer> convertToEntityProperty(Integer databaseValue) {
        List<Integer> list = Arrays.asList(databaseValue);
        return list;
    }

    //将实体Bean类(比如List<String>)转化为数据库中的值(比如String)
    @Override
    public Integer convertToDatabaseValue(List<Integer> entityProperty) {
        if(entityProperty==null){
            return null;
        } else{
            Integer nub = 0;
            for(int link : entityProperty){
                nub = link;
            }
            return nub;
        }
    }
}
