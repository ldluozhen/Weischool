package com.example.administrator.weischool;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddGoodsActivity extends AppCompatActivity {
    //图像处理相关参数
    private ImageView mImage;
    private Button mAddImage;
    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    private Spinner spinner;
    private List<String> data_list;
    private ArrayAdapter<String> arr_adapter;
    private String userID;
    private int goods_class2;

    //获得textview
    TextView tv;
    TextView tv1;
    Spinner sn1;
    //获得文本
    String name;
    String introduction;
    String goods_cate;

    String pic_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addgoods_activity);

        Intent intent = getIntent();
        //获得intent中的额外信息
        userID = intent.getStringExtra("userID");

        spinner = (Spinner) findViewById(R.id.addgoods_spinner);
        data_list = new ArrayList<String>();
        data_list.add("日用品");
        data_list.add("衣服");
        data_list.add("鞋子");
        data_list.add("运动");
        data_list.add("手机");
        data_list.add("电脑");
        data_list.add("游戏机");
        data_list.add("代练");
        data_list.add("数据");
        data_list.add("乐器");
        data_list.add("宠物");
        data_list.add("其他");

        //适配器
        arr_adapter= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data_list);
        //设置样式
        arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //加载适配器
        spinner.setAdapter(arr_adapter);

        initUI();
        initListeners();

        Button btn = (Button) findViewById(R.id.addgoods_submit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tv = (TextView) findViewById(R.id.addgoods_name);
                tv1 = (TextView) findViewById(R.id.addgoods_introduction);
                sn1 = (Spinner) findViewById(R.id.addgoods_spinner);
                //获得文本
                name = tv.getText().toString();
                introduction = tv1.getText().toString();
                goods_cate = sn1.getSelectedItem().toString();
                System.out.println(goods_cate);

                if (goods_cate.equals("日用品")){
                    goods_class2 = 1;
                }
                else if (goods_cate.equals("衣服")){
                    goods_class2 = 2;
                }
                else if (goods_cate.equals("鞋子")){
                    goods_class2 = 3;
                }
                else if (goods_cate.equals("运动")){
                    goods_class2 = 4;
                }
                else if (goods_cate.equals("手机")){
                    goods_class2 = 5;
                }
                else if (goods_cate.equals("电脑")){
                    goods_class2 = 6;
                }
                else if (goods_cate.equals("游戏机")){
                    goods_class2 = 7;
                }
                else if (goods_cate.equals("代练")){
                    goods_class2 = 8;
                }
                else if (goods_cate.equals("书籍")){
                    goods_class2 = 9;
                }
                else if (goods_cate.equals("乐器")){
                    goods_class2 = 10;
                }
                else if (goods_cate.equals("宠物")){
                    goods_class2 = 11;
                }
                else if (goods_cate.equals("其他")){
                    goods_class2 = 0;
                }

                AddGoodsActivity.MyAsyncTask mytask = new MyAsyncTask();
                mytask.execute();
            }
        });


    }

    private void initUI() {
        mImage= (ImageView) findViewById(R.id.iv_image);
        mAddImage= (Button) findViewById(R.id.btn_add_image);
    }
    private void initListeners() {
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChoosePicDialog();
            }
        });
    }
    /**
     * 显示修改图片的对话框
     */
    protected void showChoosePicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddGoodsActivity.this);
        builder.setTitle("添加图片");
        String[] items = { "选择本地照片", "拍照" };
        builder.setNegativeButton("取消", null);
        builder.setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case CHOOSE_PICTURE: // 选择本地照片
                        Intent openAlbumIntent = new Intent(
                                Intent.ACTION_GET_CONTENT);
                        openAlbumIntent.setType("image/*");
                        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
                        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                        break;
                    case TAKE_PICTURE: // 拍照
                        Intent openCameraIntent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        tempUri = Uri.fromFile(new File(Environment
                                .getExternalStorageDirectory(), "temp_image.jpg"));
                        // 将拍照所得的相片保存到SD卡根目录
                        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
                        startActivityForResult(openCameraIntent, TAKE_PICTURE);
                        break;
                }
            }
        });
        builder.show();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AddGoodsActivity.RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    cutImage(tempUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    cutImage(data.getData()); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    if (data != null) {
                        setImageToView(data); // 让刚才选择裁剪得到的图片显示在界面上
                    }
                    break;
            }
        }
    }
    /**
     * 裁剪图片方法实现
     */
    protected void cutImage(Uri uri) {
        if (uri == null) {
            Log.i("alanjet", "The uri is not exist.");
        }
        tempUri = uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    /**
     * 保存裁剪之后的图片数据
     */
    protected void setImageToView(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            mImage.setImageBitmap(mBitmap);//显示图片
            //在这个地方可以写上上传该图片到服务器的代码，后期将单独写一篇这方面的博客，敬请期待...
            try {
                String img_path = Environment.getExternalStorageDirectory()+"/cut_image.jpg";
                System.out.println("我是文件路径:" + img_path );
                File myFile = new File(img_path);
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myFile));
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                bos.flush();
                bos.close();

                RequestParams params = new RequestParams();
                params.put("file", new File(img_path));
                AsyncHttpClient client = new AsyncHttpClient();
                client.post("http://119.29.172.139/weischool/upload_img.php", params, new AsyncHttpResponseHandler(){
                    @Override
                    public void onSuccess(int i, org.apache.http.Header[] headers, byte[] bytes) {
                        try {
                            JSONObject pic = new JSONObject(new String(bytes));
                            pic_url = pic.getString("img_url");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int i, org.apache.http.Header[] headers, byte[] bytes, Throwable throwable) {
                        System.out.println("Failed");
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class MyAsyncTask extends AsyncTask<Object, Object, Integer> {
        StringBuffer sb;
        private HttpURLConnection conn;
        private URL url;
        private InputStream is;



        @Override
        protected Integer doInBackground(Object... voids) {
            String urlDate = "http://119.29.172.139/weischool/add_item.php?user_id="+ userID
                +"&name="+ name
                +"&picture=" + pic_url
                +"&introduction=" + introduction
                +"&item_class=" + goods_class2;
            System.out.println(urlDate);
            try {
                //封装访问服务器的地址
                url=new URL(urlDate);
                //打开对服务器的连接
                conn=(HttpURLConnection) url.openConnection();
                conn.connect();
                is=conn.getInputStream();
                BufferedReader br=new BufferedReader(new InputStreamReader(is));
                String line=null;
                sb=new StringBuffer();
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
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

                int is_ok = item.getInt("is_ok");
                String error_str = item.getString("error_str");

                if(is_ok == 1){
                    Toast.makeText(getApplicationContext(), "发布成功!", Toast.LENGTH_SHORT).show();
                    Intent intent_register = new Intent(AddGoodsActivity.this, ClassifyActivity.class);
                    intent_register.putExtra("userID",userID);
                    startActivity(intent_register);
                }
                else{
                    Toast.makeText(getApplicationContext(), "发布失败!", Toast.LENGTH_SHORT).show();
                    Intent intent_register = new Intent(AddGoodsActivity.this, AddGoodsActivity.class);
                    intent_register.putExtra("userID",userID);
                    startActivity(intent_register);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
