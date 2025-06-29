package com.example.music_tttaaayyyx;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
    private ImageButton btnNext;
    
    private List<HomePageResponse.HomePageInfo> homePageData = new ArrayList<>();
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMoreData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 设置MusicPlayer的Context和监听器
        setupMusicPlayer();
        
        initViews();
        setupListeners();
        loadHomePageData(true);
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
        btnNext = findViewById(R.id.btn_next);
        
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

        // 下一首按钮点击事件
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
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

    private void playNext() {
        MusicPlayer player = MusicPlayer.getInstance();
        if (player.getCurrentPlaylist() == null || player.getCurrentPlaylist().isEmpty()) {
            Toast.makeText(this, "播放列表为空", Toast.LENGTH_SHORT).show();
            return;
        }
        
        player.next();
        Music currentMusic = player.getCurrentMusic();
        if (currentMusic != null) {
            Toast.makeText(this, "正在播放: " + currentMusic.getTitle(), Toast.LENGTH_SHORT).show();
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
            }

            @Override
            public void onMusicChanged(Music music) {
                // 音乐变化时不需要额外处理，因为playMusic方法已经处理了UI更新
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 释放MediaPlayer资源
        MusicPlayer.getInstance().release();
    }
} 