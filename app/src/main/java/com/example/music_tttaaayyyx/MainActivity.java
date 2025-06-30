package com.example.music_tttaaayyyx;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.adapter.BannerAdapter;
import com.example.music_tttaaayyyx.adapter.MusicAdapter;
import com.example.music_tttaaayyyx.network.ApiService;
import com.example.music_tttaaayyyx.network.HomePageResponse;
import com.example.music_tttaaayyyx.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ViewPager2 viewPagerBanner;
    private CardView cardSearch;
    private RecyclerView recyclerView;
    private MusicAdapter musicAdapter;
    private BannerAdapter bannerAdapter;
    
    // 播放控制栏UI组件
    private ImageView ivCurrentCover;
    private TextView tvCurrentTitle;
    private TextView tvCurrentArtist;
    private ImageButton btnPlayPause;
    private ImageButton btnPlaylist;
    
    private List<HomePageResponse.HomePageInfo> homePageData = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    private SeekBar seekBarControl;
    private Handler progressHandler = new Handler(Looper.getMainLooper());
    private boolean isUserSeeking = false;
    private Runnable progressRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置MusicPlayer的Context和监听器
        setupMusicPlayer();
        
        initViews();
        setupListeners();
        loadHomePageData(true);
        // 启动进度条刷新
        startProgressUpdate();
    }

    private void initViews() {
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        viewPagerBanner = findViewById(R.id.viewpager_banner);
        cardSearch = findViewById(R.id.card_search);
        recyclerView = findViewById(R.id.recycler_view);
        
        // 初始化播放控制栏UI组件
        ivCurrentCover = findViewById(R.id.iv_current_cover);
        tvCurrentTitle = findViewById(R.id.tv_current_title);
        tvCurrentArtist = findViewById(R.id.tv_current_artist);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnPlaylist = findViewById(R.id.btn_playlist);
        
        // 初始化播放控制栏状态 - 隐藏初始文本
        tvCurrentTitle.setText("");
        tvCurrentArtist.setText("");
        
        // 设置RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        musicAdapter = new MusicAdapter();
        recyclerView.setAdapter(musicAdapter);
        
        // 设置Banner
        bannerAdapter = new BannerAdapter(new ArrayList<>());
        viewPagerBanner.setAdapter(bannerAdapter);
        
        // 设置Banner自动轮播
        setupBannerAutoScroll();

        // 新增进度条初始化
        seekBarControl = findViewById(R.id.seek_bar_control);
    }

    private void setupListeners() {
        // 搜索栏点击事件
        cardSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "搜索功能开发中...", Toast.LENGTH_SHORT).show();
            }
        });

        // 下拉刷新
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = 1;
                hasMoreData = true;
                loadHomePageData(true);
            }
        });

        // Banner点击事件
        bannerAdapter.setOnBannerClickListener(new BannerAdapter.OnBannerClickListener() {
            @Override
            public void onBannerClick(HomePageResponse.MusicInfo music) {
                playMusic(music);
            }
        });

        // 音乐列表点击事件
        musicAdapter.setOnMusicClickListener(new MusicAdapter.OnMusicClickListener() {
            @Override
            public void onMusicClick(HomePageResponse.MusicInfo music) {
                playMusic(music);
            }
        });

        // 添加到音乐列表事件
        musicAdapter.setOnAddToPlaylistListener(new MusicAdapter.OnAddToPlaylistListener() {
            @Override
            public void onAddToPlaylist(HomePageResponse.MusicInfo music) {
                addToPlaylist(music);
            }
        });

        // 上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMoreData && 
                    (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0) {
                    loadMoreData();
                }
            }
        });

        // 播放/暂停按钮点击事件
        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });

        // 播放列表按钮点击事件
        btnPlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlaylistDialog();
            }
        });
        
        // 播放控制栏点击事件 - 跳转到音乐播放页面
        findViewById(R.id.player_control_bar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer player = MusicPlayer.getInstance();
                if (player.getCurrentMusic() != null) {
                    // 跳转到音乐播放页面
                    Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
                    intent.putExtra("current_index", player.getCurrentIndex());
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "请先选择一首音乐", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 进度条拖动监听
        seekBarControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 拖动时不立即处理，松手时处理
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isUserSeeking = true;
            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int max = seekBar.getMax();
                int seekTo = 0;
                MusicPlayer player = MusicPlayer.getInstance();
                if (player.getCurrentMusic() != null && player.getDuration() > 0) {
                    seekTo = (int) (player.getDuration() * (seekBar.getProgress() / (float) max));
                    player.seekTo(seekTo);
                }
                isUserSeeking = false;
            }
        });
    }

    private void setupBannerAutoScroll() {
        final Handler handler = new Handler(Looper.getMainLooper());
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (bannerAdapter.getItemCount() > 1) {
                    int currentItem = viewPagerBanner.getCurrentItem();
                    viewPagerBanner.setCurrentItem(currentItem + 1);
                }
                handler.postDelayed(this, 3000); // 3秒自动轮播
            }
        };
        handler.postDelayed(runnable, 3000);
    }

    private void loadHomePageData(boolean isRefresh) {
        if (isLoading) return;
        
        isLoading = true;
        if (isRefresh) {
            currentPage = 1;
            hasMoreData = true;
        }

        ApiService apiService = RetrofitClient.getInstance().getApiService();
        Call<HomePageResponse> call = apiService.getHomePage(currentPage, 5);
        
        call.enqueue(new Callback<HomePageResponse>() {
            @Override
            public void onResponse(Call<HomePageResponse> call, Response<HomePageResponse> response) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    HomePageResponse homePageResponse = response.body();
                    
                    if (homePageResponse.getCode() == 200 && homePageResponse.getData() != null) {
                        List<HomePageResponse.HomePageInfo> newData = homePageResponse.getData().getRecords();
                        
                        if (isRefresh) {
                            homePageData.clear();
                        }
                        
                        homePageData.addAll(newData);
                        updateUI();
                        
                        // 检查是否还有更多数据
                        if (currentPage >= homePageResponse.getData().getPages()) {
                            hasMoreData = false;
                        }
                        currentPage++;
                    }
                }
            }

            @Override
            public void onFailure(Call<HomePageResponse> call, Throwable t) {
                isLoading = false;
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MainActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMoreData() {
        loadHomePageData(false);
    }

    private void updateUI() {
        // 过滤并重新组织数据，只显示指定的4个模块
        List<HomePageResponse.HomePageInfo> filteredData = new ArrayList<>();
        
        // 1. Banner (style = 1) - 只显示在顶部，不加入列表
        HomePageResponse.HomePageInfo bannerInfo = findModuleByStyle(homePageData, 1);
        if (bannerInfo != null) {
            // 更新Banner
            bannerAdapter = new BannerAdapter(bannerInfo.getMusicInfoList());
            viewPagerBanner.setAdapter(bannerAdapter);
            bannerAdapter.setOnBannerClickListener(new BannerAdapter.OnBannerClickListener() {
                @Override
                public void onBannerClick(HomePageResponse.MusicInfo music) {
                    playMusic(music);
                }
            });
        }
        
        // 2. 横滑大卡 (style = 2) - 专属好歌
        HomePageResponse.HomePageInfo horizontalInfo = findModuleByStyle(homePageData, 2);
        if (horizontalInfo != null) {
            horizontalInfo.setModuleName("专属好歌");
            filteredData.add(horizontalInfo);
        }
        
        // 3. 一行一列 (style = 3) - 每日推荐
        HomePageResponse.HomePageInfo singleColumnInfo = findModuleByStyle(homePageData, 3);
        if (singleColumnInfo != null) {
            singleColumnInfo.setModuleName("每日推荐");
            filteredData.add(singleColumnInfo);
        }
        
        // 4. 一行两列 (style = 4) - 热门金曲
        HomePageResponse.HomePageInfo twoColumnInfo = findModuleByStyle(homePageData, 4);
        if (twoColumnInfo != null) {
            twoColumnInfo.setModuleName("热门金曲");
            filteredData.add(twoColumnInfo);
        }
        
        // 更新音乐列表
        musicAdapter.updateData(filteredData);

        // 首次打开App，随机选择一个模块音乐播放
        SharedPreferences prefs = getSharedPreferences("music_app_prefs", MODE_PRIVATE);
        boolean hasInit = prefs.getBoolean("has_init_random_play", false);
        if (!hasInit) {
            List<HomePageResponse.HomePageInfo> modules = new ArrayList<>();
            if (horizontalInfo != null && horizontalInfo.getMusicInfoList() != null && !horizontalInfo.getMusicInfoList().isEmpty()) {
                modules.add(horizontalInfo);
            }
            if (singleColumnInfo != null && singleColumnInfo.getMusicInfoList() != null && !singleColumnInfo.getMusicInfoList().isEmpty()) {
                modules.add(singleColumnInfo);
            }
            if (twoColumnInfo != null && twoColumnInfo.getMusicInfoList() != null && !twoColumnInfo.getMusicInfoList().isEmpty()) {
                modules.add(twoColumnInfo);
            }
            if (!modules.isEmpty()) {
                int moduleIndex = (int) (Math.random() * modules.size());
                HomePageResponse.HomePageInfo selectedModule = modules.get(moduleIndex);
                List<HomePageResponse.MusicInfo> musicList = selectedModule.getMusicInfoList();
                if (musicList != null && !musicList.isEmpty()) {
                    int musicIndex = (int) (Math.random() * musicList.size());
                    HomePageResponse.MusicInfo selectedMusic = musicList.get(musicIndex);
                    // 将该模块所有音乐转为本地Music对象
                    List<Music> localList = new ArrayList<>();
                    for (HomePageResponse.MusicInfo info : musicList) {
                        localList.add(convertToLocalMusic(info));
                    }
                    // 设置播放列表
                    MusicPlayer.getInstance().setPlaylist(localList);
                    // 播放随机选中的音乐
                    MusicPlayer.getInstance().play(localList.get(musicIndex));
                    // 更新悬浮ViewUI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvCurrentTitle.setText(localList.get(musicIndex).getTitle());
                            tvCurrentArtist.setText(localList.get(musicIndex).getArtist());
                            btnPlayPause.setImageResource(R.drawable.ic_pause);
                            if (localList.get(musicIndex).getCoverUrl() != null && !localList.get(musicIndex).getCoverUrl().isEmpty()) {
                                Glide.with(MainActivity.this)
                                        .load(localList.get(musicIndex).getCoverUrl())
                                        .placeholder(R.drawable.placeholder_music)
                                        .error(R.drawable.placeholder_music)
                                        .into(ivCurrentCover);
                            }
                        }
                    });
                    // 标记已初始化
                    prefs.edit().putBoolean("has_init_random_play", true).apply();
                }
            }
        }
    }

    private HomePageResponse.HomePageInfo findModuleByStyle(List<HomePageResponse.HomePageInfo> data, int style) {
        for (HomePageResponse.HomePageInfo info : data) {
            if (info.getStyle() == style) {
                return info;
            }
        }
        return null;
    }

    private void playMusic(HomePageResponse.MusicInfo music) {
        // 将网络数据转换为本地Music对象
        Music localMusic = convertToLocalMusic(music);
        
        // 获取当前模块的所有音乐作为播放列表
        List<Music> currentModuleMusic = new ArrayList<>();
        for (HomePageResponse.HomePageInfo module : homePageData) {
            if (module.getMusicInfoList() != null) {
                for (HomePageResponse.MusicInfo moduleMusic : module.getMusicInfoList()) {
                    currentModuleMusic.add(convertToLocalMusic(moduleMusic));
                }
            }
        }
        
        // 设置播放列表
        MusicPlayer.getInstance().setPlaylist(currentModuleMusic);
        
        // 找到当前音乐在播放列表中的索引
        int currentIndex = 0;
        for (int i = 0; i < currentModuleMusic.size(); i++) {
            if (currentModuleMusic.get(i).getId().equals(localMusic.getId())) {
                currentIndex = i;
                break;
            }
        }
        
        // 立即更新播放控制栏UI（在主线程）
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // 更新音乐信息
                tvCurrentTitle.setText(localMusic.getTitle());
                tvCurrentArtist.setText(localMusic.getArtist());
                
                // 先设置为播放图标
                btnPlayPause.setImageResource(R.drawable.ic_pause);
                
                // 加载音乐封面
                if (localMusic.getCoverUrl() != null && !localMusic.getCoverUrl().isEmpty()) {
                    Glide.with(MainActivity.this)
                            .load(localMusic.getCoverUrl())
                            .placeholder(R.drawable.placeholder_music)
                            .error(R.drawable.placeholder_music)
                            .into(ivCurrentCover);
                }
            }
        });
        
        // 使用MusicPlayer播放音乐
        MusicPlayer.getInstance().play(localMusic);
        
        // 增加播放次数
        MusicDataManager.getInstance().incrementPlayCount(localMusic);
        
        // 跳转到音乐播放页面
        Intent intent = new Intent(MainActivity.this, MusicPlayerActivity.class);
        intent.putExtra("current_index", currentIndex);
        startActivity(intent);
    }

    private void togglePlayPause() {
        MusicPlayer player = MusicPlayer.getInstance();
        if (player.getCurrentMusic() == null) {
            Toast.makeText(this, "请先选择一首音乐", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (player.isPlaying()) {
            player.pause();
            btnPlayPause.setImageResource(R.drawable.ic_play);
            Toast.makeText(this, "已暂停", Toast.LENGTH_SHORT).show();
        } else {
            player.resume();
            btnPlayPause.setImageResource(R.drawable.ic_pause);
            Toast.makeText(this, "继续播放", Toast.LENGTH_SHORT).show();
        }
    }

    private void addToPlaylist(HomePageResponse.MusicInfo music) {
        // 将网络数据转换为本地Music对象
        Music localMusic = convertToLocalMusic(music);
        
        // 添加到默认播放列表（这里添加到"我的最爱"播放列表）
        List<Playlist> playlists = MusicDataManager.getInstance().getUserPlaylists();
        if (!playlists.isEmpty()) {
            Playlist favoritePlaylist = playlists.get(0); // 获取第一个播放列表
            favoritePlaylist.addMusic(localMusic);
            Toast.makeText(this, 
                "已将《" + music.getMusicName() + "》添加到播放列表", Toast.LENGTH_SHORT).show();
        } else {
            // 如果没有播放列表，创建一个新的
            Playlist newPlaylist = MusicDataManager.getInstance().createPlaylist("我的最爱", "我喜欢的音乐");
            newPlaylist.addMusic(localMusic);
            Toast.makeText(this, 
                "已创建播放列表并添加《" + music.getMusicName() + "》", Toast.LENGTH_SHORT).show();
        }
    }

    private Music convertToLocalMusic(HomePageResponse.MusicInfo music) {
        // 将网络数据转换为本地Music对象
        String audioUrl = music.getMusicUrl();
        
        // 如果网络数据中没有音频URL，使用一个测试URL
        if (audioUrl == null || audioUrl.isEmpty()) {
            // 使用一个公开的测试音频URL（这里使用一个示例URL）
            audioUrl = "https://www.soundjay.com/misc/sounds/bell-ringing-05.wav";
        }
        
        return new Music(
            String.valueOf(music.getId()),
            music.getMusicName(),
            music.getAuthor(),
            "未知专辑", // 网络数据中没有专辑信息
            "0:00",    // 网络数据中没有时长信息
            music.getCoverUrl(),
            audioUrl,
            music.getLyricUrl() // 添加歌词URL
        );
    }

    private void setupMusicPlayer() {
        MusicPlayer player = MusicPlayer.getInstance();
        player.setContext(this);
        player.setOnPlaybackStateChangeListener(new MusicPlayer.OnPlaybackStateChangeListener() {
            @Override
            public void onPlaybackStateChanged(boolean isPlaying) {
                // 延迟更新播放按钮图标，确保音乐信息先更新
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 延迟100ms更新播放按钮，确保音乐信息先显示
                        btnPlayPause.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (isPlaying) {
                                    btnPlayPause.setImageResource(R.drawable.ic_pause);
                                } else {
                                    btnPlayPause.setImageResource(R.drawable.ic_play);
                                }
                            }
                        }, 100);
                    }
                });
                // 播放状态变化时刷新进度条
                updateSeekBarProgress();
            }

            @Override
            public void onMusicChanged(Music music) {
                // 音乐变化时不需要额外处理，因为playMusic方法已经处理了UI更新
                // 切歌时刷新进度条
                updateSeekBarProgress();
            }
        });
    }

    private void startProgressUpdate() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isUserSeeking) {
                    updateSeekBarProgress();
                }
                progressHandler.postDelayed(this, 500);
            }
        };
        progressHandler.post(progressRunnable);
    }

    private void updateSeekBarProgress() {
        MusicPlayer player = MusicPlayer.getInstance();
        if (player.getCurrentMusic() != null && player.getDuration() > 0) {
            int position = player.getCurrentPosition();
            int duration = player.getDuration();
            int progress = (int) (position * 1.0f / duration * seekBarControl.getMax());
            seekBarControl.setProgress(progress);
        } else {
            seekBarControl.setProgress(0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放MediaPlayer资源
        MusicPlayer.getInstance().release();
        // 停止进度条刷新
        if (progressRunnable != null) {
            progressHandler.removeCallbacks(progressRunnable);
        }
    }

    // 新增：首页弹出播放列表弹窗
    private void showPlaylistDialog() {
        MusicPlayer player = MusicPlayer.getInstance();
        if (player.getCurrentPlaylist() == null || player.getCurrentPlaylist().isEmpty()) {
            Toast.makeText(this, "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_playlist, null);
        dialog.setContentView(dialogView);
        TextView tvTitle = dialogView.findViewById(R.id.tv_playlist_title);
        TextView tvCount = dialogView.findViewById(R.id.tv_playlist_count);
        TextView tvMode = dialogView.findViewById(R.id.tv_playlist_mode);
        RecyclerView rv = dialogView.findViewById(R.id.rv_playlist);
        tvTitle.setText("播放列表");
        tvCount.setText(player.getCurrentPlaylist().size() + "首");
        // 显示当前播放模式
        int playMode = player.getPlayMode();
        String modeText = "顺序播放";
        if (playMode == 1) modeText = "单曲循环";
        else if (playMode == 2) modeText = "随机播放";
        tvMode.setText(modeText);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        com.example.music_tttaaayyyx.adapter.PlaylistDialogAdapter adapter = new com.example.music_tttaaayyyx.adapter.PlaylistDialogAdapter(new ArrayList<>(player.getCurrentPlaylist()), player.getCurrentPlaylist());
        rv.setAdapter(adapter);
        adapter.setOnItemActionListener(new com.example.music_tttaaayyyx.adapter.PlaylistDialogAdapter.OnItemActionListener() {
            @Override
            public void onDelete(com.example.music_tttaaayyyx.Music music, int position) {
                List<com.example.music_tttaaayyyx.Music> playlist = player.getCurrentPlaylist();
                boolean isCurrent = (player.getCurrentMusic() != null && player.getCurrentMusic().getId().equals(music.getId()));
                playlist.remove(music);
                adapter.notifyItemRemoved(position);
                tvCount.setText(playlist.size() + "首");
                if (playlist.isEmpty()) {
                    dialog.dismiss();
                    tvCurrentTitle.setText("");
                    tvCurrentArtist.setText("");
                    ivCurrentCover.setImageResource(R.drawable.placeholder_music);
                    btnPlayPause.setImageResource(R.drawable.ic_play);
                    return;
                }
                if (isCurrent) {
                    int playMode = player.getPlayMode();
                    if (playMode == 0 || playMode == 1) {
                        int nextIndex = position;
                        if (nextIndex >= playlist.size()) nextIndex = 0;
                        player.setPlaylist(playlist);
                        player.play(playlist.get(nextIndex));
                    } else if (playMode == 2) {
                        int nextIndex = new java.util.Random().nextInt(playlist.size());
                        player.setPlaylist(playlist);
                        player.play(playlist.get(nextIndex));
                    }
                } else {
                    player.setPlaylist(playlist);
                }
            }
            @Override
            public void onPlay(com.example.music_tttaaayyyx.Music music, int position) {
                player.setPlaylist(player.getCurrentPlaylist());
                player.play(music);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
} 