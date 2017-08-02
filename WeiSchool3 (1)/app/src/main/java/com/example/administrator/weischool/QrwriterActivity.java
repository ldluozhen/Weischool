package com.example.administrator.weischool;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.EncodeHintType;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;


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
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/7/5.
 */

public class QrwriterActivity extends AppCompatActivity {
    @Bind(R.id.iv)
    ImageView iv;

    int item_id;
    int item_id2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_qrwriter_activity);

        //获取intent的额外信息
        Intent intent = getIntent();
        item_id = intent.getIntExtra("itemID1",1234);
        item_id2 = intent.getIntExtra("itemID2",1234);

        ButterKnife.bind(this);
        generate(iv);

    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        private HttpURLConnection conn;
        private URL url;
        private InputStream is;

        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/finish_trade.php?my_item_id="+ item_id +"&trader_item_id="+ item_id2;
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
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject item = (JSONObject) arr.get(i);
                    int is_ok = item.getInt("is_ok");
                    String error_str = item.getString("error_str");

                    if (is_ok == 1) {
                        Toast.makeText(getApplicationContext(), "交易成功!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "交易失败!", Toast.LENGTH_SHORT).show();
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }




    private Bitmap generateBitmap(String content, int width, int height) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            BitMatrix encode = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            int[] pixels = new int[width * height];
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (encode.get(j, i)) {
                        pixels[i * width + j] = 0x00000000;
                    } else {
                        pixels[i * width + j] = 0xffffffff;
                    }
                }
            }
            return Bitmap.createBitmap(pixels, 0, width, width, height, Bitmap.Config.RGB_565);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void generate(View view) {
        Bitmap qrBitmap = generateBitmap("http://119.29.172.139/weischool/finish_trade.php?my_item_id="+ item_id +"&trader_item_id="+ item_id2,400, 400);
        iv.setImageBitmap(qrBitmap);
    }

}