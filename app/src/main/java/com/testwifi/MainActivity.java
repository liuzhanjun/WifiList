package com.testwifi;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.switchbutton.SwitchButton;
import com.ricky.pulltorefresh.PullToRefreshLayout;
import com.ricky.pulltorefresh.WrapRecyclerView;
import com.testwifi.recyclerview.BaseLinearDecoration;
import com.testwifi.recyclerview.OnRecyclerItemClick;
import com.testwifi.view.CustomerDialog;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private WrapRecyclerView recyclerView;
    private PullToRefreshLayout ptrl;
    private BaseLinearDecoration Verticaldecoration;
    private WifiAdmin admin;
    private TextView wifiname;
    public static final  String TAG="MainActivity";
    private List<ScanResult> slist;
    private WifiAdapter adapter;
    private Context mContext;
    private SwitchButton wifi_sw;
    private String wifiPassword;
    private List<WifiConfiguration> confis;
    private ScanResult result;
    private int clickSSID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        wifiname= (TextView) findViewById(R.id.wifiname);
        ptrl= (PullToRefreshLayout) findViewById(R.id.refresh_view);
        admin=new WifiAdmin(getApplicationContext()){

            @Override
            public Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter) {
                mContext.registerReceiver(receiver,filter);
                return null;
            }

            @Override
            public void myUnregisterReceiver(BroadcastReceiver receiver) {
                mContext.unregisterReceiver(receiver);
            }

            @Override
            public void onNotifyWifiConnected() {
                Log.i(TAG, "onNotifyWifiConnected: ");
                admin.updateWifiInfo();
                wifiname.setText(admin.getSSID());
            }

            @Override
            public void onNotifyWifiConnectFailed() {
                Log.i(TAG, "onNotifyWifiConnectFailed: ");
            }

            @Override
            public void onNotifyWifiOpened() {
                admin.startScan();
                slist = admin.getWifiList();
                adapter.setScanResults(slist);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "onRefresh: ==================================="+admin.getSSID());
                admin.updateWifiInfo();
                wifiname.setText(admin.getSSID());
            }

            @Override
            public void onNotifyWifiCloseed() {
                wifiname.setText("");
            }
        };
        wifi_sw= (SwitchButton) findViewById(R.id.wifi_sw);
        if (admin.isWifiContected(getApplicationContext())==WifiAdmin.WIFI_CONNECTED){
            wifi_sw.setChecked(true);
        }else {
            wifi_sw.setChecked(false);
        }

        wifi_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    admin.openWifi();
                }else {
                    admin.closeWifi();
                }
            }
        });
        admin.startScan();
        mContext=this;
        admin.updateWifiInfo();
        wifiname.setText(admin.getSSID());

        ptrl.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                admin.startScan();
                slist = admin.getWifiList();
                adapter.setScanResults(slist);
                adapter.notifyDataSetChanged();
                Log.i(TAG, "onRefresh: ==================================="+admin.getSSID());
                admin.updateWifiInfo();
                wifiname.setText(admin.getSSID());

                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });
        ptrl.setPullDownEnable(true);
        ptrl.setPullUpEnable(false);
        recyclerView= (WrapRecyclerView) ptrl.getPullableView();

        slist = admin.getWifiList();
        adapter=new WifiAdapter(slist,this);
        recyclerView.setAdapter(adapter);
        Verticaldecoration = new BaseLinearDecoration(this, LinearLayoutManager.VERTICAL);
        Verticaldecoration.setmDivider(R.drawable.itme_divider);
        recyclerView.addItemDecoration(Verticaldecoration);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        RecyclerItemClick click=new RecyclerItemClick();
        adapter.setOnRecyclerItemClick(click);

        admin.register();

    }

    @Override
    protected void onStop() {
        super.onStop();
        admin.unRegister();
    }

    public class RecyclerItemClick extends OnRecyclerItemClick {

        @Override
        public void onClick(View veiw, int position) {

            result = slist.get(position);
            Log.i(TAG, "onClick: =============="+result.SSID);
            //获得配置好的信息列表
            confis = admin.getConfiguration();

            clickSSID = admin.IsConfiguration("\"" + result.SSID + "\"");

            if (clickSSID != -1) {
                //直接连接

                new CustomerDialog(mContext)
                        .builder()
                        .setTitle(result.SSID)
                        .setMsg(getResources().getString(
                                R.string.connect_wifi))
                        .setCancelable(true)
                        .setPositiveButton(
                                getResources().getString(
                                        R.string.connect_wifi),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        boolean isConn=admin.connectConfiguration(clickSSID);
                                    }
                                })
                        .setNegativeButton(
                                getResources()
                                        .getString(
                                                R.string.wifi_password_again),
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        setPassWord(result.SSID);

                                    }
                                }).show();

            } else {
                setPassWord(result.SSID);
            }


            Toast.makeText(getApplicationContext(), result.SSID, Toast.LENGTH_SHORT).show();

        }


        private void setPassWord(final String ssid) {

            // 没有配置好信息，配置
            new CustomerDialog(mContext, new CustomerDialog.CustomDialogListener() {

                @Override
                public void onConform(String str) {
                    // TODO Auto-generated method stub
                    wifiPassword = str;
                    if (wifiPassword != null) {
                        int netId = admin.AddWifiConfig(slist, ssid,
                                wifiPassword);
                        Log.e(TAG, "netId: " + netId);
                        if (netId != -1) {
                            confis = admin.getConfiguration();// 添加了配置信息，要重新得到配置信息?
                            if (admin.ConnectWifi(netId)) {

                            }
                        } else {
                            Toast.makeText(
                                    mContext,
                                    getResources().getString(
                                            R.string.wifi_password_error),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }).builder()
                    .setTitle(
                            getResources().getString(R.string.wifi_password_title))
                    .setEditMode(true)
                    .setCancelable(false)
                    .setPositiveButton(getResources().getString(R.string.conform),
                            null)
                    .setNegativeButton(getResources().getString(R.string.cancel),
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            }).show();
        }
    }

}
