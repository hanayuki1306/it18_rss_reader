package it18.ehou.rssreaderapp.network;

import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it18.ehou.rssreaderapp.models.NewsItem;

public class JsoupHtmlParser {
    private static final String TAG = "JsoupHtmlParser";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36";
    private static final int TIMEOUT_MS = 15000;

    /**
     * Tải và phân tích cú pháp RSS feed từ URL truyền vào.
     */
    public static List<NewsItem> parseRss(String url) throws IOException {
        List<NewsItem> itemsList = new ArrayList<>();
        if (url == null || url.trim().isEmpty()) {
            return itemsList;
        }

        Document doc = Jsoup.connect(url)
                .userAgent(USER_AGENT)
                .referrer("https://vnexpress.net/")
                .timeout(TIMEOUT_MS)
                .ignoreHttpErrors(true)
                .get();

        Elements items = doc.select("item");
        for (Element item : items) {
            String title = item.select("title").text();
            String link = item.select("link").text();
            String pubDate = item.select("pubDate").text();

            String imgUrl = "";

            // 1. Thử bóc tách ảnh từ thẻ description (cấu trúc phổ biến của VnExpress RSS)
            String description = item.select("description").text();
            if (description != null && !description.isEmpty()) {
                Document descDoc = Jsoup.parse(description);
                Element imgElement = descDoc.selectFirst("img");
                if (imgElement != null) {
                    imgUrl = imgElement.attr("src");
                }
            }

            // 2. Thử bóc tách từ thẻ enclosure (nếu có)
            if (imgUrl.isEmpty()) {
                Element enclosure = item.selectFirst("enclosure");
                if (enclosure != null) {
                    imgUrl = enclosure.attr("url");
                }
            }

            // 3. Thử bóc tách từ media:content
            if (imgUrl.isEmpty()) {
                Element mediaContent = item.selectFirst("media\\:content, content");
                if (mediaContent != null) {
                    imgUrl = mediaContent.attr("url");
                }
            }

            itemsList.add(new NewsItem(title, link, imgUrl, pubDate));
        }

        Log.d(TAG, "Đã parse thành công " + itemsList.size() + " bài viết từ " + url);
        return itemsList;
    }
}