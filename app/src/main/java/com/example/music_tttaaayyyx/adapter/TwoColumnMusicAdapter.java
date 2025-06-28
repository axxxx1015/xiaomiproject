package com.example.music_tttaaayyyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.R;
import com.example.music_tttaaayyyx.network.HomePageResponse;

import java.util.List;

public class TwoColumnMusicAdapter extends RecyclerView.Adapter<TwoColumnMusicAdapter.ViewHolder> {

    private List<HomePageResponse.MusicInfo> musicList;
    private Context context;
    private OnMusicClickListener musicClickListener;
    private OnAddToPlaylistListener addToPlaylistListener;

    public interface OnMusicClickListener {
        void onMusicClick(HomePageResponse.MusicInfo music);
    }

    public interface OnAddToPlaylistListener {
        void onAddToPlaylist(HomePageResponse.MusicInfo music);
    }

    public TwoColumnMusicAdapter(List<HomePageResponse.MusicInfo> musicList) {
        this.musicList = musicList;
    }

    public void setOnMusicClickListener(OnMusicClickListener listener) {
        this.musicClickListener = listener;
    }

    public void setOnAddToPlaylistListener(OnAddToPlaylistListener listener) {
        this.addToPlaylistListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_two_column_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomePageResponse.MusicInfo music = musicList.get(position);
        
        holder.tvTitle.setText(music.getMusicName());
        holder.tvArtist.setText(music.getAuthor());

        // 加载图片
        Glide.with(context)
                .load(music.getCoverUrl())
                .placeholder(R.drawable.placeholder_music)
                .error(R.drawable.placeholder_music)
                .into(holder.ivCover);

        // 点击事件
        holder.itemView.setOnClickListener(v -> {
            if (musicClickListener != null) {
                musicClickListener.onMusicClick(music);
            }
        });

        holder.btnPlay.setOnClickListener(v -> {
            if (musicClickListener != null) {
                musicClickListener.onMusicClick(music);
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            if (addToPlaylistListener != null) {
                addToPlaylistListener.onAddToPlaylist(music);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;
        TextView tvArtist;
        ImageButton btnPlay;
        ImageButton btnAdd;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            btnPlay = itemView.findViewById(R.id.btn_play);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
    }
} 