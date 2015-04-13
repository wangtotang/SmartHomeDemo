package com.tang.smarthomedemo.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tang.smarthomedemo.R;
import com.tang.smarthomedemo.config.Config;


/**
 * Created by Wangto Tang on 2015/4/9.
 */
public class SplashActivity extends BaseActivity {
    private  Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== Config.TIME_UP){
                startAnimActivity(MainActivity.class);
                finish();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHandler.sendEmptyMessageDelayed(Config.TIME_UP,2000);
    }
}
