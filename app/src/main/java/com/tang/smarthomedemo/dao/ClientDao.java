package com.tang.smarthomedemo.dao;

import android.bluetooth.BluetoothDevice;

import com.tang.smarthomedemo.bean.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class ClientDao {

    private static Set<BluetoothDevice> devices;

    public static List<Client> getClient(){
        List<Client> clients = new ArrayList<Client>();
        if(devices!=null) {
            for (BluetoothDevice device : devices) {
                Client mClient = new Client();
                mClient.setName(device.getName());
                mClient.setAddress(device.getAddress());
                mClient.setState(device.getBondState());
                clients.add(mClient);
            }
            return clients;
        }else{
            return null;
        }
    }

    public static void setClient(Set<BluetoothDevice> dev){
        devices = dev;
    }
}
