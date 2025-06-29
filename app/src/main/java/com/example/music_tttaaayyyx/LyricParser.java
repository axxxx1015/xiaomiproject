package com.example.music_tttaaayyyx;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LyricParser {
    
    public static class LyricLine {
        private long time; // 时间戳（毫秒）
        private String text; // 歌词文本
        
        public LyricLine(long time, String text) {
            this.time = time;
            this.text = text;
        }
        
        public long getTime() { return time; }
        public String getText() { return text; }
        
        @Override
        public String toString() {
            return "LyricLine{" +
                    "time=" + time +
                    ", text='" + text + '\'' +
                    '}';
        }
    }
    
    /**
     * 解析LRC格式的歌词
     * @param lrcContent LRC文件内容
     * @return 解析后的歌词列表
     */
    public static List<LyricLine> parseLrc(String lrcContent) {
        List<LyricLine> lyrics = new ArrayList<>();
        
        if (lrcContent == null || lrcContent.trim().isEmpty()) {
            return lyrics;
        }
        
        String[] lines = lrcContent.split("\n");
        Pattern timePattern = Pattern.compile("\\[(\\d{2}):(\\d{2})\\.(\\d{2})\\](.*)");
        
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;
            
            Matcher matcher = timePattern.matcher(line);
            if (matcher.find()) {
                int minutes = Integer.parseInt(matcher.group(1));
                int seconds = Integer.parseInt(matcher.group(2));
                int centiseconds = Integer.parseInt(matcher.group(3));
                String text = matcher.group(4).trim();
                
                // 转换为毫秒
                long time = (minutes * 60 + seconds) * 1000 + centiseconds * 10;
                
                if (!text.isEmpty()) {
                    lyrics.add(new LyricLine(time, text));
                }
            }
        }
        
        // 按时间排序
        lyrics.sort((a, b) -> Long.compare(a.getTime(), b.getTime()));
        
        return lyrics;
    }
    
    /**
     * 根据当前播放时间获取当前应该高亮的歌词行索引
     * @param lyrics 歌词列表
     * @param currentTime 当前播放时间（毫秒）
     * @return 当前歌词行索引，如果没有找到返回-1
     */
    public static int getCurrentLyricIndex(List<LyricLine> lyrics, long currentTime) {
        if (lyrics == null || lyrics.isEmpty()) {
            return -1;
        }
        
        for (int i = 0; i < lyrics.size(); i++) {
            if (lyrics.get(i).getTime() > currentTime) {
                return Math.max(0, i - 1);
            }
        }
        
        return lyrics.size() - 1;
    }
    
    /**
     * 生成示例歌词（当无法获取真实歌词时使用）
     * @return 示例歌词列表
     */
    public static List<LyricLine> generateSampleLyrics() {
        List<LyricLine> lyrics = new ArrayList<>();
        lyrics.add(new LyricLine(0, "这是一首示例歌曲"));
        lyrics.add(new LyricLine(3000, "歌词内容正在加载中"));
        lyrics.add(new LyricLine(6000, "请稍候..."));
        lyrics.add(new LyricLine(9000, ""));
        lyrics.add(new LyricLine(12000, "Verse 1"));
        lyrics.add(new LyricLine(15000, "这是第一段歌词"));
        lyrics.add(new LyricLine(18000, "示例内容"));
        lyrics.add(new LyricLine(21000, ""));
        lyrics.add(new LyricLine(24000, "Chorus"));
        lyrics.add(new LyricLine(27000, "这是副歌部分"));
        lyrics.add(new LyricLine(30000, "重复的旋律"));
        lyrics.add(new LyricLine(33000, ""));
        lyrics.add(new LyricLine(36000, "Verse 2"));
        lyrics.add(new LyricLine(39000, "这是第二段歌词"));
        lyrics.add(new LyricLine(42000, "不同的内容"));
        lyrics.add(new LyricLine(45000, ""));
        lyrics.add(new LyricLine(48000, "Bridge"));
        lyrics.add(new LyricLine(51000, "这是桥段"));
        lyrics.add(new LyricLine(54000, "过渡部分"));
        lyrics.add(new LyricLine(57000, ""));
        lyrics.add(new LyricLine(60000, "Final Chorus"));
        lyrics.add(new LyricLine(63000, "最后的副歌"));
        lyrics.add(new LyricLine(66000, "结束部分"));
        
        return lyrics;
    }
} 