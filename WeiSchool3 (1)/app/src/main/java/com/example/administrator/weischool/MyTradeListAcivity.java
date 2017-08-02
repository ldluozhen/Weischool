package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.example.administrator.weischool.view.GetPicFromURL;

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
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Administrator on 2017/7/5.
 */

public class MyTradeListAcivity extends Activity {
    private TextView tv;
    private String userID;

    public int version_class;

    //图片封装为一个数组
    private int[] icon;
    //商品数封装为数组
    ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mytrade_activity);

        Intent intent = getIntent();
        //获得intent中的额外信息
        userID = intent.getStringExtra("userID");


        MyTradeListAcivity.MyAsyncTask mytask = new MyAsyncTask();
        mytask.execute();
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        private HttpURLConnection conn;
        private URL url;
        private InputStream is;

        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/get_trade_by_user.php?user_id="+ userID ;
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
                    System.out.println("lalala"+is.toString());
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
                    String trader_item_name = item.getString("trader_item_name");
                    int trader_item_id = item.getInt("trader_item_id");
                    int trader_id = item.getInt("trader_id");
                    String trader_name = item.getString("trader");
                    String name = item.getString("my_item_name");
                    int item_id = item.getInt("my_item_id");
                    System.out.println(item.toString());

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("trader_item_name", trader_item_name);
                    map.put("trader_item_id", trader_item_id);
                    map.put("trader_id", trader_id);
                    map.put("trader_name", trader_name);
                    map.put("ItemTitle", name);
                    map.put("item_id", item_id);
                    map.put("image1", item.getString("my_item_picture"));
                    map.put("image2", item.getString("trader_item_picture"));
                    mylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //声明listview
            final ListView listview = (ListView)findViewById(R.id.mytrade_list);

            //生成适配器
            SimpleAdapter mSchedule = new SimpleAdapter(getApplicationContext(), //没什么解释
                    mylist,//数据来源
                    R.layout.trades,//ListItem的XML实现
                    new String[] {"image1","image2","item_id","ItemTitle","trader_name","trader_id","trader_item_id","trader_item_name"},//动态数组与ListItem对应的子项
                    //ListItem的XML文件里面的两个TextView ID
                    new int[] {R.id.my_item_img,R.id.trader_item_img,R.id.item_id,R.id.my_item_name,R.id.trader_name,R.id.trader_id,R.id.trade_item_id,R.id.trade_item_name});//ListItem的XML文件里面的两个TextView ID
            mSchedule.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data, String textRepresentation) {
                    if (view instanceof ImageView) {
                        System.out.println("String: " + textRepresentation);
                        new GetPicFromURL((ImageView)view, textRepresentation);
                        return true;
                    } else
                        return false;
                }
            });
            //添加并且显示
            listview.setAdapter(mSchedule);

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                    HashMap<String, Object> map = (HashMap<String, Object>) listview.getItemAtPosition(position);
                    String btn_name = (String) map.get("ItemText");
                    int item_id = (int)map.get("item_id");
                    int trader_item_id = (int)map.get("trader_item_id");
                    String user_id = userID;
                    Intent intent = new Intent(MyTradeListAcivity.this, TradesDetailsActivity.class);
                    intent.putExtra("item_id2",trader_item_id);
                    intent.putExtra("item_id", item_id);
                    intent.putExtra("user_id",userID);
                    startActivity(intent);
                }
            });
        }
}
}
