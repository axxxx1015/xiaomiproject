package com.example.music_tttaaayyyx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.R;
import com.example.music_tttaaayyyx.network.HomePageResponse.MusicInfo;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {
    
    private List<MusicInfo> musicList;
    private OnBannerClickListener listener;
    
    public BannerAdapter(List<MusicInfo> musicList) {
        this.musicList = musicList;
    }
    
    public void setOnBannerClickListener(OnBannerClickListener listener) {
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        MusicInfo music = musicList.get(position % musicList.size());
        holder.bind(music);
    }
    
    @Override
    public int getItemCount() {
        return musicList.size() > 1 ? Integer.MAX_VALUE : musicList.size();
    }
    
    class BannerViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        
        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_banner);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        MusicInfo music = musicList.get(position % musicList.size());
                        listener.onBannerClick(music);
                    }
                }
            });
        }
        
        public void bind(MusicInfo music) {
            Glide.with(imageView.getContext())
                    .load(music.getCoverUrl())
                    .placeholder(R.drawable.placeholder_banner)
                    .error(R.drawable.placeholder_banner)
                    .into(imageView);
        }
    }
    
    public interface OnBannerClickListener {
        void onBannerClick(MusicInfo music);
    }
} 