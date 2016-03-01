package com.xiaomi.rxjava;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import junit.framework.Assert;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    int port = 8192;
    String hostaddres;
    TextView mHelloWorld;
    MyNanoHTTPD testServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHelloWorld = (TextView) findViewById(R.id.hello_world);
        startServer();
    }

    public void startServer() {
        hostaddres = getWIFILocalIpAdress(this);
        mHelloWorld.setText(hostaddres + ":" + port);
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.testServer = new MyNanoHTTPD();
        try {
            this.testServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            long start = System.currentTimeMillis();
            Thread.sleep(100L);
            while (!this.testServer.wasStarted()) {
                Thread.sleep(100L);
                if (System.currentTimeMillis() - start > 2000) {
                    Assert.fail("could not start server");
                }
            }
        } catch (InterruptedException e) {
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.testServer.stop();
    }

    public static String getWIFILocalIpAdress(Context mContext) {

        //获取wifi服务
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        //判断wifi是否开启
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        String ip = formatIpAddress(ipAddress);
        return ip;
    }

    private static String formatIpAddress(int ipAdress) {

        return (ipAdress & 0xFF) + "." +
                ((ipAdress >> 8) & 0xFF) + "." +
                ((ipAdress >> 16) & 0xFF) + "." +
                (ipAdress >> 24 & 0xFF);
    }
}
