package com.gtx.app;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.gtx.resource.Resource;
import com.gtx.resource.Url;
import com.gtx.service.DownApkService;
import com.gtx.util.FormartTimeUtil;
import com.gtx.util.HttpURLConnectionUtil;
import com.gtx.util.LoginUtil;
import com.gtx.util.NetUtil;
import com.gtx.util.SearchUtil;
import com.gtx.util.StringUtil;
import com.nightonke.boommenu.BoomButtons.ButtonPlaceEnum;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextInsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;
import com.nightonke.boommenu.ButtonEnum;
import com.nightonke.boommenu.Piece.PiecePlaceEnum;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private double currentVersion=20180927;
    public static String url;
    public static String version;

    private  boolean isRequest=false;
    private String name;
    private String pwd;
    private String response;
    public  Map<String,String> loginInfo;

    private String[] title;
    private String[] fields;
    private int[] columnWidths;
    private int rowHeight;
    private int emptyHeight;
    private TableLayout fixedColumn;
    private TableLayout scrollablePart;
    private TextView recyclableTextView;
    private TableRow.LayoutParams wrapWrapTableRowParams;

    private MyHandler handler = new MyHandler(this);

    private int curpage=1;
    private int totalPage=0;
    private int totalResult;
    private Map<String,String> dataMap;


    private BoomMenuButton bmb;

    private TextView tv_userName;
    private TextView pageNumber;
    private String updateContent;

    public static boolean needRefreshing=false;

    public static String status;
    public static String searchstr="";
    public static String begintime="";
    public static String endtime="";
    public static boolean isSearch;

    private Button prevbtn;
    private Button nextbtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSystem();

        mainLogin();
        initView();
        initMenu();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (needRefreshing){
            request();
            needRefreshing=false;
        }
        if (isSearch){
            curpage=1;
            totalPage=0;
            request();
            isSearch=false;
        }
    }

    private void initSystem(){

        fixedColumn = findViewById(R.id.fixed_column);
        scrollablePart= findViewById(R.id.scrollable_part);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main);
        tv_userName = headerLayout.findViewById(R.id.tv_userName);
        tv_userName.setText("请登录");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_search) {
            Intent intent=new Intent(this,SearchActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

       /* if (id == R.id.nav_camera) {

        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else*/ if (id == R.id.nav_manage) {
            checkUpdate();
        } else if (id == R.id.nav_share) {
            changeUser();
        } else if (id == R.id.nav_send) {
            System.exit(1);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initView(){
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        if (screenWidth<=768){
            rowHeight =6;
            emptyHeight=8;
            columnWidths = new int[]{27,0,15,50,15,28,
                    40,40,40,18};
        } else if (screenWidth<=1080){
            rowHeight =5;
            emptyHeight=7;
            columnWidths = new int[]{25,0,14,47,14,25,
                    37,37,37,16};
        } else if(screenWidth<=1440){
            rowHeight =6;
            emptyHeight=8;
            columnWidths = new int[]{26,0,14,46,14,28,
                    37,37,37, 18};
        }
        title=new String[]{"工单号","TICKET_ID","状态","客户名称","联系人","联系人电话",
                "预计到达时间","实际到达时间","实际完成时间","超时时间"};
        fields=new String[]{"TICKET_ID","TICKET_ID","DESTATUS","CUST_NAME", "CONTACT","PHONE",
                "EXP_ARRV_TIME","ARRIVE_TIME","COMPLETE_TIME", "LATETIME"};

        bmb = findViewById(R.id.bmb);
        pageNumber=findViewById(R.id.pageNumber);
        MainActivity.status= SearchUtil.read(this);

        prevbtn=findViewById(R.id.prevbtn);
        nextbtn=findViewById(R.id.nextbtn);
    }
    public void pleaseLogin(View view){
        if ("请登录".equals(tv_userName.getText().toString())){
            mainLogin();
        }
    }

    private void initMenu(){
        assert bmb != null;
        bmb.setButtonEnum(ButtonEnum.TextInsideCircle);
        bmb.setPiecePlaceEnum(PiecePlaceEnum.DOT_5_1);
        bmb.setButtonPlaceEnum(ButtonPlaceEnum.SC_5_1);
        bmb.setDraggable(true);
        for (int i = 0; i < bmb.getPiecePlaceEnum().pieceNumber(); i++){
            bmb.addBuilder(getTextInsideCircle());
        }
    }
    private TextInsideCircleButton.Builder getTextInsideCircle() {
        TextInsideCircleButton.Builder builder = new TextInsideCircleButton.Builder().listener(new OnBMClickListener() {
            @Override
            public void onBoomButtonClick(int index) {
                MyMethod(index);
            }
        }).normalColor(Color.BLUE).normalText(Resource.getText()).textSize(13).normalTextColor(Color.WHITE);
        return builder;
    }

    private void MyMethod(int index){
        switch (index){
            case 0:
                request();
                break;
            case 1:
                changeUser();
                break;
            case 2:
                System.exit(1);
                break;
            case 3:
                firstPage();
                break;
            case 4:
                lastPage();
                break;
            case 8:
                if (bmb.isDraggable()){
                    bmb.setDraggable(false);
                }else{
                    bmb.setDraggable(true);
                }
                break;
            default:
                break;
        }
    }
    private void changeUser(){
        SearchUtil.write(this, "-1");
        directLoginActivity(true);
    }

    public void prev(View v){
        curpage--;
        request();
    }
    public void next(View v){
        curpage++;
        request();
    }
    private void firstPage(){
       if (totalPage>1&&curpage!=1){
           curpage=1;
           request();
       }
    }
    private void lastPage(){
        if (totalPage>1&&curpage!=totalPage){
            curpage=totalPage;
            request();
        }
    }

    private void setHeader() {
        wrapWrapTableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

        TextView fixedTitle = makeTableRowWithText(title[0], columnWidths[0], rowHeight, Gravity.CENTER,14);
        fixedColumn.addView(fixedTitle);

        TableRow titleRow = new TableRow(this);
        for (int j = 1; j < title.length; j++) {
            titleRow.setLayoutParams(wrapWrapTableRowParams);
            titleRow.addView(makeTableRowWithText(title[j], columnWidths[j], rowHeight,Gravity.CENTER,14));
        }

        scrollablePart.addView(titleRow);
    }
    private void setBtnStatus(){
        if (totalPage<=1){
            prevbtn.setEnabled(false);
            nextbtn.setEnabled(false);

            prevbtn.setText("");
            nextbtn.setText("");
        } else if (totalPage>1){
            if (curpage==1){
                prevbtn.setEnabled(false);
                nextbtn.setEnabled(true);

                prevbtn.setText("");
                nextbtn.setText("下一页");
            } else if (curpage==totalPage){
                prevbtn.setEnabled(true);
                nextbtn.setEnabled(false);

                prevbtn.setText("上一页");
                nextbtn.setText("");
            }else{
                prevbtn.setEnabled(true);
                nextbtn.setEnabled(true);

                prevbtn.setText("上一页");
                nextbtn.setText("下一页");
            }
        }
    }
    public void setBody(String string){
        try {
            JSONObject json=new JSONObject(string);
            if("1".equals(json.getString("retcode"))){
                //设置表头
                setHeader();
                dataMap=new HashMap<>();
                curpage=json.getInt("curpage");
                totalPage=json.getInt("totalPage");
                totalResult=json.getInt("totalResult");
                pageNumber.setText("第"+curpage+"/"+totalPage+"页 共"+totalResult+"条");

                setBtnStatus();

                JSONArray jsonArray=new JSONArray(json.getString("data"));
                for(int k=0;k<jsonArray.length();k++){
                    String jsonString=jsonArray.getString(k);
                    JSONObject jsonObject=new JSONObject(jsonString);
                    dataMap.put(jsonObject.getString(fields[0]),jsonString);

                    TextView fixedView = makeTableRowWithText(jsonObject.getString(fields[0]), columnWidths[0], rowHeight,Gravity.LEFT,14);
                    fixedColumn.addView(fixedView);
                    fixedView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TextView textView=(TextView) view;
                            directDetailActivity(textView);
                        }
                    });
                    TableRow row = new TableRow(this);
                    row.setLayoutParams(wrapWrapTableRowParams);
                    for (int i=1;i<fields.length-1;i++){
                        row.addView(makeTableRowWithText(jsonObject.getString(fields[i]), columnWidths[i], rowHeight,Gravity.LEFT,14));
                    }
                    //时间转换
                    row.addView(makeTableRowWithText(FormartTimeUtil.format(jsonObject.getLong(fields[fields.length-1])), columnWidths[fields.length-1], rowHeight,Gravity.LEFT,14));
                    row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            TableRow row2=(TableRow)view;
                            TextView textView = (TextView)row2.getChildAt(0);
                            directDetailActivity(textView);
                        }
                    });
                    scrollablePart.addView(row);
                }
                //空白行
                TextView fixedView = makeTableRowWithText("", columnWidths[0],emptyHeight ,Gravity.LEFT,14);
                fixedColumn.addView(fixedView);
            }else{
                curpage=0;
                totalResult=0;
                pageNumber.setText("没有查到数据");
                setBtnStatus();
            }
            isRequest=false;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void directDetailActivity(TextView textView){
        String id=textView.getText().toString();
        Intent  intent=new Intent(MainActivity.this,DetailActivity.class);
        intent.putExtra("data",dataMap.get(id));
        intent.putExtra("type",loginInfo.get("type"));
        intent.putExtra("user",loginInfo.get("user"));
        startActivity(intent);
    }

    private TextView makeTableRowWithText(String text, int width, int height,int gravity,int fontSize) {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight=getResources().getDisplayMetrics().heightPixels;
        recyclableTextView = new TextView(this);
        recyclableTextView.setText(text);
        recyclableTextView.setTextColor(Color.BLACK);
        recyclableTextView.setGravity(gravity);
        recyclableTextView.setTextSize(fontSize);
        recyclableTextView.setWidth(width * screenWidth / 100);
        recyclableTextView.setHeight(height*screenHeight/100);
        return recyclableTextView;
    }


    public void request() {
        if (!NetUtil.netIsConnection(this)){
            Toast.makeText(this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        if (loginInfo==null){
            mainLogin();
            return;
        }
        if (!isRequest){
            fixedColumn.removeAllViews();
            scrollablePart.removeAllViews();
            isRequest=true;
            new Thread(){
                @Override
                public void run() {
                    if(!StringUtil.isEmpty(loginInfo.get("user"))&&!StringUtil.isEmpty(new  Integer(curpage).toString())){
                        Map<String,String> map=new HashMap<>();
                        System.out.println(loginInfo);

                        map.put("user",loginInfo.get("user"));
                        map.put("type",loginInfo.get("type"));
                        map.put("curpage",new  Integer(curpage).toString());

                        map.put("status",StringUtil.isEmpty(status)?"-1":status);
                        map.put("searchstr",StringUtil.isEmpty(searchstr)?"":searchstr);
                        map.put("begintime",StringUtil.isEmpty(begintime)?"":FormartTimeUtil.formatFullTime(begintime)+" 00:00:00");
                        map.put("endtime",StringUtil.isEmpty(endtime)?"":FormartTimeUtil.formatFullTime(endtime)+" 23:59:59");

                        String url=Url.url+"Ly_TicketAppServlet/Servlet_ServiceList";
                        response=new HttpURLConnectionUtil().request(url,map);
                        System.out.println("zi response-----------------------"+response);
                        Message msg = new Message();
                        msg.arg1=0;
                        Bundle bundle=new Bundle();
                        bundle.putString("response",response);
                        msg.setData(bundle);
                        handler.sendMessage(msg);
                    }
                }
            }.start();
        }
    }

    class MyHandler extends Handler {
        //弱引用
        WeakReference<MainActivity> weakReference;
        public MyHandler(MainActivity activity) {
            weakReference = new WeakReference(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            MainActivity activity = weakReference.get();
            if (activity != null) {
                String response=msg.getData().getString("response");
                if (msg.arg1==1){
                    login(response);
                }else if (msg.arg1==0){
                    setBody(response);
                }else if (msg.arg1==2){
                    checkResult(response);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


    private void mainLogin(){
        if (!NetUtil.netIsConnection(this)){
            Toast.makeText(this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        searchstr="";
        begintime="";
        endtime="";
        if (loginInfo==null){
            Intent intent=getIntent();
            String userName=intent.getStringExtra("userName");
            String user=intent.getStringExtra("user");
            if(!StringUtil.isEmpty(userName)&&!StringUtil.isEmpty(user)){
                loginInfo=new HashMap<>();
                loginInfo.put("userName",userName);
                loginInfo.put("user",user);
                loginInfo.put("type", intent.getStringExtra("type"));
                tv_userName.setText(userName);

                //从登录页面跳转过来
                request();
            }else{
                requestLogin();
            }
        }
    }

    private void requestLogin(){
        boolean isRedirect=false;
        Map<String,String> readMap= LoginUtil.read(this);
        if (readMap!=null){
            String isRemember=readMap.get("isRemember");
            if(!StringUtil.isEmpty(isRemember)){
                if (new Boolean(isRemember)){
                    name=readMap.get("name");
                    pwd=readMap.get("pwd");
                    new Thread(){
                        @Override
                        public void run() {
                            Map<String,String> map=new HashMap<>();
                            map.put("user",name);
                            String pass=new SimpleHash("SHA-1",name,pwd).toString();
                            map.put("pass", pass);
                            String url= Url.url+"Ly_TicketAppServlet/loginCheck";
                            response=new HttpURLConnectionUtil().request(url,map);
                            System.out.println(response);
                            Message msg=new Message();
                            msg.arg1=1;
                            Bundle bundle=new Bundle();
                            bundle.putString("response",response);
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        }
                    } .start();
                }else{
                    isRedirect=true;
                }
            }else{
                isRedirect=true;
            }
        }else{
            isRedirect=true;
        }
        directLoginActivity(isRedirect);
    }

    public void login(String response){
        try{
            boolean isRedirect=false;
            if (!StringUtil.isEmpty(response)){
                JSONObject jsonObject=new JSONObject(response);
                if ("1".equals(jsonObject.getString("retcode"))) {
                    loginInfo=new HashMap<>();
                    loginInfo.put("userName", jsonObject.getString("userName"));
                    loginInfo.put("type", jsonObject.getString("type"));
                    loginInfo.put("user", name);
                    tv_userName.setText(jsonObject.getString("userName"));

                    //登录好之后开始请求数据
                    request();
                }else{
                    isRedirect=true;
                }
            }else{
                isRedirect=true;
            }
            directLoginActivity(isRedirect);
        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,"连接服务端出现异常",Toast.LENGTH_SHORT).show();
            //directLoginActivity(true);
        }
    }

    private void directLoginActivity(boolean isRedirect){
        if(isRedirect){
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }
    private void checkUpdate(){
        Toast.makeText(this,"正在检查",Toast.LENGTH_SHORT).show();
        new Thread(){
            @Override
            public void run() {
                String url=Url.url+"Ly_TicketAppServlet/Servlet_AppVersions";
                response=new HttpURLConnectionUtil().request(url,new HashMap<String, String>());
                Message msg=new Message();
                msg.arg1=2;
                Bundle bundle=new Bundle();
                bundle.putString("response",response);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }.start();
    }
    private void checkResult(String response){
        try {
            System.out.println(response);
            JSONObject jsonObject=new JSONObject(response);
            if (jsonObject.getInt("retcode")==1){
                if (jsonObject.getInt("VERSIONS_NUM")>currentVersion){
                    url=jsonObject.getString("DOWNLOAD_LINK");
                    version=jsonObject.getString("VERSIONS_NUM");
                    updateContent=jsonObject.getString("UPDATE_CONTENT");
                    showNormalDialog();
                }else{
                    Toast.makeText(this,"已是最新版本",Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this,"检测失败",Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void showNormalDialog(){
        final AlertDialog.Builder normalDialog = new AlertDialog.Builder(MainActivity.this);
        normalDialog.setTitle("工单服务系统更新");
        normalDialog.setMessage(updateContent);
        normalDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(requestPower()){
                            Intent service = new Intent(MainActivity.this, DownApkService.class);
                            Bundle bundle=new Bundle();
                            bundle.putString("downloadUrl",url);
                            bundle.putString("title","工单管理系统");
                            service.putExtra("download",bundle);
                            startService(service);
                        }else{
                            Toast.makeText(MainActivity.this,"没有存储权限",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        normalDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        normalDialog.show();
    }
    private boolean requestPower() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
            return false;
        }
        return true;
    }
}