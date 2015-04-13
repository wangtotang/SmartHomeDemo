package com.tang.smarthomedemo.ui;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import com.tang.smarthomedemo.manager.MyApplication;

/**
 * Created by Wangto Tang on 2015/4/9.
 */
public class BaseActivity extends FragmentActivity {

    BluetoothAdapter adapter;
    BluetoothSocket mSocket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = MyApplication.getInstance().getAdapter();
    }

    public void startAnimActivity(Class cls) {
        startActivity(new Intent(this, cls));
    }

    public void startAnimActivity(Intent intent) {
        startActivity(intent);
    }

    public void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public ProgressDialog getProgress(String title,int icon,String msg){
        ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle(title);
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIcon(icon);
        progress.setMessage(msg);
        return progress;
    }

}
