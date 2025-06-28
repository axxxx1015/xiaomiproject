package com.example.music_tttaaayyyx;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String id;
    private String name;
    private String description;
    private String coverUrl;
    private List<Music> musicList;
    private int musicCount;
    private boolean isPublic;

    public Playlist(String id, String name, String description, String coverUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.coverUrl = coverUrl;
        this.musicList = new ArrayList<>();
        this.musicCount = 0;
        this.isPublic = true;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }

    public List<Music> getMusicList() { return musicList; }
    public void setMusicList(List<Music> musicList) { 
        this.musicList = musicList; 
        this.musicCount = musicList.size();
    }

    public int getMusicCount() { return musicCount; }
    public void setMusicCount(int musicCount) { this.musicCount = musicCount; }

    public boolean isPublic() { return isPublic; }
    public void setPublic(boolean aPublic) { isPublic = aPublic; }

    // 添加音乐到播放列表
    public void addMusic(Music music) {
        if (!musicList.contains(music)) {
            musicList.add(music);
            musicCount++;
        }
    }

    // 从播放列表移除音乐
    public void removeMusic(Music music) {
        if (musicList.remove(music)) {
            musicCount--;
        }
    }

    // 清空播放列表
    public void clearPlaylist() {
        musicList.clear();
        musicCount = 0;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", musicCount=" + musicCount +
                ", isPublic=" + isPublic +
                '}';
    }
} 