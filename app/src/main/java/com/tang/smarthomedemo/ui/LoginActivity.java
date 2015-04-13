package com.tang.smarthomedemo.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.tang.smarthomedemo.R;
import com.tang.smarthomedemo.config.Config;
import com.tang.smarthomedemo.dao.ClientDao;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {
    ImageButton btn_login;
    ProgressDialog progress;
    BroadcastReceiver mReceiver;
    Button btn_home;
    Set<BluetoothDevice> devices = new HashSet<BluetoothDevice>();
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what== Config.TIME_UP){
               //搜索完毕，启动联系人界面
                ClientDao.setClient(devices);
                progress.dismiss();
                startAnimActivity(ContactActivity.class);
                finish();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (ImageButton) findViewById(R.id.btn_login);
        btn_home = (Button) findViewById(R.id.btn_home);
        btn_home.setOnClickListener(this);
        //检查蓝牙功能
        if(adapter == null){
            showToast("该机器不支持蓝牙功能");
        }else{
            btn_login.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //搜索按钮
            case R.id.btn_login:
                if(adapter.getState()==BluetoothAdapter.STATE_ON){
                    progress = getProgress("设备连接",R.drawable.bluetooth01,"正在搜索中...");
                    progress.show();
                    //蓝牙广播接收者
                    mReceiver = new BroadcastReceiver() {

                        @Override
                        public void onReceive(Context context, Intent intent) {
                            String action = intent.getAction();
                            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                                BluetoothDevice mDevice =intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                                devices.add(mDevice);
                                ClientDao.setClient(devices);
                            }
                        }
                    };
                    IntentFilter mFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
                    registerReceiver(mReceiver, mFilter);
                    Set<BluetoothDevice> pairedDevice = adapter.getBondedDevices();
                    if(pairedDevice.size() > 0){
                        for(BluetoothDevice device:pairedDevice){
                            devices.add(device);
                        }
                    }
                    adapter.startDiscovery();
                    mHandler.sendEmptyMessageDelayed(Config.TIME_UP,2000);
                }else{
                    showToast("请打开蓝牙");
                }
                break;
            case R.id.btn_home:
                startAnimActivity(MainActivity.class);
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        //页面销毁时注销广播接受者
        if(mReceiver!=null){
            unregisterReceiver(mReceiver);
        }
        super.onDestroy();
    }
}
