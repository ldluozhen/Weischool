package com.example.administrator.weischool.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by Administrator on 2017/7/5.
 */

public class GetPicFromURL {
    private ImageView imgview;
    private String img_url;

    public GetPicFromURL(ImageView view, String url){
        imgview = view;
        img_url = "http://119.29.172.139/weischool/" + url;
        // 异步加载图片
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(img_url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                imgview.setImageBitmap(bmp);
                System.out.println("Load an image: " + img_url);
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }
}
