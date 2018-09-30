package com.gtx.util;

public class FormartTimeUtil {
    public static String format(long time){
        long hour=0,minm=0;
        hour=time/60;
        minm=time-hour*60;
        if (hour>0){
            return  hour+":"+minm;
        }else if (minm>0){
            return  "0:"+minm;
        }else{
            return "0:0";
        }
    }
    public static String formatFullTime(String time){
        if (!StringUtil.isEmpty(time)){
            String[] strings=time.split("-");
            if(strings[1].length()<2){
                strings[1]="0"+strings[1];
            }
            if (strings[2].length()<2){
                strings[2]="0"+strings[2];
            }
            return strings[0]+"-"+strings[1]+"-"+strings[2];
        }else{
            return "";
        }
    }
}
