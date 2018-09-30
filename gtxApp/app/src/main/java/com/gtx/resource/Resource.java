package com.gtx.resource;

import com.gtx.app.R;

public class Resource {
    private static int index = 0;
    public static String getText() {
        if (index >= text.length) index = 0;
        return text[index++];
    }
    //private static String[] text = new String[]{"上一页","刷新","下一页","切换用户","退出"};
    private static String[] text = new String[]{"刷新","切换用户","退出","首页","尾页"};


    private static int imageResourceIndex = 0;
    public static int getImageResource() {
        if (imageResourceIndex >= imageResources.length) imageResourceIndex = 0;
        return imageResources[imageResourceIndex++];
    }
    private static int[] imageResources = new int[]{
            R.drawable.bat,
            R.drawable.bear,
            R.drawable.bee,
            R.drawable.butterfly,
            R.drawable.cat,
            R.drawable.deer,
            R.drawable.dolphin,
            R.drawable.eagle,
            R.drawable.horse,
            R.drawable.elephant,
            R.drawable.owl,
            R.drawable.peacock,
            R.drawable.pig,
            R.drawable.rat,
            R.drawable.snake,
            R.drawable.squirrel
    };
}
