package com.example.administrator.weischool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Window;
import android.widget.ImageView;
import com.example.administrator.weischool.R;
import com.example.administrator.weischool.utils.SpUtil;

/**
 * Created by Administrator on 2017/6/28.
 */

public class WelcomeActivity extends Activity {
    protected static final String TAG = "WelcomeActivity";
    private Context mContext;
    private ImageView mImageView;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
/*        Transition explode = TransitionInflater.from(this).inflateTransition(R.transition.explode);
        getWindow().setExitTransition(explode);*/
        mContext = this;
        sp = SpUtil.getSharePerference(mContext);
        boolean isFirst = SpUtil.isFirst(sp);
        if (!isFirst) {
            SpUtil.getInstance();
            SpUtil.setBooleanSharedPerference(sp,
                    "isFirst", true);
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            setContentView(R.layout.activity_welcome);
            findView();
            mImageView.postDelayed(new Runnable() {

                @Override
                public void run() {
                    enterHomeActivity();
                }
            }, 2000);

        }


        // System.out.println("准备跳转查找结果页面");

        //init();
    }

    private void findView() {
        mImageView = (ImageView) findViewById(R.id.iv_welcome);
    }

    private void init() {
        mImageView.postDelayed(new Runnable() {
            @Override
            public void run() {

                SpUtil.getInstance();
                sp = SpUtil.getSharePerference(mContext);
                SpUtil.getInstance();
                boolean isFirst = SpUtil.isFirst(sp);
                if (!isFirst) {
                    SpUtil.getInstance();
                    SpUtil.setBooleanSharedPerference(sp,
                            "isFirst", true);
                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {

                    Intent intent = new Intent(mContext, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        },2000);

    }

    private void enterHomeActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
