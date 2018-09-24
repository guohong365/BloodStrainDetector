package com.uc.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SysUtils {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日");
    public static SimpleDateFormat dataTimeFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    public static String formatDate(Date date, String pattern){
        SimpleDateFormat format=new SimpleDateFormat(pattern);
        return format.format(date);
    }
}
