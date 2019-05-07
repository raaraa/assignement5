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


    @Override
    protected String doInBackground(String... params) {
        String api_url = "https://newsapi.org/v2/sources?language=en&country=us&apiKey=31d8e8132c5e498c81fb129290904f3d&category=" + params[0];
        try {
            URL url = new URL(api_url);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream inputStream = conn.getInputStream();
            BufferedReader bufferedReader = new BufferedReader((new InputStreamReader(inputStream)));
            return bufferedReader.readLine();
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (ProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray jsonArray = jsonObject.getJSONArray("sources");
            mainActivity.Clearout();

            for (int i =0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String id = jsonObject1.getString("id");
                String name = jsonObject1.getString("name");
                String url = jsonObject1.getString("url");
                String category = jsonObject1.getString("category");

                NewsSource newsSource = new NewsSource(id, name, url, category);
                mainActivity.addSource(newsSource);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
