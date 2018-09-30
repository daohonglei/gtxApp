package com.gtx.app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.gtx.resource.Url;
import com.gtx.util.Encoder;
import com.gtx.util.HttpURLConnectionUtil;
import com.gtx.util.LoginUtil;
import com.gtx.util.NetUtil;
import com.gtx.util.StringUtil;

import org.apache.shiro.crypto.hash.SimpleHash;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText ed_name;
    private EditText ed_pwd;
    private CheckBox ch_remember;
    private Button loginBut;

    private String response;
    private Boolean isRemember;
    private  boolean isRequestIng=false;

    private Map<String,String> map;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        init();

    }

    private void init(){
        map=new HashMap<>();
        ed_name=findViewById(R.id.name);
        ed_pwd=findViewById(R.id.pwd);
        loginBut=findViewById(R.id.login);
        ch_remember=findViewById(R.id.remember);
        loginBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestLogin();
            }
        });
        map=LoginUtil.read(this);
        if(map!=null){
            if("true".equals(map.get("isRemember"))){
                ed_name.setText(map.get("name"));
                ed_pwd.setText(map.get("pwd"));
                ch_remember.setChecked(true);
            }
        }
    }
    private void requestLogin(){
        if (!NetUtil.netIsConnection(this)){
            Toast.makeText(this,"网络连接不可用",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!isRequestIng){
            isRequestIng=true;
            new Thread(){
                @Override
                public void run() {

                    Map<String,String> map=new HashMap<>();
                    String user=ed_name.getText().toString();
                    String pass=ed_pwd.getText().toString();
                    pass=new SimpleHash("SHA-1",user,pass).toString();
                    map.put("user",user);
                    map.put("pass",pass);
                    String url= Url.url+"Ly_TicketAppServlet/loginCheck";
                    response=new HttpURLConnectionUtil().request(url,map);
                    Message msg=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("response",response);
                    msg.setData(bundle);
                    mhandler.sendMessage(msg);
                }
            } .start();
        }
    }
    private Handler mhandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            isRequestIng=false;
            String response=msg.getData().getString("response");
            login(response);
        }
    };
    public void login(String response){
        try{
            if (!StringUtil.isEmpty(response)) {
                JSONObject jsonObject = new JSONObject(response);
                if ("1".equals(jsonObject.getString("retcode"))) {
                    isRemember = false;
                    String userInfo = null;
                    if (ch_remember.isChecked()) {
                        isRemember = true;
                        userInfo = ed_name.getText().toString().trim() + " " + ed_pwd.getText().toString().trim() + " " + isRemember.toString();
                    }
                    LoginUtil.save(this, userInfo);

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("userName", jsonObject.getString("userName"));
                    intent.putExtra("type", jsonObject.getString("type"));
                    intent.putExtra("user", ed_name.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this,"用户名或者密码出错",Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(LoginActivity.this,"连接服务端出现异常",Toast.LENGTH_SHORT).show();
            }
        }catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(LoginActivity.this,"连接服务端出现异常",Toast.LENGTH_SHORT).show();
        }
    }
}
