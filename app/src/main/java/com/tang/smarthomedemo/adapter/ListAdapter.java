package com.tang.smarthomedemo.adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tang.smarthomedemo.R;
import com.tang.smarthomedemo.bean.Client;

import java.util.List;

/**
 * Created by Wangto Tang on 2015/4/13.
 */
public class ListAdapter extends BaseAdapter {
    private List<Client> list;
    private LayoutInflater mInflater;
    public ListAdapter(Context context,List<Client> clients){
        mInflater = LayoutInflater.from(context);
        list = clients;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_contact, null);
            viewHolder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.tv_mac = (TextView) convertView.findViewById(R.id.tv_mac);
            viewHolder.tv_state = (TextView) convertView.findViewById(R.id.tv_state);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Client client = list.get(position);
        viewHolder.tv_name.setText(client.getName());
        viewHolder.tv_mac.setText(client.getAddress());
        if(client.getState()== BluetoothDevice.BOND_BONDED){
            viewHolder.tv_state.setText("已绑定");
        }else{
            viewHolder.tv_state.setText("未绑定");
        }
        return convertView;
    }

    static class ViewHolder{
        TextView tv_name;
        TextView tv_mac;
        TextView tv_state;
    }
}
