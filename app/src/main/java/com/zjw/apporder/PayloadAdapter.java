package com.zjw.apporder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PayloadAdapter extends RecyclerView.Adapter<PayloadAdapter.PayloadVHolder> {


    private LayoutInflater mInflater;
    private List<String> datas;

    public PayloadAdapter(Context context, List<String> datas) {
        mInflater = LayoutInflater.from(context);
        this.datas = datas;
    }


    @NonNull
    @Override
    public PayloadVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("PayloadAdapter", "onCreateViewHolder");
        return new PayloadVHolder(mInflater.inflate(R.layout.payload_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PayloadVHolder holder, int position, @NonNull List<Object> payloads) {
        Log.d("PayloadAdapter", "onBindViewHolder payload: " + payloads);
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            holder.botTv.setText("text update by payload position: " + position);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PayloadVHolder holder, int position) {
        Log.d("PayloadAdapter", "onBindViewHolder position: " + position);
        holder.topTv.setText("position - " + position + "\n" + datas.get(position));
        holder.botTv.setText("");
    }

    @Override
    public int getItemCount() {
        return null == datas ? 0 : datas.size();
    }

    static class PayloadVHolder extends RecyclerView.ViewHolder {
        TextView topTv, botTv;

        public PayloadVHolder(View itemView) {
            super(itemView);
            topTv = itemView.findViewById(R.id.payload_top_tv);
            botTv = itemView.findViewById(R.id.payload_bot_tv);
        }
    }

}