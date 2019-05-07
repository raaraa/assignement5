package com.example.newsgateway;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;

public class ServiceNews extends Service {
    private ArrayList<Article> aricle_list;
    private ServiceReceiver serviceReceiver;
    private boolean running = true;
    private ServiceNews serviceNews;
    private static final String TAG = "SampleService";

    @Override
    public IBinder onBind(Intent arg0) {
        Log.i(TAG, "Service onBind");
        return null;  //  Don't want clients to bind to the service
    }

    public ServiceNews() {
        serviceNews = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        aricle_list = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                serviceReceiver = new ServiceReceiver();
                registerReceiver(serviceReceiver, new IntentFilter(MainActivity.REQUEST_ARTICLES));

                while (running){
                    if (aricle_list.size() == 0 || aricle_list.size() != aricle_list.get(0).getTotal()){
                        try {
                            Thread.sleep(200);
                            continue;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Intent res_intent = new Intent();
                        res_intent.setAction(MainActivity.RESPONSE_ARTICLES);
                        res_intent.putExtra("articles", aricle_list);
                        sendBroadcast(res_intent);
                        aricle_list.clear();
                    }
                }
            }
        }).start();
        return Service.START_STICKY;
    }

    public void addArticle(Article article){
        aricle_list.add(article);
    }


    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            AsynchArticle asyncArticle = new AsynchArticle(serviceNews);
            asyncArticle.execute(intent.getStringExtra("source"));
        }
    }
}
