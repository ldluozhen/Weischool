package com.example.administrator.weischool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

/**
 * Created by Administrator on 2017/6/22.
 */

public class RegisterActivity extends Activity {
    //图像处理相关参数
    private ImageView mImage;
    private Button mAddImage;
    private Bitmap mBitmap;
    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    private static final int CROP_SMALL_PICTURE = 2;

    Uri MyUri ;
    String img_path;

    private HttpURLConnection conn;
    private URL url;
    private InputStream is;

    String pic_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        initUI();
        initListeners();

        Button btn = (Button) findViewById(R.id.btn_rgt_submit);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                MyAsyncTask mytask = new MyAsyncTask();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
                    MyUri = tempUri;
                    System.out.println(MyUri);
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
        System.out.println("\n\n\nset img to view\n\n\n");
        Bundle extras = data.getExtras();
        if (extras != null) {
            mBitmap = extras.getParcelable("data");
            //这里图片是方形的，可以用一个工具类处理成圆形（很多头像都是圆形，这种工具类网上很多不再详述）
            mImage.setImageBitmap(mBitmap);//显示图片
            try {
                img_path = Environment.getExternalStorageDirectory()+"/cut_image.jpg";
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





    class MyAsyncTask extends AsyncTask<Void, Void, Void> {
        StringBuffer sb;
        //声明布局文件中的textview
        TextView tv0 = (TextView) findViewById(R.id.register_name);
        TextView tv1 = (TextView) findViewById(R.id.register_password);
        TextView tv2 = (TextView) findViewById(R.id.register_password2);
        TextView tv3 = (TextView) findViewById(R.id.register_nickname);
        TextView tv4 = (TextView) findViewById(R.id.phoneNumber);

        //获取布局文件中输入内容
        String name =  tv0.getText().toString();
        String password =  tv1.getText().toString();
        String password2 =  tv2.getText().toString();
        String nickname =  tv3.getText().toString();
        String phoneNumber =  tv4.getText().toString();

        @Override
        protected Void doInBackground(Void... voids) {
            String urlDate = "http://119.29.172.139/weischool/register.php?user_name=" + name +
                    "&user_nickname=" + nickname +
                    "&password=" + password +
                    "&phone_number=" + phoneNumber +
                    "&picture=" + pic_url +
                    "&longitude=123.4&latitude=321.0";
            try {
                //封装访问服务器的地址
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
                    e.printStackTrace();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            publishProgress();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            StringBuffer buff = new StringBuffer();
            try {
                JSONObject item = new JSONObject(sb.toString());
                int is_ok = item.getInt("is_ok");
                String error_str = item.getString("error_str");
                String user_id = item.getString("user_id");
                if(is_ok == 1) {
                    Toast.makeText(getApplicationContext(), "注册成功!", Toast.LENGTH_SHORT).show();
                    Intent intent_register = new Intent(RegisterActivity.this, LoginActivity.class);
                    RegisterActivity.this.startActivity(intent_register);
                }
                else{
                    Toast.makeText(getApplicationContext(), "注册失败,用户名已存在!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
