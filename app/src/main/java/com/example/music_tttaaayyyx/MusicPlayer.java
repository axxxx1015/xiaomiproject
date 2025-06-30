package com.example.music_tttaaayyyx;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class MusicPlayer {
    private static final String TAG = "MusicPlayer";
    private static MusicPlayer instance;
    private List<Music> currentPlaylist;
    private Music currentMusic;
    private int currentIndex;
    private boolean isPlaying;
    private int playMode = 0; // 0: 顺序播放, 1: 单曲循环, 2: 随机播放
    
    private MediaPlayer mediaPlayer;
    private Context context;
    private OnPlaybackStateChangeListener playbackListener;
    private Random random = new Random();

    public interface OnPlaybackStateChangeListener {
        void onPlaybackStateChanged(boolean isPlaying);
        void onMusicChanged(Music music);
    }

    private MusicPlayer() {
        this.currentIndex = 0;
        this.isPlaying = false;
        this.mediaPlayer = new MediaPlayer();
        setupMediaPlayer();
    }

    // 单例模式
    public static MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOnPlaybackStateChangeListener(OnPlaybackStateChangeListener listener) {
        this.playbackListener = listener;
    }

    private void setupMediaPlayer() {
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // 播放完成后根据播放模式处理
                handlePlaybackCompletion();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e(TAG, "MediaPlayer error: what=" + what + ", extra=" + extra);
                isPlaying = false;
                if (playbackListener != null) {
                    playbackListener.onPlaybackStateChanged(false);
                }
                return true;
            }
        });
    }
    
    private void handlePlaybackCompletion() {
        if (currentPlaylist == null || currentPlaylist.isEmpty()) {
            stop();
            return;
        }
        
        switch (playMode) {
            case 0: // 顺序播放
                if (currentIndex < currentPlaylist.size() - 1) {
                    currentIndex++;
                } else {
                    currentIndex = 0; // 循环到第一首
                }
                break;
            case 1: // 单曲循环
                // 继续播放当前歌曲
                break;
            case 2: // 随机播放
                currentIndex = generateRandomIndex();
                break;
        }
        
        if (currentIndex < currentPlaylist.size()) {
            currentMusic = currentPlaylist.get(currentIndex);
            play(currentMusic);
        }
    }
    
    private int generateRandomIndex() {
        if (currentPlaylist.size() <= 1) return 0;
        
        int newIndex;
        do {
            newIndex = random.nextInt(currentPlaylist.size());
        } while (newIndex == currentIndex && currentPlaylist.size() > 1);
        
        return newIndex;
    }

    // 播放音乐
    public void play(Music music) {
        if (music == null || music.getAudioUrl() == null || music.getAudioUrl().isEmpty()) {
            Log.w(TAG, "Music or audio URL is null/empty");
            return;
        }

        try {
            // 重置MediaPlayer
            mediaPlayer.reset();
            
            // 设置音频源
            if (music.getAudioUrl().startsWith("http")) {
                // 网络音频
                mediaPlayer.setDataSource(music.getAudioUrl());
            } else {
                // 本地音频文件
                if (context != null) {
                    Uri uri = Uri.parse(music.getAudioUrl());
                    mediaPlayer.setDataSource(context, uri);
                } else {
                    Log.e(TAG, "Context is null, cannot play local audio");
                    return;
                }
            }
            
            // 准备播放
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    isPlaying = true;
                    currentMusic = music;
                    
                    if (playbackListener != null) {
                        playbackListener.onPlaybackStateChanged(true);
                        playbackListener.onMusicChanged(music);
                    }
                    
                    Log.i(TAG, "正在播放: " + music.getTitle() + " - " + music.getArtist());
                }
            });
            
        } catch (IOException e) {
            Log.e(TAG, "Error playing music: " + e.getMessage());
            isPlaying = false;
            if (playbackListener != null) {
                playbackListener.onPlaybackStateChanged(false);
            }
        }
    }

    // 暂停播放
    public void pause() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPlaying = false;
            if (playbackListener != null) {
                playbackListener.onPlaybackStateChanged(false);
            }
            Log.i(TAG, "播放已暂停");
        }
    }

    // 继续播放
    public void resume() {
        if (!mediaPlayer.isPlaying() && currentMusic != null) {
            mediaPlayer.start();
            isPlaying = true;
            if (playbackListener != null) {
                playbackListener.onPlaybackStateChanged(true);
            }
            Log.i(TAG, "继续播放");
        }
    }

    // 停止播放
    public void stop() {
        mediaPlayer.stop();
        isPlaying = false;
        currentMusic = null;
        if (playbackListener != null) {
            playbackListener.onPlaybackStateChanged(false);
        }
        Log.i(TAG, "播放已停止");
    }

    // 播放下一首
    public void next() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            switch (playMode) {
                case 0: // 顺序播放
                case 1: // 单曲循环
                    if (currentIndex < currentPlaylist.size() - 1) {
                        currentIndex++;
                    } else {
                        currentIndex = 0;
                    }
                    break;
                case 2: // 随机播放
                    currentIndex = generateRandomIndex();
                    break;
            }
            currentMusic = currentPlaylist.get(currentIndex);
            play(currentMusic);
        }
    }

    // 播放上一首
    public void previous() {
        if (currentPlaylist != null && !currentPlaylist.isEmpty()) {
            switch (playMode) {
                case 0: // 顺序播放
                case 1: // 单曲循环
                    if (currentIndex > 0) {
                        currentIndex--;
                    } else {
                        currentIndex = currentPlaylist.size() - 1;
                    }
                    break;
                case 2: // 随机播放
                    currentIndex = generateRandomIndex();
                    break;
            }
            currentMusic = currentPlaylist.get(currentIndex);
            play(currentMusic);
        }
    }

    // 设置播放列表
    public void setPlaylist(List<Music> playlist) {
        this.currentPlaylist = playlist;
        this.currentIndex = 0;
        if (!playlist.isEmpty()) {
            this.currentMusic = playlist.get(0);
        }
        if (playbackListener != null && currentMusic != null) {
            playbackListener.onMusicChanged(currentMusic);
        }
    }

    // 设置播放模式
    public void setPlayMode(int mode) {
        this.playMode = mode;
        Log.i(TAG, "播放模式设置为: " + mode);
    }

    // 获取当前播放位置
    public int getCurrentPosition() {
        return mediaPlayer.getCurrentPosition();
    }

    // 获取总时长
    public int getDuration() {
        return mediaPlayer.getDuration();
    }

    // 跳转到指定位置
    public void seekTo(int position) {
        mediaPlayer.seekTo(position);
    }

    // 释放资源
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    // Getters
    public Music getCurrentMusic() { return currentMusic; }
    public boolean isPlaying() { return isPlaying; }
    public int getCurrentIndex() { return currentIndex; }
    public List<Music> getCurrentPlaylist() { return currentPlaylist; }
    public int getPlayMode() { return playMode; }

    // 获取播放状态信息
    public String getStatus() {
        if (currentMusic == null) {
            return "未播放";
        }
        return (isPlaying ? "正在播放" : "已暂停") + ": " + 
               currentMusic.getTitle() + " - " + currentMusic.getArtist();
    }
} 