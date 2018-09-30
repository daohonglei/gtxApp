package com.gtx.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchUtil {
    public static void write(Context context,String string){
        PrintWriter out=null;
        try {
            out=new PrintWriter(context.openFileOutput("search.txt",0));
            out.println(string);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally{
            if (out!=null)out.close();
        }
    }
    public static  String read(Context context){
        BufferedReader reader=null;
        String string="";
        try {
            reader=new BufferedReader(new InputStreamReader(context.openFileInput("search.txt")));
            string=reader.readLine();
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
                return string;
            }
        }
    }
}
