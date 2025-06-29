package com.example.music_tttaaayyyx;

import com.example.music_tttaaayyyx.network.HomePageResponse;

import java.util.ArrayList;
import java.util.List;

public class MusicDataManager {
    private static MusicDataManager instance;
    private List<Music> allMusic;
    private List<Playlist> userPlaylists;
    private List<Music> favoriteMusic;

    private MusicDataManager() {
        allMusic = new ArrayList<>();
        userPlaylists = new ArrayList<>();
        favoriteMusic = new ArrayList<>();
        initializeSampleData();
    }

    // 单例模式
    public static MusicDataManager getInstance() {
        if (instance == null) {
            instance = new MusicDataManager();
        }
        return instance;
    }

    // 初始化示例数据
    private void initializeSampleData() {
        // 添加示例音乐
        allMusic.add(new Music("1", "夜曲", "周杰伦", "十一月的萧邦", "3:45", "", "", ""));
        allMusic.add(new Music("2", "稻香", "周杰伦", "魔杰座", "3:42", "", "", ""));
        allMusic.add(new Music("3", "青花瓷", "周杰伦", "我很忙", "3:59", "", "", ""));
        allMusic.add(new Music("4", "告白气球", "周杰伦", "周杰伦的床边故事", "3:35", "", "", ""));
        allMusic.add(new Music("5", "晴天", "周杰伦", "叶惠美", "4:29", "", "", ""));
        allMusic.add(new Music("6", "七里香", "周杰伦", "七里香", "4:59", "", "", ""));
        allMusic.add(new Music("7", "简单爱", "周杰伦", "范特西", "4:30", "", "", ""));
        allMusic.add(new Music("8", "双截棍", "周杰伦", "范特西", "3:20", "", "", ""));

        // 创建示例播放列表
        Playlist playlist1 = new Playlist("1", "我的最爱", "我最喜欢的音乐", "");
        playlist1.addMusic(allMusic.get(0));
        playlist1.addMusic(allMusic.get(1));
        playlist1.addMusic(allMusic.get(2));

        Playlist playlist2 = new Playlist("2", "经典老歌", "经典怀旧音乐", "");
        playlist2.addMusic(allMusic.get(3));
        playlist2.addMusic(allMusic.get(4));
        playlist2.addMusic(allMusic.get(5));

        userPlaylists.add(playlist1);
        userPlaylists.add(playlist2);
    }

    /**
     * 从网络响应创建Music对象列表
     * @param response 网络响应数据
     * @return Music对象列表
     */
    public List<Music> createMusicFromResponse(HomePageResponse response) {
        List<Music> musicList = new ArrayList<>();
        
        if (response != null && response.getData() != null && response.getData().getRecords() != null) {
            for (HomePageResponse.HomePageInfo module : response.getData().getRecords()) {
                if (module.getMusicInfoList() != null) {
                    for (HomePageResponse.MusicInfo musicInfo : module.getMusicInfoList()) {
                        Music music = new Music(
                            String.valueOf(musicInfo.getId()),
                            musicInfo.getMusicName(),
                            musicInfo.getAuthor(),
                            "", // 专辑信息暂时为空
                            "", // 时长信息暂时为空
                            musicInfo.getCoverUrl(),
                            musicInfo.getMusicUrl(),
                            musicInfo.getLyricUrl()
                        );
                        musicList.add(music);
                    }
                }
            }
        }
        
        return musicList;
    }

    /**
     * 更新音乐数据（从网络获取）
     * @param response 网络响应数据
     */
    public void updateMusicFromNetwork(HomePageResponse response) {
        List<Music> networkMusic = createMusicFromResponse(response);
        if (!networkMusic.isEmpty()) {
            allMusic.clear();
            allMusic.addAll(networkMusic);
        }
    }

    // 获取所有音乐
    public List<Music> getAllMusic() {
        return new ArrayList<>(allMusic);
    }

    // 根据关键词搜索音乐
    public List<Music> searchMusic(String keyword) {
        List<Music> results = new ArrayList<>();
        for (Music music : allMusic) {
            if (music.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                music.getArtist().toLowerCase().contains(keyword.toLowerCase()) ||
                music.getAlbum().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(music);
            }
        }
        return results;
    }

    // 获取用户播放列表
    public List<Playlist> getUserPlaylists() {
        return new ArrayList<>(userPlaylists);
    }

    // 创建新播放列表
    public Playlist createPlaylist(String name, String description) {
        String id = String.valueOf(userPlaylists.size() + 1);
        Playlist playlist = new Playlist(id, name, description, "");
        userPlaylists.add(playlist);
        return playlist;
    }

    // 删除播放列表
    public boolean deletePlaylist(String playlistId) {
        return userPlaylists.removeIf(playlist -> playlist.getId().equals(playlistId));
    }

    // 获取收藏的音乐
    public List<Music> getFavoriteMusic() {
        return new ArrayList<>(favoriteMusic);
    }

    // 添加音乐到收藏
    public void addToFavorites(Music music) {
        if (!favoriteMusic.contains(music)) {
            favoriteMusic.add(music);
            music.setLiked(true);
        }
    }

    // 从收藏中移除音乐
    public void removeFromFavorites(Music music) {
        if (favoriteMusic.remove(music)) {
            music.setLiked(false);
        }
    }

    // 切换音乐收藏状态
    public void toggleFavorite(Music music) {
        if (music.isLiked()) {
            removeFromFavorites(music);
        } else {
            addToFavorites(music);
        }
    }

    // 获取推荐音乐（这里简单返回前5首）
    public List<Music> getRecommendedMusic() {
        List<Music> recommended = new ArrayList<>();
        int count = Math.min(5, allMusic.size());
        for (int i = 0; i < count; i++) {
            recommended.add(allMusic.get(i));
        }
        return recommended;
    }

    // 获取热门音乐（按播放次数排序）
    public List<Music> getPopularMusic() {
        List<Music> popular = new ArrayList<>(allMusic);
        popular.sort((m1, m2) -> Integer.compare(m2.getPlayCount(), m1.getPlayCount()));
        return popular;
    }

    // 增加音乐播放次数
    public void incrementPlayCount(Music music) {
        music.setPlayCount(music.getPlayCount() + 1);
    }
} 