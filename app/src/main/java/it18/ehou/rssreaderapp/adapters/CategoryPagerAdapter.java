package it18.ehou.rssreaderapp.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import it18.ehou.rssreaderapp.fragments.CategoryFragment;

public class CategoryPagerAdapter extends FragmentStateAdapter {

    // Danh sách các link RSS từ VnExpress
    private final String[] rssUrls = {
            "https://vnexpress.net/rss/giao-duc.rss",
            "https://vnexpress.net/rss/the-thao.rss",
            "https://vnexpress.net/rss/giai-tri.rss",
            "https://vnexpress.net/rss/du-lich.rss",
            "https://vnexpress.net/rss/suc-khoe.rss"
    };

    public CategoryPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về Fragment chứa List bài báo tương ứng với URL
        return CategoryFragment.newInstance(rssUrls[position]);
    }

    @Override
    public int getItemCount() {
        return rssUrls.length;
    }
}