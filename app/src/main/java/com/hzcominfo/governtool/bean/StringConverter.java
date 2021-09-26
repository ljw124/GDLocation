package com.hzcominfo.governtool.bean;

import org.greenrobot.greendao.converter.PropertyConverter;
import java.util.Arrays;
import java.util.List;

/**
 * Create by Ljw on 2020/11/17 17:26
 */
public class StringConverter  implements PropertyConverter<List<String>, String> {

    //将数据库中的值，转化为实体Bean类对象(比如List<String>)
    @Override
    public List<String> convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        else {
            List<String> list = Arrays.asList(databaseValue.split("-"));
            return list;
        }
    }

    //将实体Bean类(比如List<String>)转化为数据库中的值(比如String)
    @Override
    public String convertToDatabaseValue(List<String> entityProperty) {
        if(entityProperty==null){
            return null;
        }
        else{
            StringBuilder sb = new StringBuilder();
            for(String link : entityProperty){
                sb.append(link);
                sb.append("-");
            }
            return sb.toString();
        }
    }
}
