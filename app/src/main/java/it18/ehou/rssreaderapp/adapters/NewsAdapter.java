package it18.ehou.rssreaderapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it18.ehou.rssreaderapp.R;
import it18.ehou.rssreaderapp.models.NewsItem;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(NewsItem item, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(NewsItem item, int position);
    }

    private final Context context;
    private final List<NewsItem> items;
    private OnItemClickListener clickListener;
    private OnItemLongClickListener longClickListener;

    public NewsAdapter(Context context, List<NewsItem> items) {
        this.context = context;
        this.items = (items != null) ? items : new ArrayList<>();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void updateData(List<NewsItem> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size() - position);
        }
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_news, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        NewsItem item = items.get(position);
        holder.txtTitle.setText(item.getTitle());
        holder.txtDate.setText(item.getPubDate());

        // Sử dụng Glide tải ảnh an toàn, có placeholder và xóa view cũ khi URL rỗng
        String imageUrl = item.getImageUrl();
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.color.image_placeholder)
                    .error(R.color.image_placeholder)
                    .into(holder.imgThumbnail);
        } else {
            Glide.with(context).clear(holder.imgThumbnail);
            holder.imgThumbnail.setImageResource(R.color.image_placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(item, holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClick(item, holder.getAdapterPosition());
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        final TextView txtTitle;
        final TextView txtDate;
        final ImageView imgThumbnail;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtTitle);
            txtDate = itemView.findViewById(R.id.txtDate);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}