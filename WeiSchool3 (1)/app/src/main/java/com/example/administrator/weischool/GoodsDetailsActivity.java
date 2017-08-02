package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
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
 * Created by Administrator on 2017/7/3.
 */

public class GoodsDetailsActivity extends Activity {
    int item_id;
    String userID;
    String userID2;//想要物品的拥有者 ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goodsdetails_activity);

        Intent thisit = getIntent();
        item_id = thisit.getIntExtra("item_id", 1234);
        userID = thisit.getStringExtra("user_id");

        System.out.println("item id = " + item_id);

        new MyAsyncTask().execute();

        Button btn = (Button)findViewById(R.id.goods_button_detail);
        btn.setOnClickListener(new View.OnClickListener(){
@Override
public void onClick(View view) {
        Intent intent = new Intent(GoodsDetailsActivity.this,ChooseExGoodsActivity.class);
        intent.putExtra("userID",userID);
        intent.putExtra("itemID1",item_id);
        intent.putExtra("userID2",userID2);
        GoodsDetailsActivity.this.startActivity(intent);
/*
                Toast.makeText(getApplicationContext(), "请求已发送,请耐心等待卖家回复!", Toast.LENGTH_SHORT).show();*/
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
            url=new URL(urlDate);
            try {
                //打开对服务器的连接
                conn=(HttpURLConnection) url.openConnection();
                conn.connect();

                //得到输入流
                is=conn.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                String line=null;
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

            ((TextView)findViewById(R.id.goods_name_detail)).setText(name);
            ((TextView)findViewById(R.id.goods_time_detail)).setText(publish_time);
            ((TextView)findViewById(R.id.goods_introduction_detail)).setText(introduction);
            ((TextView)findViewById(R.id.goods_state_detail)).setText("待售");
            new GetPicFromURL((ImageView)findViewById(R.id.goods_image_detail), item.getString("picture"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

}
