package com.tang.smarthomedemo.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.tang.smarthomedemo.R;
import com.tang.smarthomedemo.adapter.ListAdapter;
import com.tang.smarthomedemo.bean.Client;
import com.tang.smarthomedemo.config.Config;
import com.tang.smarthomedemo.dao.ClientDao;

import java.util.List;
import java.util.UUID;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class ContactActivity extends BaseActivity {
    ListView lv_contact;
    List<Client> mClient;
    static BluetoothDevice aDevice = null;
    ProgressDialog progress;

    private Handler mHandler = new Handler(){
        //处理连接情况
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progress.dismiss();
            if(msg.what== Config.SUCCESS){
                startAnimActivity(MainActivity.class);
                finish();
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        lv_contact = (ListView) this.findViewById(R.id.lv_contact);
        mClient = ClientDao.getClient();
        //ListView设置Adapter
        lv_contact.setAdapter(new ListAdapter(this,mClient));
        //设置点击事件
        lv_contact.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                adapter.cancelDiscovery();
                final Client aClient = mClient.get(position);
                progress = getProgress("蓝牙设备",R.drawable.bluetooth02,"正在连接"+aClient.getName());
                progress.show();
                if(mSocket==null){
                    //通过子线程连接蓝牙
                    new Thread(){

                        @Override
                        public void run() {
                            super.run();
                            //1.获取MAC地址
                            aDevice = adapter.getRemoteDevice(aClient.getAddress());
                            try {
                                //2.使用蓝牙UUID获取Socket
                                mSocket = aDevice.createRfcommSocketToServiceRecord(UUID.fromString(Config.UUID));
                                //3.使用Socket连接
                                mSocket.connect();
                                progress.setMessage("连接成功");
                                mHandler.sendEmptyMessageDelayed(Config.SUCCESS,2000);
                            } catch (Exception e) {
                                mSocket = null;
                                progress.setMessage("连接失败");
                                mHandler.sendEmptyMessageDelayed(Config.FALURE,2000);
                                e.printStackTrace();
                            }
                        }

                    }.start();
                }
            }

        });
    }
}
