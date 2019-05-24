package com.zjw.apporder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class PayloadActivity extends AppCompatActivity {

    RecyclerView payloadRc;
    Button payloadBtn;
    Button notifyBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rc_payload);

        payloadRc = findViewById(R.id.rc_view);

        final List<String> datas = new ArrayList<>(15);
        for (int i = 0; i < 15; i++) {
            datas.add("data - " + i);
        }
        final PayloadAdapter adapter = new PayloadAdapter(this, datas);
        payloadRc.setLayoutManager(new LinearLayoutManager(this));
        payloadRc.setAdapter(adapter);

        payloadRc.getItemAnimator().setChangeDuration(0);
        findViewById(R.id.payloadBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.notifyItemChanged(2, "payload");
            }
        });

        findViewById(R.id.notifyBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datas.set(2,"局部刷新2");
                adapter.notifyItemChanged(2);
            }
        });

        findViewById(R.id.notifyAllBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datas.clear();
                for (int i = 0; i < 15; i++) {
                    datas.add(" new - data - " + i);
                }
                Log.d("Payload", "notify all ");
                adapter.notifyDataSetChanged();
            }
        });
    }
}