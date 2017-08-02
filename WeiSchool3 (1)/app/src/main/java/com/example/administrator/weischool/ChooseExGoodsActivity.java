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
 * Created by Administrator on 2017/7/4.
 */

public class ChooseExGoodsActivity extends Activity {
    private TextView tv;
    private TextView et1;
    private TextView et2;
    private TextView et3;
    private String userID;
    private String userID2;
    private int itemID1;

    public int version_class;

    //图片封装为一个数组
    private int[] icon;
    //商品数封装为数组
    ArrayList<HashMap<String, Object>> mylist = new ArrayList<HashMap<String, Object>>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chooseexgoods_activity);

        Intent intent = getIntent();
        //获得intent中的额外信息
        userID = intent.getStringExtra("userID");
        userID2 = intent.getStringExtra("userID2");
        itemID1 = intent.getIntExtra("itemID1",1234);

        ChooseExGoodsActivity.MyAsyncTask mytask = new MyAsyncTask();
        mytask.execute();
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        private HttpURLConnection conn;
        private URL url;
        private InputStream is;

        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/query_item_by_user.php?user_id="+ userID;
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
                    String name = item.getString("name");
                    String introduction = item.getString("introduction");
                    int item_id = item.getInt("item_id");
                    System.out.println(item.toString());

                    HashMap<String, Object> map = new HashMap<String, Object>();
                    map.put("ItemTitle", name);
                    map.put("ItemText", introduction);
                    map.put("item_id", item_id);
                    map.put("image", item.getString("picture"));
                    mylist.add(map);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //声明listview
            final ListView listview = (ListView)findViewById(R.id.choose_listview);

            //生成适配器，数组===》ListItem
            SimpleAdapter mSchedule = new SimpleAdapter(getApplicationContext(), //没什么解释
                    mylist,//数据来源
                    R.layout.goods,//ListItem的XML实现
                    new String[] {"image","ItemTitle", "ItemText","date"},//动态数组与ListItem对应的子项
                    //ListItem的XML文件里面的两个TextView ID
                    new int[] {R.id.goods_image,R.id.goods_name,R.id.goods_text,R.id.goods_date});//ListItem的XML文件里面的两个TextView ID
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
                    Intent intent = new Intent(ChooseExGoodsActivity.this, EndExGoodsActivity.class);
                    intent.putExtra("userID2",userID2);
                    intent.putExtra("userID",userID);
                    intent.putExtra("itemID1",itemID1);
                    intent.putExtra("itemID2", item_id);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);//设置新的intent
    }


}
