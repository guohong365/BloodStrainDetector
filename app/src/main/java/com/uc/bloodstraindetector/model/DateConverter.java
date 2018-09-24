package com.uc.bloodstraindetector.model;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by guoho on 2017/6/12.
 */

public class DateConverter implements PropertyConverter<String, Date> {
   public static SimpleDateFormat DATE_FORMAT=new SimpleDateFormat("yyyy年MM月dd日");
    private static DateConverter private_instance=new DateConverter();
    public static String DateToString(Date date){
        return private_instance.convertToEntityProperty(date);
    }
    public static Date StringToDate(String date){
        return private_instance.convertToDatabaseValue(date);
    }
    @Override
    public String convertToEntityProperty(Date databaseValue) {
        return DATE_FORMAT.format(databaseValue);
    }

    @Override
    public Date convertToDatabaseValue(String entityProperty) {
        try {
            return DATE_FORMAT.parse(entityProperty);
        } catch (ParseException e) {
        }
        return new Date();
    }
}
