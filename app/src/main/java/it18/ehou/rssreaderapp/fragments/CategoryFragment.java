package it18.ehou.rssreaderapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.activities.DetailActivity;
import it18.ehou.rssreaderapp.adapters.NewsAdapter;
import it18.ehou.rssreaderapp.models.NewsItem;
import it18.ehou.rssreaderapp.network.JsoupHtmlParser;

public class CategoryFragment extends Fragment {

    private static final String TAG = "CategoryFragment";
    private static final String ARG_RSS_URL = "rss_url";

    private String rssUrl;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView txtError;

    private final ArrayList<NewsItem> newsList = new ArrayList<>();
    private NewsAdapter adapter;
    private ExecutorService executorService;

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
        if (getArguments() != null) {
            rssUrl = getArguments().getString(ARG_RSS_URL);
        }
        executorService = Executors.newSingleThreadExecutor();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        progressBar = view.findViewById(R.id.progressBar);
        txtError = view.findViewById(R.id.txtError);

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new NewsAdapter(requireContext(), newsList);
        recyclerView.setAdapter(adapter);

        // Sự kiện click bài viết mở màn hình chi tiết
        adapter.setOnItemClickListener((item, position) -> {
            Intent intent = new Intent(requireContext(), DetailActivity.class);
            intent.putExtra("ARTICLE_LINK", item.getLink());
            intent.putExtra("ARTICLE_TITLE", item.getTitle());
            intent.putExtra("ARTICLE_IMAGE", item.getImageUrl());
            intent.putExtra("ARTICLE_DATE", item.getPubDate());
            startActivity(intent);
        });

        // Sự kiện vuốt xuống để làm mới danh sách tin
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_green);
        swipeRefreshLayout.setOnRefreshListener(() -> fetchRssData(rssUrl, false));

        // Chỉ tải dữ liệu nếu danh sách đang rỗng
        if (newsList.isEmpty()) {
            fetchRssData(rssUrl, true);
        }

        return view;
    }

    private void fetchRssData(String url, boolean showProgressBar) {
        if (url == null || url.trim().isEmpty()) return;

        if (showProgressBar && progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        if (txtError != null) {
            txtError.setVisibility(View.GONE);
        }

        if (executorService == null || executorService.isShutdown()) {
            executorService = Executors.newSingleThreadExecutor();
        }

        executorService.execute(() -> {
            try {
                Log.d(TAG, "Bắt đầu tải RSS từ: " + url);
                List<NewsItem> fetchedItems = JsoupHtmlParser.parseRss(url);

                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                        if (fetchedItems.isEmpty()) {
                            if (newsList.isEmpty() && txtError != null) {
                                txtError.setText(R.string.msg_load_error);
                                txtError.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (txtError != null) txtError.setVisibility(View.GONE);
                            adapter.updateData(fetchedItems);
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Lỗi khi tải RSS từ: " + url, e);
                if (isAdded() && getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        if (progressBar != null) progressBar.setVisibility(View.GONE);
                        if (swipeRefreshLayout != null) swipeRefreshLayout.setRefreshing(false);

                        if (newsList.isEmpty() && txtError != null) {
                            txtError.setText(R.string.msg_load_error);
                            txtError.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(requireContext(), R.string.msg_load_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
}