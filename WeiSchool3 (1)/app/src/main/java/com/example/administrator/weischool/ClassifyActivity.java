package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mxn.soul.flowingdrawer_core.ElasticDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingDrawer;
import com.mxn.soul.flowingdrawer_core.FlowingMenuLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.example.administrator.weischool.R.drawable.ic_book;
import static com.example.administrator.weischool.R.drawable.ic_clothes;
import static com.example.administrator.weischool.R.drawable.ic_commodity;
import static com.example.administrator.weischool.R.drawable.ic_computer;
import static com.example.administrator.weischool.R.drawable.ic_game;
import static com.example.administrator.weischool.R.drawable.ic_music;
import static com.example.administrator.weischool.R.drawable.ic_others;
import static com.example.administrator.weischool.R.drawable.ic_pet;
import static com.example.administrator.weischool.R.drawable.ic_phone;
import static com.example.administrator.weischool.R.drawable.ic_practice;
import static com.example.administrator.weischool.R.drawable.ic_shoes;
import static com.example.administrator.weischool.R.drawable.ic_sport;

/**
 * Created by Administrator on 2017/6/27.
 */

public class ClassifyActivity extends Activity {
    @Bind(R.id.drawerlayout)
    FlowingDrawer mDrawer;

    @Bind(R.id.btn_mybusiness)
    Button btn_business;

    @Bind(R.id.btn_myupload)
    Button mBtn_upload;

    @Bind(R.id.btn_map)
    Button mBtn_map;

    @Bind(R.id.menulayout)
    FlowingMenuLayout mMenuLayout;

    private GridView classify_grid;
    private List<Map<String, Object>> data_list;
    private String userID;


    private SimpleAdapter sim_adapter;
    // 图片封装为一个数组
    private int[] icon = {ic_commodity, ic_clothes, ic_shoes, ic_sport, ic_phone,ic_computer, ic_game, ic_practice, ic_book, ic_music,ic_pet,ic_others};
    private String[] iconName = {"日用品", "衣服", "鞋子", "运动", "手机", "电脑", "游戏机", "代练", "书籍","乐器","宠物","其他"};

    String user_pic = "img/default";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_activity);

        ButterKnife.bind(this);
        mDrawer.setTouchMode(ElasticDrawer.TOUCH_MODE_BEZEL);


        Intent intent = getIntent();
        //获得intent中的额外信息
        userID = intent.getStringExtra("userID");
        user_pic = intent.getStringExtra("picture");

        // 获取用户头像
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://119.29.172.139/weischool/" + user_pic, new AsyncHttpResponseHandler(){
            @Override
            public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                ((ImageView)findViewById(R.id.roundImageView)).setImageBitmap(bmp);
            }
            @Override
            public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                System.out.println("Failed");
            }
        });



        //筛选界面
        classify_grid = (GridView) findViewById(R.id.classify_gview);
        //新建List
        data_list = new ArrayList<Map<String, Object>>();
        //获取数据
        getData();
        //新建适配器
        final String[] from = {"image", "text"};
        int[] to = {R.id.item_image, R.id.item_text};
        sim_adapter = new SimpleAdapter(this, data_list, R.layout.item, from, to);
        //配置适配器
        classify_grid.setAdapter(sim_adapter);

        classify_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                HashMap<String, Object> map = (HashMap<String, Object>) classify_grid.getItemAtPosition(position);
                String btn_name = (String) map.get("text");
                Intent intent = new Intent(ClassifyActivity.this, DealListActivity.class);
                intent.putExtra("text", btn_name);
                intent.putExtra("userID",userID);

                //Try to pass informayion.
//                intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
  /*              Intent intent = new Intent(ClassifyActivity.this,DealListActivity.class);*/

                startActivity(intent);
            }
        });

//        Button btn_classify = (Button)findViewById(R.id.addgoods_button);
//        btn_classify.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(ClassifyActivity.this,AddGoodsActivity.class);
//                intent.putExtra("userID",userID);
//                ClassifyActivity.this.startActivity(intent);
//            }
//        });

        mBtn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this,AddGoodsActivity.class);
                intent.putExtra("userID",userID);
                ClassifyActivity.this.startActivity(intent);
            }
        });

        mBtn_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this,MapActivity.class);
                intent.putExtra("userID",userID);
                ClassifyActivity.this.startActivity(intent);
            }
        });

        btn_business.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClassifyActivity.this,MyTradeListAcivity.class);
                intent.putExtra("userID",userID);
                ClassifyActivity.this.startActivity(intent);
            }
        });





    }

    public List<Map<String, Object>> getData() {
        //cion和iconName的长度是相同的，这里任选其一都可以
        for (int i = 0; i < icon.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("image", icon[i]);
            map.put("text", iconName[i]);
            data_list.add(map);
        }
        return data_list;
    }
}
