package com.gtx.app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gtx.util.SearchUtil;
import com.gtx.util.StringUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private EditText beginTime;
    private EditText endTime;
    private EditText searchEditText;

    private List<String> status;


    private Spinner spinner;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        initEvent();



    }
    private void initView(){
        beginTime =findViewById(R.id.beginTime);
        beginTime.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘
        beginTime.setText(MainActivity.begintime);


        endTime=findViewById(R.id.endTime);
        endTime.setInputType(InputType.TYPE_NULL); //不显示系统输入键盘
        endTime.setText(MainActivity.endtime);


        searchEditText=findViewById(R.id.searchEditText);
        searchEditText.setText(MainActivity.searchstr);


        status=new ArrayList<>();
        status.add("-1");
        status.add("-2");
        status.add("6");
        status.add("2");
        spinner=findViewById(R.id.spinner);

        int index=status.indexOf(MainActivity.status);
        spinner.setSelection(index);


    }
    private void initEvent(){
        beginTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            beginTime.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        endTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    Calendar c = Calendar.getInstance();
                    new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            endTime.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
                        }
                    }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                MainActivity.status=status.get(arg2);
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
    }
    public void getBeginTime(View view){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                beginTime.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void clearBeginTime(View view){
        beginTime.setText("");
    }

    public void getEndTime(View view){
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(SearchActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                endTime.setText(year+"-"+(monthOfYear+1)+"-"+dayOfMonth);
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void clearEndTime(View view){
        endTime.setText("");
    }


    public void search(View view){
        String beginTimeStr=beginTime.getText().toString();
        String endTimeStr=endTime.getText().toString();
        if(!StringUtil.isEmpty(beginTimeStr)&&!StringUtil.isEmpty(endTimeStr)){
            int beginTimeInt=Integer.parseInt(beginTimeStr.replaceAll("-",""));
            int endTimeInt=Integer.parseInt(endTimeStr.replaceAll("-",""));
            if (beginTimeInt>endTimeInt){
                Toast.makeText(this,"开始日期不能大于结束日期",Toast.LENGTH_SHORT).show();;
                return;
            }
        }
        MainActivity.begintime=beginTimeStr;
        MainActivity.endtime=endTimeStr;
        MainActivity.searchstr=searchEditText.getText().toString().trim();
        MainActivity.isSearch=true;
        SearchUtil.write(this, MainActivity.status);
        finish();

    }
}
