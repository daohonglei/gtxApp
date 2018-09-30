package com.gtx.app;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DetailAdapter extends SimpleAdapter {
    private Context context;
    private List<? extends Map<String, String>> list;
    public DetailAdapter(Context context, List<? extends Map<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context=context;
        this.list=data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v=View.inflate(context,R.layout.detail_item,null);
        TextView key=v.findViewById(R.id.key);
        TextView value=v.findViewById(R.id.value);

        if (position==1){
            value.setAutoLinkMask(Linkify.ALL);
            value.setMovementMethod(LinkMovementMethod.getInstance());
        }

        Map<String,String> map=list.get(position);
        key.setText(map.get("key"));
        value.setText(map.get("value"));
        return v;
    }
}
