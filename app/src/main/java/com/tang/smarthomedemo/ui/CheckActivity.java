package com.tang.smarthomedemo.ui;

import android.os.Bundle;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class CheckActivity extends BaseActivity {
    boolean isLogined = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //检查是否连接蓝牙设备
        checkLogin();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLogin();
    }

    public void checkLogin() {
        if (mSocket == null) {
            isLogined = false;
        }else{
            isLogined = true;
        }
    }
}
