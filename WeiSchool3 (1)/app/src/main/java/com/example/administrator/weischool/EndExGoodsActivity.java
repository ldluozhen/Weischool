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
 * Created by Administrator on 2017/7/4.
 */

public class EndExGoodsActivity extends Activity {

    private String userID;
    private String userID2;
    private int itemID1;
    private int itemID2;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exgoodswaiting);


        Intent intent = getIntent();
        userID = intent.getStringExtra("userID");
        userID2 = intent.getStringExtra("userID2");
        itemID1 = intent.getIntExtra("itemID1",1234);
        itemID2 = intent.getIntExtra("itemID2",1234);



        Button btn = (Button) findViewById(R.id.waiting_button);
        btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                EndExGoodsActivity.MyAsyncTask mytask = new MyAsyncTask();
                mytask.execute();
                Intent intent_register = new Intent(EndExGoodsActivity.this, ClassifyActivity.class);
                intent_register.putExtra("userID",userID);
                EndExGoodsActivity.this.startActivity(intent_register);
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
            String urlDate = "http://119.29.172.139/weischool/add_trade.php?my_user_id="+ userID
                    +"&trader_user_id="+ userID2
                    +"&my_item_id="+ itemID2
                    +"&trader_item_id="+ itemID1;
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
                        Toast.makeText(getApplicationContext(), "交易生成成功!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "交易生成失败!", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
