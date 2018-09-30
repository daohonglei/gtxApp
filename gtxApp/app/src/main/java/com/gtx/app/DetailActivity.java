package com.gtx.app;

import android.Manifest;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gtx.resource.Url;
import com.gtx.util.FormartTimeUtil;
import com.gtx.util.HttpURLConnectionUtil;
import com.gtx.util.NetUtil;
import com.gtx.util.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    private TextView title;
    private String status;
    private Button btn;

    private String TICKET_ID;
    private String TASK_NUM;
    private int op_flag;
    private boolean isRequest=false;

    private ListView listView;


    private String[] keys;
    private String[] values;


    private Map<String,String> map;
    private String user;

    private LocationManager locationManager;

    private String jingDu=" ";
    private String weiDu=" ";

    private List<Map<String,String>> list=new ArrayList<>();

    private JSONObject jsonObject;
    private String type;

    private  Intent intent;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_detail);
        init();
        setData();

    }
    private void init(){
        intent=getIntent();
        title=findViewById(R.id.title);
        btn=findViewById(R.id.btn);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listView=findViewById(R.id.detail_list);

        type=intent.getStringExtra("type");
        if ("3".equals(type)){
            keys=new String[]{"联系人","联系电话","客户名称","详细地址","线路描述","工作类型","配件信息","超时时间",
                    "预计到达时间","实际到达时间","实际完成时间","工单建立时间","差旅费类型","纸质工单号","项目名称","工单状态","工单备注"};
            values=new String[]{"CONTACT","PHONE","CUST_NAME","ADDRESS","ROUTE_INFO", "JOBS","PARTS","LATETIME",
                    "EXP_ARRV_TIME","ARRIVE_TIME","COMPLETE_TIME","CREATE_TIME","TRAVEL_TYPE","PAPER_ID","PROJECT_NAME","DESTATUS","REMARKS1"};
        }else{
            keys=new String[]{"联系人","联系电话","客户名称","详细地址","线路描述","工作类型","实施车辆","实施人员","配件信息", "工单备注","超时时间",
                    "预计到达时间","实际到达时间","实际完成时间","工单建立时间","差旅费类型","纸质工单号","项目名称","工单状态","任务编号",
                    "操作员","公里数","人员数","天数"};
            values=new String[]{"CONTACT","PHONE","CUST_NAME","ADDRESS","ROUTE_INFO", "JOBS","CARS","WORKERS","PARTS","REMARKS1","LATETIME",
                    "EXP_ARRV_TIME","ARRIVE_TIME","COMPLETE_TIME","CREATE_TIME","TRAVEL_TYPE","PAPER_ID","PROJECT_NAME","DESTATUS","TASK_NUM",
                    "OPERATOR","DISTANCE","WORKER_NUM","DAYS_NUM"};
        }
    }
    private void setData(){
        try {

            user=intent.getStringExtra("user");
            String data= intent.getStringExtra("data");
            jsonObject=new JSONObject(data);
            TICKET_ID=jsonObject.getString("TICKET_ID");
            TASK_NUM=jsonObject.getString("TASK_NUM");
            status=jsonObject.getString("DESTATUS");

            title.setText(TICKET_ID);

            for (int i=0;i<keys.length;i++){
                addMapToList(i);
            }

            DetailAdapter detailAdapter=new DetailAdapter(this,list,R.layout.detail_item,new String[]{"key","value"},new int[]{R.id.key,R.id.value});
            listView.setAdapter(detailAdapter);

            showBtn();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showBtn(){
        btn.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        btn.setTextColor(Color.WHITE);

        if (!"3".equals(type)){
            if("处理中".equals(status)){
                btn.setText("确认");
                op_flag=4;
            }else if("已确认".equals(status)){
                btn.setText("到达");
                op_flag=5;
            } else if("已到达".equals(status)){
                btn.setText("完成");
                op_flag=6;
            }else{
                if ("1".equals(type)){
                    btn.setEnabled(false);
                    btn.setText(status);
                    btn.setBackgroundColor(Color.GRAY);
                }else{
                    btn.setEnabled(false);
                    btn.setText(status);
                    btn.setBackgroundColor(Color.GRAY);
                }

            }
        }else{
            btn.setEnabled(false);
            btn.setBackgroundColor(Color.GRAY);
            if ("已完成".equals(status)||"已关闭".equals(status)){
                btn.setText("已完成");
            }else{
                btn.setText("未完成");
            }
        }
    }
    private void addMapToList(int i){
        try {
            Map<String,String> map=new HashMap<>();
            map.put("key",keys[i]+"：");
            if ("3".equals(type)){
                if (i==7){
                    map.put("value",FormartTimeUtil.format(jsonObject.getLong(values[i])));
                }else{
                    map.put("value",jsonObject.getString(values[i]));
                }
            }else{
                if (i==10){
                    map.put("value",FormartTimeUtil.format(jsonObject.getLong(values[i])));
                }else{
                    map.put("value",jsonObject.getString(values[i]));
                }
            }

            list.add(map);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void changeStatus(View view){
        if (!NetUtil.netIsConnection(this)){
            Toast.makeText(this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        showNormalDialog();
       /* if (op_flag==5){
            if (!isOPenGps()){
                Toast.makeText(this,"请打开GPS",Toast.LENGTH_SHORT).show();
                openGPS2();
                return;
            }else{
                requestAllPower();
                if (!getLocation()){
                    Toast.makeText(this,"请打开定位权限",Toast.LENGTH_SHORT).show();
                    return;
                }
                if (StringUtil.isEmpty(jingDu)||StringUtil.isEmpty(weiDu)){
                    Toast.makeText(this,"没有获得位置信息,请往开阔地",Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    showNormalDialog();
                }
            }
        }else{
            showNormalDialog();
        }*/

    }
    private void showNormalDialog(){
        String title;
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(DetailActivity.this);
        if (StringUtil.isEmpty(TASK_NUM)){
           title ="你确定要"+btn.getText()+"此工单吗";
        }else{
            title ="你确定要"+btn.getText()+TASK_NUM+"任务号下的所有工单吗";
        }
        normalDialog.setTitle(title);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map =new HashMap<>();
                        if (!StringUtil.isEmpty(TASK_NUM)){
                            map.put("task_num",TASK_NUM);
                            map.put("ticket_id","");
                        }else{
                            map.put("ticket_id",TICKET_ID);
                            map.put("task_num","");
                        }
                        if (op_flag==5){
                            map.put("lon",jingDu);
                            map.put("lat",weiDu);
                        }
                        change();
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        map =new HashMap<>();
                        if (!StringUtil.isEmpty(TASK_NUM)){
                            map.put("task_num","");
                            map.put("ticket_id",TICKET_ID);
                            if (op_flag==5){
                                map.put("lon",jingDu);
                                map.put("lat",weiDu);
                            }
                            change();
                        }
                    }
                });
        // 显示
        normalDialog.show();
    }

    public void change(){
        map.put("user",user);
        map.put("op_flag",new Integer(op_flag).toString());
        if (!isRequest){
            isRequest=true;
            new Thread(){
                @Override
                public void run() {
                    String url= Url.url+"Ly_TicketAppServlet/Servlet_ServiceOp";
                    String response=new HttpURLConnectionUtil().request(url,map);
                    System.out.println("response--------------------------------------------------------------------"+response);
                    Bundle bundle=new Bundle();
                    bundle.putString("response",response);
                    Message msg=new Message();
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }.start();
        }
    }

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            result(msg.getData().getString("response"));
        }
    };
    private void result(String string){
        System.out.println(string);
        try {
            JSONObject jsonObject=new JSONObject(string);
            if ("1".equals(jsonObject.getString("retcode"))){
                MainActivity.needRefreshing=true;
                finish();
            }else{
                Toast.makeText(this, "请求失败", Toast.LENGTH_SHORT).show();
                isRequest=false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isOPenGps() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }
    private boolean getLocation(){
        Location location= null;
        try {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location!=null){
                String string="经度为:" + location.getLongitude() + "\n纬度为:" + location.getLatitude();
                Toast.makeText(this,string,Toast.LENGTH_SHORT).show();
                jingDu+=location.getLongitude();
                weiDu+=location.getLatitude();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void requestAllPower() {
        /*if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        }*/
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void bank(View view){
        finish();
    }
}

