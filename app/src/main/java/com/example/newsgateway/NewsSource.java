package com.example.newsgateway;

public class NewsSource {
    private String id;
    private String name;
    private String category;
    private String url;


    public NewsSource(String id, String name, String category, String url) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getId() {
        return id;
    }
}
