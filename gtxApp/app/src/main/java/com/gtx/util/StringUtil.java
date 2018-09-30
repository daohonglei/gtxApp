package com.gtx.util;

public class StringUtil {
    public static boolean isEmpty(String string){
        if (string==null||"".equals(string)){
            return true;
        }else{
            return false;
        }
    }
}
