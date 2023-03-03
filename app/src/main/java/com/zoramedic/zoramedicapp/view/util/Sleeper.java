package com.zoramedic.zoramedicapp.view.util;

import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zoramedic.zoramedicapp.data.Message;

import java.util.ArrayList;
import java.util.List;

public class Sleeper {

    private TextView emptyTV;
    private ProgressBar loading;
    private List list;
    private List list2;

    public Sleeper(TextView emptyTV, ProgressBar loading, List list, List list2) {
        this.emptyTV = emptyTV;
        this.loading = loading;
        this.list = list;
        this.list2 = list2;
    }


    public void start() {
        loading.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                } // Just catch the InterruptedException
                handler.post(new Runnable() {
                    public void run() {
                        if (list.isEmpty() && list2.isEmpty()) {
                            loading.setVisibility(View.GONE);
                            if (loading.getVisibility() == View.GONE) {
                                emptyTV.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                });
            }
        }).start();
    }
}
