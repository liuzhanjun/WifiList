package com.testwifi;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.testwifi.recyclerview.BaseRecyclerAdapter;
import com.testwifi.recyclerview.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yunniu on 2017/7/29.
 */

public class WifiAdapter extends BaseRecyclerAdapter{

    List<ScanResult> scanResults=new ArrayList<ScanResult>();
    Context context;

    public WifiAdapter(List<ScanResult> scanResults, Context context) {
        if (scanResults!=null) {
            this.scanResults = scanResults;
        }
        this.context = context;
    }


    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.wifi_item,parent,false);
        return new BaseViewHolder(view,onRecyclerItemClick,context);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
               TextView tv= (TextView) holder.itemView.findViewById(R.id.wifi_name);
        ScanResult result=scanResults.get(position);
        tv.setText(result.SSID);
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }
}
