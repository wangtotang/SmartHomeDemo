package com.tang.smarthomedemo.manager;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class MyApplication extends Application{

    private static MyApplication mInstance;
    private BluetoothAdapter adapter;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        init();
    }

    private void init(){
        //获取蓝牙设配器
       adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public BluetoothAdapter getAdapter(){
        return adapter;
    }

    public static MyApplication getInstance() {
        return mInstance;
    }
}
