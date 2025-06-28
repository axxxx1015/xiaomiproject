package com.example.music_tttaaayyyx.network;

import java.util.List;

public class HomePageResponse {
    private int code;
    private String msg;
    private PageData data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public PageData getData() {
        return data;
    }

    public void setData(PageData data) {
        this.data = data;
    }

    public static class PageData {
        private List<HomePageInfo> records;
        private int total;
        private int size;
        private int current;
        private int pages;

        public List<HomePageInfo> getRecords() {
            return records;
        }

        public void setRecords(List<HomePageInfo> records) {
            this.records = records;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getSize() {
            return size;
        }

        public void setSize(int size) {
            this.size = size;
        }

        public int getCurrent() {
            return current;
        }

        public void setCurrent(int current) {
            this.current = current;
        }

        public int getPages() {
            return pages;
        }

        public void setPages(int pages) {
            this.pages = pages;
        }
    }

    public static class HomePageInfo {
        private int moduleConfigId;
        private String moduleName;
        private int style;
        private List<MusicInfo> musicInfoList;

        public int getModuleConfigId() {
            return moduleConfigId;
        }

        public void setModuleConfigId(int moduleConfigId) {
            this.moduleConfigId = moduleConfigId;
        }

        public String getModuleName() {
            return moduleName;
        }

        public void setModuleName(String moduleName) {
            this.moduleName = moduleName;
        }

        public int getStyle() {
            return style;
        }

        public void setStyle(int style) {
            this.style = style;
        }

        public List<MusicInfo> getMusicInfoList() {
            return musicInfoList;
        }

        public void setMusicInfoList(List<MusicInfo> musicInfoList) {
            this.musicInfoList = musicInfoList;
        }
    }

    public static class MusicInfo {
        private long id;
        private String musicName;
        private String author;
        private String coverUrl;
        private String musicUrl;
        private String lyricUrl;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public String getMusicName() {
            return musicName;
        }

        public void setMusicName(String musicName) {
            this.musicName = musicName;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getCoverUrl() {
            return coverUrl;
        }

        public void setCoverUrl(String coverUrl) {
            this.coverUrl = coverUrl;
        }

        public String getMusicUrl() {
            return musicUrl;
        }

        public void setMusicUrl(String musicUrl) {
            this.musicUrl = musicUrl;
        }

        public String getLyricUrl() {
            return lyricUrl;
        }

        public void setLyricUrl(String lyricUrl) {
            this.lyricUrl = lyricUrl;
        }
    }
} 