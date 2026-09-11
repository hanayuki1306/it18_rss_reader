package it18.ehou.rssreaderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.models.NewsItem;

public class NewsAdapter extends ArrayAdapter<NewsItem> {
    public NewsAdapter(Context context, List<NewsItem> items) {
        super(context, 0, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_news, parent, false);
        }
        NewsItem item = getItem(position);

        TextView txtTitle = convertView.findViewById(R.id.txtTitle);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        ImageView imgThumbnail = convertView.findViewById(R.id.imgThumbnail);

        txtTitle.setText(item.title);
        txtDate.setText(item.pubDate);

        // Sử dụng thư viện Glide tải ảnh từ URL internet
        if (item.imageUrl != null && !item.imageUrl.isEmpty()) {
            Glide.with(getContext()).load(item.imageUrl).into(imgThumbnail);
        }

        return convertView;
    }
}