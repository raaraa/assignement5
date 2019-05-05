package com.example.newsgateway;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class AsynchSource extends AsyncTask<String, Void, String> {

    private MainActivity mainActivity;

    public AsynchSource(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

}
