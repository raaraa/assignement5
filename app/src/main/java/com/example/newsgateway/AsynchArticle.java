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

public class AsynchArticle extends AsyncTask<String, Void, String> {
    private ServiceNews serviceNews;

    public AsynchArticle(ServiceNews serviceNews) {
        this.serviceNews = serviceNews;
    }

    @Override
    protected String doInBackground(String... params) {
        String api_url = "https://newsapi.org/v2/everything?language=en&pageSize=100&apiKey=31d8e8132c5e498c81fb129290904f3d&sources=" + params[0];
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
            JSONArray jsonArray = jsonObject.getJSONArray("articles");
            for (int i =0; i<jsonArray.length(); i++){
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String author = jsonObject1.getString("author");
                String title = jsonObject1.getString("title");
                String description = jsonObject1.getString("description");
                String url = jsonObject1.getString("url");
                String urlToImage = jsonObject1.getString("urlToImage");
                String publishedAt = jsonObject1.getString("publishedAt");

                serviceNews.addArticle(new Article(author, title, description, url, urlToImage, publishedAt, jsonArray.length(), i));
                if (isCancelled()) return;
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
