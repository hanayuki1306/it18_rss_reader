package it18.ehou.rssreaderapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.activities.DetailActivity;
import it18.ehou.rssreaderapp.adapters.NewsAdapter;
import it18.ehou.rssreaderapp.models.NewsItem;

public class CategoryFragment extends Fragment {

    private static final String TAG = "RSS_DEBUG";
    private static final String ARG_RSS_URL = "rss_url";

    private String rssUrl;
    private ListView listView;
    private ArrayList<NewsItem> newsList;
    private NewsAdapter adapter;

    public CategoryFragment() {
        // Constructor rỗng bắt buộc của Fragment
    }

    public static CategoryFragment newInstance(String rssUrl) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RSS_URL, rssUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newsList = new ArrayList<>();
        if (getArguments() != null) {
            rssUrl = getArguments().getString(ARG_RSS_URL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        listView = view.findViewById(R.id.listView);

        adapter = new NewsAdapter(requireContext(), newsList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            if (position >= 0 && position < newsList.size()) {
                NewsItem selectedItem = newsList.get(position);
                Intent intent = new Intent(requireContext(), DetailActivity.class);
                intent.putExtra("ARTICLE_LINK", selectedItem.link);
                intent.putExtra("ARTICLE_TITLE", selectedItem.title);
                intent.putExtra("ARTICLE_IMAGE", selectedItem.imageUrl); // Bổ sung truyền ảnh
                intent.putExtra("ARTICLE_DATE", selectedItem.pubDate); // Bổ sung truyền ngày
                startActivity(intent);
            }
        });

        // Chỉ tải dữ liệu nếu danh sách đang rỗng để tránh tải lại không cần thiết
        if (newsList.isEmpty()) {
            fetchRssData(rssUrl);
        }

        return view;
    }

    private void fetchRssData(String url) {
        if (url == null || url.trim().isEmpty()) return;

        new Thread(() -> {
            try {
                Log.d(TAG, "Đang tải URL: " + url);

                Document doc = Jsoup.connect(url)
                        .userAgent("Mozilla/5.0 (Linux; Android 14; Pixel 8) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36")
                        .referrer("https://vnexpress.net/")
                        .timeout(15000)
                        .ignoreHttpErrors(true)
                        .get();

                Elements items = doc.select("item");
                Log.d(TAG, "Số bài viết tìm thấy: " + items.size() + " cho URL: " + url);

                ArrayList<NewsItem> tempList = new ArrayList<>();

                for (Element item : items) {
                    String title = item.select("title").text();
                    String link = item.select("link").text();
                    String pubDate = item.select("pubDate").text();

                    String imgUrl = "";
                    String description = item.select("description").text();
                    if (description != null && !description.isEmpty()) {
                        Document descDoc = Jsoup.parse(description);
                        Element imgElement = descDoc.selectFirst("img");
                        if (imgElement != null) {
                            imgUrl = imgElement.attr("src");
                        }
                    }

                    if (imgUrl.isEmpty()) {
                        Element enclosure = item.selectFirst("enclosure");
                        if (enclosure != null) {
                            imgUrl = enclosure.attr("url");
                        }
                    }

                    tempList.add(new NewsItem(title, link, imgUrl, pubDate));
                }

                // Cập nhật lại UI an toàn
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        newsList.clear();
                        newsList.addAll(tempList);
                        if (adapter != null) {
                            adapter.notifyDataSetChanged();
                        }
                        if (listView != null) {
                            listView.invalidateViews();
                        }
                        Log.d(TAG, "Đã cập nhật UI thành công cho: " + url);
                    });
                }

            } catch (Exception e) {
                Log.e(TAG, "Lỗi tải RSS cho URL: " + url, e);
            }
        }).start();
    }
}