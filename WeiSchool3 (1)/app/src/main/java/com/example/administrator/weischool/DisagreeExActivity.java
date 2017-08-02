package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Administrator on 2017/7/5.
 */

public class DisagreeExActivity extends Activity {
    int item_id;
    int item_id2;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disagree_activity);

        Intent intent = getIntent();
        item_id = intent.getIntExtra("itemID1",1234);
        item_id2 = intent.getIntExtra("itemID2",1234);

        Button btn = (Button) findViewById(R.id.disagree_button);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                DisagreeExActivity.MyAsyncTask mytask = new MyAsyncTask();
                mytask.execute();
            }
        });
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        private HttpURLConnection conn;
        private URL url;
        private InputStream is;

        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/del_trade.php?my_item_id="+ item_id +"&trader_item_id="+ item_id2;
            System.out.println(urlDate);
            try {
                //封装访问服务器的地址
                url=new URL(urlDate);
                try {
                    //打开对服务器的连接
                    conn=(HttpURLConnection) url.openConnection();
                    conn.connect();

                    //得到输入流
                    is=conn.getInputStream();
                    //创建包装流
                    BufferedReader br=new BufferedReader(new InputStreamReader(is));
                    //定义String类型用于储存单行数据
                    String line=null;
                    //创建StringBuffer对象用于存储所有数据
                    sb=new StringBuffer();
                    while((line=br.readLine())!=null){
                        sb.append(line);
                    }
                } catch (IOException e) {
                    System.out.println("err1");
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                System.out.println("err2");
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            try {
                JSONArray arr = new JSONArray(sb.toString());
                //生成动态数组，并且转载数据
                for(int i = 0; i < arr.length(); i++){
                    JSONObject item = (JSONObject)arr.get(i);
                    int is_ok = item.getInt("is_ok");
                    String error_str = item.getString("error_str");

                    if(is_ok == 1) {
                        Toast.makeText(getApplicationContext(), "交易删除成功!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "交易删除失败!", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




}}}