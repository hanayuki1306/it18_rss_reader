package it18.ehou.rssreaderapp.models;

import java.io.Serializable;

public class NewsItem implements Serializable {
    private String title;
    private String link;
    private String imageUrl;
    private String pubDate;

    public NewsItem() {
    }

    public NewsItem(String title, String link, String imageUrl, String pubDate) {
        this.title = title;
        this.link = link;
        this.imageUrl = imageUrl;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title != null ? title : "";
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link != null ? link : "";
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageUrl() {
        return imageUrl != null ? imageUrl : "";
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPubDate() {
        return pubDate != null ? pubDate : "";
    }

    public void setPubDate(String pubDate) {
        this.pubDate = pubDate;
    }
}