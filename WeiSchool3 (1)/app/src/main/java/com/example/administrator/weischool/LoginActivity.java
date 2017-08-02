package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
 * Created by Administrator on 2017/6/22.
 */

/*
*  页面跳转，跳转至地图API主页面
* */
public class LoginActivity extends Activity {
/*    private android.support.v7.widget.Toolbar toolbar;*/

    private HttpURLConnection conn;
    private URL url;
    private InputStream is;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        Button btn = (Button)findViewById(R.id.btn_login);
        Button btn_register =(Button)findViewById(R.id.btn_register);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyAsyncTask mytask = new MyAsyncTask();
                mytask.execute();
            }
        });
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_register = new Intent(LoginActivity.this,RegisterActivity.class);
                LoginActivity.this.startActivity(intent_register);
            }
        });
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        //声明布局文件中的textview
        TextView tv0 = (TextView) findViewById(R.id.name);
        TextView tv1 = (TextView) findViewById(R.id.password);


        //获取布局文件中输入内容
        String name =  tv0.getText().toString();
        String password =  tv1.getText().toString();

        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/login.php?user_name="+ name +"&password="+ password;
            System.out.println(urlDate);
            try {
                //封装访问服务器的地址
                url=new URL(urlDate);
                try {
                    //打开对服务器的连接
                    conn=(HttpURLConnection) url.openConnection();
                    //连接服务器
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
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }


        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            StringBuffer buff = new StringBuffer();
            try {
                JSONObject item = new JSONObject(sb.toString());
                int is_ok = item.getInt("is_ok");
                String error_str = item.getString("error_str");
                String user_id = item.getString("user_id");
                String picture = item.getString("picture");
                System.out.println(item.toString());
                if ( is_ok == 1 ){
                    Toast.makeText(getApplicationContext(), "登录成功!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,ClassifyActivity.class);
                    intent.putExtra("userID",user_id);
                    intent.putExtra("picture",picture);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(getApplicationContext(), "登录失败!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(LoginActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}


