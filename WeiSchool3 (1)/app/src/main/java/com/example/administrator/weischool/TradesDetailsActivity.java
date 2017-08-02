package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.weischool.view.GetPicFromURL;

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

public class TradesDetailsActivity extends Activity {
    int item_id;
    int item_id2;
    String userID;
    String userID2;//想要物品的拥有者 ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tradedetails_activity);

        Intent thisit = getIntent();
        item_id = thisit.getIntExtra("item_id", 1234);
        item_id2 = thisit.getIntExtra("item_id2",1234);
        userID = thisit.getStringExtra("user_id");

        System.out.println("item id = " + item_id);

        MyAsyncTask mytask = new MyAsyncTask();
        mytask.execute();

        Button btn = (Button)findViewById(R.id.trades_button_detail);
        Button btn2 = (Button)findViewById(R.id.trades_button2_detail);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TradesDetailsActivity.this,QrwriterActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("itemID1",item_id);
                intent.putExtra("userID2",userID2);
                intent.putExtra("itemID2",item_id2);
                TradesDetailsActivity.this.startActivity(intent);
            }
        });

        btn2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TradesDetailsActivity.this,DisagreeExActivity.class);
                intent.putExtra("userID",userID);
                intent.putExtra("itemID1",item_id);
                intent.putExtra("userID2",userID2);
                intent.putExtra("itemID2",item_id2);
                TradesDetailsActivity.this.startActivity(intent);
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
            String urlDate = "http://119.29.172.139/weischool/get_item_detail.php?item_id="+ item_id;
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
                JSONObject item = new JSONObject(sb.toString());
                System.out.println(item.toString());

                String name = item.getString("name");
                String introduction = item.getString("introduction");
                String publish_time = item.getString("publish_time");
                userID2 = item.getString("user_id");

                ((TextView)findViewById(R.id.trades_name_detail)).setText(name);
                ((TextView)findViewById(R.id.trades_time_detail)).setText(publish_time);
                ((TextView)findViewById(R.id.trades_introduction_detail)).setText(introduction);
                ((TextView)findViewById(R.id.trades_state_detail)).setText("待售");
                new GetPicFromURL((ImageView)findViewById(R.id.trades_image_detail), item.getString("picture"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
