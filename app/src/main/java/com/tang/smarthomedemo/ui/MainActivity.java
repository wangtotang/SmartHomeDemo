package com.tang.smarthomedemo.ui;

import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.tang.smarthomedemo.R;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MainActivity extends CheckActivity implements View.OnClickListener {

    private TabHost mTab;
    private ImageButton mImagBLink;
    private ImageButton mImagBTemp;
    private ImageButton mImagBLight;
    private ImageButton mImagBSwitch;
    private TextView mTVTime,mTVTemp;
    private TextView mTVState,mTVTempState;
    private Button mBtLink;
    private ImageView iv1;
    private ToggleButton tb1;
    private ToggleButton tb2;
    private final int TIME_UP = 1,mRead = 2;
    private boolean stopThread = false,mReadTemp = false;
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    private String title = "温度检测";
    private XYSeries series;
    private GraphicalView mChartView;
    private double x = -1,y;
    private int[] xv = new int[100];
    private int[] yv = new int[100];
    /*private InputStream is = null;
    private OutputStream os = null;*/
    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TIME_UP:
                    if(mSocket!=null){
                        String time = mTVTime.getText().toString();
                        int start = time.indexOf(":");
                        int second = Integer.parseInt(time.substring(start+1,time.length()));
                        int minute = Integer.parseInt(time.substring(0,start));
                        //截取字符（当前位置，结束位置后一位）
                        second++;
                        if(second==60){
                            second = 0;
                            minute++;
                            if(minute==60){
                                minute = 0;
                            }
                        }
                        if(minute < 10){
                            time = "0"+minute;
                        }else if(minute >= 10){
                            time = ""+minute;
                        }
                        if(second < 10){
                            time = time+":"+"0"+second;
                        }else if(second >= 10){
                            time = time+":"+second;
                        }
                        mTVTime.setText(time);
                    }else{
                        mTVTime.setText("00:00");
                    }
                    break;
                case mRead:
                    if(mReadTemp){
                        byte[] mByte = (byte[]) msg.obj;
                        String mmsg = new String(mByte,0,msg.arg1);
                        int start = mmsg.indexOf(".");
                        if(start==2){
                            if(mmsg.length() >= 4){
                                String mtemper = mmsg.substring(start-2, 4);
                                mTVTemp.setText(mtemper+"℃");
                                double temp = Double.parseDouble(mtemper);
                                if(temp<10){
                                    mTVTempState.setText("低温");
                                }else if(temp>30){
                                    mTVTempState.setText("高温");
                                }else{
                                    mTVTempState.setText("常温");
                                }
                                updateChart(temp);
                            }
                        }else{
                            if(mmsg.length()>4){
                                String mtemper = mmsg.substring(start+2, start+6);
                                mTVTemp.setText(mtemper+"℃");
                                double temp = Double.parseDouble(mtemper);
                                if(temp<10){
                                    mTVTempState.setText("低温");
                                }else if(temp>30){
                                    mTVTempState.setText("高温");
                                }else{
                                    mTVTempState.setText("常温");
                                }
                                updateChart(temp);
                            }
                        }
                    }
                    break;
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTab = (TabHost) this.findViewById(R.id.th_main);
        mTab.setup();

        mImagBLink = new ImageButton(this);
        mImagBLink.setBackgroundResource(R.drawable.btn_link_selector);
        TabHost.TabSpec mLink = mTab.newTabSpec("Link");
        mLink.setIndicator(mImagBLink);
        mLink.setContent(R.id.ll_1);
        mTab.addTab(mLink);
        mImagBLink.setEnabled(false);

        View view = mTab.getTabContentView();
        mTVTime = (TextView) view.findViewById(R.id.tv_time);
        mBtLink = (Button) view.findViewById(R.id.bt_link);
        mBtLink.setOnClickListener(this);
        mTVState = (TextView) view.findViewById(R.id.tv_linkstate);

        mImagBTemp = new ImageButton(this);
        mImagBTemp.setBackgroundResource(R.drawable.btn_temp_selector);
        TabHost.TabSpec mTemp = mTab.newTabSpec("Temp");
        mTemp.setIndicator(mImagBTemp);
        mTemp.setContent(R.id.ll_2);
        mTab.addTab(mTemp);
        mTVTemp = (TextView) view.findViewById(R.id.tv_temp);
        mTVTempState = (TextView) view.findViewById(R.id.tv_tempstate);
        myDraw(view);

        mImagBLight = new ImageButton(this);
        mImagBLight.setBackgroundResource(R.drawable.btn_light_selector);
        TabHost.TabSpec mLight = mTab.newTabSpec("Light");
        mLight.setIndicator(mImagBLight);
        mLight.setContent(R.id.ll_3);
        mTab.addTab(mLight);
        iv1 = (ImageView) view.findViewById(R.id.iv1);
        tb1 = (ToggleButton) view.findViewById(R.id.tb1);
        tb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                iv1.setImageResource(isChecked?R.drawable.lighton:R.drawable.lightoff);
                try {
                    if (isChecked) {
                        write("#3#");
                    } else {
                        write("#2#");
                    }
                } catch (Exception e) {
                }
            }
        });

        mImagBSwitch = new ImageButton(this);
        mImagBSwitch.setBackgroundResource(R.drawable.btn_other_selector);
        TabHost.TabSpec mOther = mTab.newTabSpec("Other");
        mOther.setIndicator(mImagBSwitch);
        mOther.setContent(R.id.ll_4);
        mTab.addTab(mOther);
        tb2 = (ToggleButton) view.findViewById(R.id.tb2);
        tb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                tb2.setChecked(isChecked);
                tb2.setBackgroundResource(isChecked?R.drawable.switchon:R.drawable.switchoff);
                try {
                    if (isChecked) {
                        write("#5#");
                    } else {
                        write("#4#");
                    }
                } catch (Exception e) {
                }
            }
        });


        mTab.setOnTabChangedListener(new TabHost.OnTabChangeListener(){

            @Override
            public void onTabChanged(String tabId) {
                int j = mTab.getTabWidget().getTabCount();
                ImageButton currentImagB =(ImageButton) mTab.getCurrentTabView();
                for(int i = 0;i < j;i++){
                    if(mTab.getCurrentTab()==i){
                        currentImagB.setEnabled(false);
                    }else{
                        if(mTab.getTabWidget().getChildTabViewAt(i)!=null){
                            ((ImageButton)mTab.getTabWidget().getChildTabViewAt(i)).setEnabled(true);
                        }
                    }
                }

                if(tabId=="Temp"){
                    mReadTemp = true;
                    if(mSocket!=null){
                        new Thread(new Runnable(){
                            @Override
                            public void run() {
                                while(mReadTemp){
                                    try {
                                        Thread.sleep(600);
                                        InputStream is = mSocket.getInputStream();
                                        write("#1#");
                                        byte[] buffer = new byte[is.available()];
                                        int len = is.read(buffer);
                                        //is.close();
                                        mHandler.obtainMessage(mRead, len, -1, buffer).sendToTarget();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        stopThread = true;
                                        return;
                                    }

                                }
                            }

                        }).start();
                    }
                }else{
                    mReadTemp = false;
                    write("#0#");

                }
            }

        });

    }
    public void write(String message){
        if(mSocket!=null){
            try {
                OutputStream os = mSocket.getOutputStream();
                os.write(message.getBytes());
                //os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_link:
                if(mBtLink.getText().equals("开始连接")){
                    startAnimActivity(LoginActivity.class);
                }else{
                    mBtLink.setText("开始连接");
                    mTVState.setText("未连接");
                    stopThread = true;
                    if(mSocket!=null){
                        try {
                            mSocket.close();
                            //每次蓝牙断开连接后，都要置空，才能再次连接
                            mSocket = null;
					/*is = null;
					os = null;*/
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
    }
    private class MyThread implements Runnable{

        @Override
        public void run() {
            //页面销毁后，需要关闭子线程，不然再次打开此页面时会同时存在多个子线程
            while(!stopThread){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = TIME_UP;
                mHandler.sendMessage(msg);
            }
        }

    }
    @Override
    protected void onDestroy() {
        stopThread = true;

        if(mSocket!=null){
            try {
                mSocket.close();
                //每次蓝牙断开连接后，都要置空，才能再次连接
                mSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
    @Override
    protected void onStart() {
        if(mSocket!=null){
            stopThread = false;
            MyThread mThread = new MyThread();
            new Thread(mThread).start();
            mTVState.setText("已连接");
            mBtLink.setText("断开连接");
		/*if(os==null){
		try {
			os = Contact.mSocket.getOutputStream();
			is = Contact.mSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
		}*/
        }
        super.onStart();
    }
    private void myDraw(View view){

        series = new XYSeries(title);

        // 创建一个数据集的实例，这个数据集将被用来创建图表
        mDataset = new XYMultipleSeriesDataset();

        // 将点集添加到这个数据集中
        mDataset.addSeries(series);
        int color = Color.GREEN;
        PointStyle style = PointStyle.CIRCLE;
        mRenderer = buildRenderer(color, style, true);
        // 设置好图表的样式
        setChartSettings(mRenderer, "X", "Y", 0, 100, -10, 40, Color.WHITE,
                Color.WHITE);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.ll_temp);
        mChartView = ChartFactory.getLineChartView(view.getContext(), mDataset, mRenderer);

        layout.addView(mChartView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));


    }

    private XYMultipleSeriesRenderer buildRenderer(int color,
                                                   PointStyle style, boolean fill) {
        XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

        // 设置图表中曲线本身的样式，包括颜色、点的大小以及线的粗细等
        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(color);
        r.setPointStyle(style);
        r.setFillPoints(fill);
        r.setLineWidth(3);
        renderer.addSeriesRenderer(r);

        return renderer;
    }
    protected void setChartSettings(XYMultipleSeriesRenderer renderer,
                                    String xTitle, String yTitle, double xMin, double xMax,
                                    double yMin, double yMax, int axesColor, int labelsColor) {
        // 有关对图表的渲染可参看api文档
        renderer.setChartTitle("温度监测");
        renderer.setXTitle(xTitle);
        renderer.setYTitle(yTitle);
        renderer.setXAxisMin(xMin);
        renderer.setXAxisMax(xMax);
        renderer.setYAxisMin(yMin);
        renderer.setYAxisMax(yMax);
        renderer.setAxesColor(axesColor);
        renderer.setLabelsColor(labelsColor);
        renderer.setShowGrid(true);
        renderer.setGridColor(Color.GREEN);
        renderer.setXLabels(20);
        renderer.setYLabels(10);
        renderer.setXTitle("时间");
        renderer.setYTitle("温度");
        renderer.setYLabelsAlign(Paint.Align.RIGHT);
        renderer.setPointSize((float) 2);
        renderer.setShowLegend(false);
    }
    private void updateChart(double y) {

        // 设置好下一个需要增加的节点
        this.x = 0;
        this.y = y;
        // 移除数据集中旧的点集
        mDataset.removeSeries(series);
        // 判断当前点集中到底有多少点，因为屏幕总共只能容纳100个，所以当点数超过100时，长度永远是100
        int length = series.getItemCount();
        if (length > 100) {
            length = 100;
        }
        // 将旧的点集中x和y的数值取出来放入backup中，并且将x的值加1，造成曲线向右平移的效果
        for (int i = 0; i < length; i++) {
            xv[i] = (int) series.getX(i) + 1;
            yv[i] = (int) series.getY(i);
        }
        // 点集先清空，为了做成新的点集而准备
        series.clear();

        // 将新产生的点首先加入到点集中，然后在循环体中将坐标变换后的一系列点都重新加入到点集中
        // 这里可以试验一下把顺序颠倒过来是什么效果，即先运行循环体，再添加新产生的点
        series.add(x,y);
        for (int k = 0; k < length; k++) {
            series.add(xv[k], yv[k]);
        }
        // 在数据集中添加新的点集
        mDataset.addSeries(series);

        // 视图更新，没有这一步，曲线不会呈现动态
        // 如果在非UI主线程中，需要调用postInvalidate()，具体参考api
        mChartView.invalidate();
    }
}
