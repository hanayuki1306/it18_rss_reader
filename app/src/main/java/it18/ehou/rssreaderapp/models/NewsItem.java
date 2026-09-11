package it18.ehou.rssreaderapp.models;

public class NewsItem {
    public String title;
    public String link;
    public String imageUrl;
    public String pubDate;

    public NewsItem(String title, String link, String imageUrl, String pubDate) {
        this.title = title;
        this.link = link;
        this.imageUrl = imageUrl;
        this.pubDate = pubDate;
    }
}