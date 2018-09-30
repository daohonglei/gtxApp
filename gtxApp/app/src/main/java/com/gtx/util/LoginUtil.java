package com.gtx.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class LoginUtil {
    public static void save(Context context, String string){
        PrintWriter out=null;
        try {
            out=new PrintWriter(context.openFileOutput("userInfo.txt",0));
            out.println(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if (out!=null)out.close();
        }
    }

    public static Map<String,String> read(Context context){
        BufferedReader reader=null;
        Map<String,String> map=new HashMap<>();
        try {
            reader=new BufferedReader(new InputStreamReader(context.openFileInput("userInfo.txt")));
            String string=reader.readLine();
            String[] strings=string.split(" ");
            map.put("name",strings[0]);
            map.put("pwd",strings[1]);
            map.put("isRemember",strings[2]);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (reader!=null)reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally{
                return map;
            }
        }
    }
}
