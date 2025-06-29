package com.example.music_tttaaayyyx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.LyricParser;
import com.example.music_tttaaayyyx.Music;
import com.example.music_tttaaayyyx.R;
import com.example.music_tttaaayyyx.LyricDownloader;
import com.example.music_tttaaayyyx.SampleLyrics;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayerPagerAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<MusicPlayerPagerAdapter.ViewHolder> {
    
    private static final int VIEW_TYPE_COVER = 0;
    private static final int VIEW_TYPE_LYRICS = 1;
    
    private Context context;
    private Music currentMusic;
    private ImageView albumCover;
    private LyricsAdapter lyricsAdapter;
    private List<LyricParser.LyricLine> currentLyrics;
    
    public MusicPlayerPagerAdapter(Context context) {
        this.context = context;
        this.currentLyrics = new ArrayList<>();
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_LYRICS) {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_lyrics_view, parent, false);
            return new ViewHolder(view, viewType);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.fragment_cover_view, parent, false);
            // 立即设置albumCover引用
            ViewHolder holder = new ViewHolder(view, viewType);
            albumCover = holder.ivAlbumCover;
            return holder;
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position == VIEW_TYPE_LYRICS) {
            bindLyricsView(holder);
        } else {
            bindCoverView(holder);
        }
    }
    
    @Override
    public int getItemCount() {
        return 2; // 歌词视图和封面视图
    }
    
    @Override
    public int getItemViewType(int position) {
        return position;
    }
    
    private void bindLyricsView(ViewHolder holder) {
        if (currentMusic != null) {
            // 设置歌曲信息
            holder.tvLyricsSongTitle.setText(currentMusic.getTitle());
            holder.tvLyricsArtistName.setText(currentMusic.getArtist());
        } else {
            // 设置默认内容
            holder.tvLyricsSongTitle.setText("歌曲名称");
            holder.tvLyricsArtistName.setText("歌手名称");
        }
        
        // 设置歌词列表
        setupLyricsList(holder.rvLyrics);
    }
    
    private void bindCoverView(ViewHolder holder) {
        if (currentMusic != null) {
            // 设置歌曲信息
            holder.tvSongTitle.setText(currentMusic.getTitle());
            holder.tvArtistName.setText(currentMusic.getArtist());
            
            // 设置封面图片
            if (currentMusic.getCoverUrl() != null && !currentMusic.getCoverUrl().isEmpty()) {
                Glide.with(context)
                    .load(currentMusic.getCoverUrl())
                    .placeholder(R.drawable.placeholder_music)
                    .error(R.drawable.placeholder_music)
                    .into(holder.ivAlbumCover);
            }
            
            albumCover = holder.ivAlbumCover;
        } else {
            // 设置默认内容
            holder.tvSongTitle.setText("歌曲名称");
            holder.tvArtistName.setText("歌手名称");
            holder.ivAlbumCover.setImageResource(R.drawable.placeholder_music);
            albumCover = holder.ivAlbumCover;
        }
    }
    
    private void setupLyricsList(RecyclerView recyclerView) {
        // 使用新的歌词适配器
        if (lyricsAdapter == null) {
            lyricsAdapter = new LyricsAdapter(context, currentLyrics);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(lyricsAdapter);
        } else {
            lyricsAdapter.updateLyrics(currentLyrics);
        }
    }
    
    public void updateMusicInfo(Music music) {
        this.currentMusic = music;
        
        // 加载歌词
        if (music != null && music.getLyricUrl() != null && !music.getLyricUrl().isEmpty()) {
            loadLyrics(music.getLyricUrl());
        } else {
            // 使用示例歌词
            currentLyrics = LyricParser.generateSampleLyrics();
            if (lyricsAdapter != null) {
                lyricsAdapter.updateLyrics(currentLyrics);
            }
        }
        
        notifyDataSetChanged();
    }
    
    /**
     * 加载歌词文件
     * @param lyricUrl 歌词文件URL
     */
    private void loadLyrics(String lyricUrl) {
        // 先显示包含制作信息的示例歌词
        String sampleLrc = SampleLyrics.getRealisticLrcWithCredits();
        currentLyrics = LyricParser.parseLrc(sampleLrc);
        if (lyricsAdapter != null) {
            lyricsAdapter.updateLyrics(currentLyrics);
        }
        
        // 如果有歌词URL，尝试异步下载真实歌词
        if (lyricUrl != null && !lyricUrl.isEmpty()) {
            LyricDownloader.downloadLyric(lyricUrl, new LyricDownloader.LyricDownloadCallback() {
                @Override
                public void onSuccess(String lyricContent) {
                    // 解析歌词
                    List<LyricParser.LyricLine> parsedLyrics = LyricParser.parseLrc(lyricContent);
                    
                    // 如果解析成功，更新歌词
                    if (parsedLyrics != null && !parsedLyrics.isEmpty()) {
                        currentLyrics = parsedLyrics;
                        if (lyricsAdapter != null) {
                            lyricsAdapter.updateLyrics(currentLyrics);
                        }
                    }
                }
                
                @Override
                public void onFailure(String error) {
                    // 下载失败，保持示例歌词
                    // 可以在这里添加错误处理逻辑
                }
            });
        }
    }
    
    /**
     * 更新当前播放时间对应的歌词
     * @param currentTime 当前播放时间（毫秒）
     */
    public void updateCurrentLyric(long currentTime) {
        if (lyricsAdapter != null) {
            lyricsAdapter.updateCurrentLyric(currentTime);
        }
    }
    
    public ImageView getAlbumCover() {
        return albumCover;
    }
    
    public LyricsAdapter getLyricsAdapter() {
        return lyricsAdapter;
    }
    
    static class ViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        // 封面视图的组件
        ImageView ivAlbumCover;
        TextView tvSongTitle;
        TextView tvArtistName;
        
        // 歌词视图的组件
        TextView tvLyricsSongTitle;
        TextView tvLyricsArtistName;
        RecyclerView rvLyrics;
        
        public ViewHolder(@NonNull View itemView, int viewType) {
            super(itemView);
            
            if (viewType == VIEW_TYPE_LYRICS) {
                tvLyricsSongTitle = itemView.findViewById(R.id.tv_lyrics_song_title);
                tvLyricsArtistName = itemView.findViewById(R.id.tv_lyrics_artist_name);
                rvLyrics = itemView.findViewById(R.id.rv_lyrics);
            } else {
                ivAlbumCover = itemView.findViewById(R.id.iv_album_cover);
                tvSongTitle = itemView.findViewById(R.id.tv_song_title);
                tvArtistName = itemView.findViewById(R.id.tv_artist_name);
            }
        }
    }
} 