package com.example.music_tttaaayyyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.music_tttaaayyyx.LyricParser;
import com.example.music_tttaaayyyx.R;

import java.util.List;

public class LyricsAdapter extends RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder> {
    
    private Context context;
    private List<LyricParser.LyricLine> lyrics;
    private int currentIndex = -1;
    
    public LyricsAdapter(Context context, List<LyricParser.LyricLine> lyrics) {
        this.context = context;
        this.lyrics = lyrics;
    }
    
    @NonNull
    @Override
    public LyricsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_lyrics, parent, false);
        return new LyricsViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull LyricsViewHolder holder, int position) {
        if (lyrics != null && position < lyrics.size()) {
            LyricParser.LyricLine lyricLine = lyrics.get(position);
            holder.tvLyricText.setText(lyricLine.getText());
            
            // 设置高亮效果
            if (position == currentIndex) {
                holder.tvLyricText.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvLyricText.setTextSize(16);
                holder.tvLyricText.setAlpha(1.0f);
                holder.tvLyricText.setShadowLayer(3, 1, 1, ContextCompat.getColor(context, R.color.black));
            } else {
                holder.tvLyricText.setTextColor(ContextCompat.getColor(context, R.color.white));
                holder.tvLyricText.setTextSize(14);
                holder.tvLyricText.setAlpha(0.7f);
                holder.tvLyricText.setShadowLayer(2, 1, 1, ContextCompat.getColor(context, R.color.black));
            }
        } else {
            holder.tvLyricText.setText("");
        }
    }
    
    @Override
    public int getItemCount() {
        return lyrics != null ? lyrics.size() : 0;
    }
    
    /**
     * 更新当前高亮的歌词行
     * @param index 当前歌词行索引
     */
    public void setCurrentIndex(int index) {
        int oldIndex = this.currentIndex;
        this.currentIndex = index;
        
        // 只更新变化的行以提高性能
        if (oldIndex >= 0 && oldIndex < getItemCount()) {
            notifyItemChanged(oldIndex);
        }
        if (index >= 0 && index < getItemCount()) {
            notifyItemChanged(index);
        }
    }
    
    /**
     * 根据播放时间更新当前歌词
     * @param currentTime 当前播放时间（毫秒）
     */
    public void updateCurrentLyric(long currentTime) {
        if (lyrics != null) {
            int newIndex = LyricParser.getCurrentLyricIndex(lyrics, currentTime);
            if (newIndex != currentIndex) {
                setCurrentIndex(newIndex);
            }
        }
    }
    
    /**
     * 更新歌词数据
     * @param newLyrics 新的歌词数据
     */
    public void updateLyrics(List<LyricParser.LyricLine> newLyrics) {
        this.lyrics = newLyrics;
        this.currentIndex = -1;
        notifyDataSetChanged();
    }
    
    static class LyricsViewHolder extends RecyclerView.ViewHolder {
        TextView tvLyricText;
        
        public LyricsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLyricText = itemView.findViewById(R.id.tv_lyric_text);
        }
    }
} 