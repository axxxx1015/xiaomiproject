package com.example.music_tttaaayyyx;

import android.os.Handler;
import android.os.Looper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LyricDownloader {
    
    private static final ExecutorService executor = Executors.newCachedThreadPool();
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    
    public interface LyricDownloadCallback {
        void onSuccess(String lyricContent);
        void onFailure(String error);
    }
    
    /**
     * 异步下载歌词文件
     * @param lyricUrl 歌词文件URL
     * @param callback 回调接口
     */
    public static void downloadLyric(String lyricUrl, LyricDownloadCallback callback) {
        executor.execute(() -> {
            try {
                String lyricContent = downloadLyricSync(lyricUrl);
                mainHandler.post(() -> callback.onSuccess(lyricContent));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onFailure(e.getMessage()));
            }
        });
    }
    
    /**
     * 同步下载歌词文件
     * @param lyricUrl 歌词文件URL
     * @return 歌词文件内容
     * @throws IOException 网络异常
     */
    private static String downloadLyricSync(String lyricUrl) throws IOException {
        URL url = new URL(lyricUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        
        try {
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder content = new StringBuilder();
                String line;
                
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                
                reader.close();
                inputStream.close();
                
                return content.toString();
            } else {
                throw new IOException("HTTP error: " + responseCode);
            }
        } finally {
            connection.disconnect();
        }
    }
} 