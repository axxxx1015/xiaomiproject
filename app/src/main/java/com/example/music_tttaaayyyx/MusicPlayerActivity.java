package com.example.music_tttaaayyyx;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.palette.graphics.Palette;
import androidx.viewpager2.widget.ViewPager2;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.music_tttaaayyyx.adapter.MusicPlayerPagerAdapter;
import com.example.music_tttaaayyyx.adapter.PlaylistDialogAdapter;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicPlayerActivity extends AppCompatActivity {
    
    private ConstraintLayout rootLayout;
    private ViewPager2 viewPager;
    private ImageButton btnClose;
    private ImageButton btnPlayMode;
    private ImageButton btnPrevious;
    private ImageButton btnPlayPause;
    private ImageButton btnNext;
    private ImageButton btnMore;
    private ImageButton btnSwitchView;
    private ImageButton btnFavorite;
    private SeekBar seekBar;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    
    private TextView tvSwipeHint;
    
    private MusicPlayer musicPlayer;
    private MusicPlayerPagerAdapter pagerAdapter;
    private List<Music> playlist;
    private int currentIndex;
    private boolean isPlaying = false;
    private int playMode = 0; // 0: 顺序播放, 1: 单曲循环, 2: 随机播放
    private boolean isFavorite = false;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable progressRunnable;
    
    // 播放模式图标
    private int[] playModeIcons = {
        R.drawable.ic_order,      // 顺序播放
        R.drawable.ic_repeat,     // 单曲循环
        R.drawable.ic_shuffle     // 随机播放
    };
    
    private float downY = 0;
    private float translationY = 0;
    private GestureDetector gestureDetector;
    private boolean isSliding = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        
        initViews();
        setupListeners();
        setupMusicPlayer();
        setupViewPager();
        loadDataFromIntent();
        
        // 滑动关闭手势
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (e1 == null || e2 == null) return false;
                float deltaY = e2.getRawY() - downY;
                if (deltaY > 0) { // 只允许下滑
                    rootLayout.setTranslationY(deltaY);
                    isSliding = true;
                }
                return true;
            }
        });
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        downY = event.getRawY();
                        translationY = 0;
                        isSliding = false;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        gestureDetector.onTouchEvent(event);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        if (isSliding) {
                            float finalY = rootLayout.getTranslationY();
                            if (finalY > rootLayout.getHeight() / 4) {
                                // 滑动超过1/4屏，关闭
                                rootLayout.animate().translationY(rootLayout.getHeight()).setDuration(200).withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        overridePendingTransition(0, android.R.anim.fade_out);
                                    }
                                }).start();
                            } else {
                                // 回弹
                                rootLayout.animate().translationY(0).setDuration(200).start();
                            }
                        }
                        isSliding = false;
                        break;
                }
                return isSliding;
            }
        });
    }
    
    private void initViews() {
        rootLayout = findViewById(R.id.root_layout);
        viewPager = findViewById(R.id.view_pager);
        btnClose = findViewById(R.id.btn_close);
        btnPlayMode = findViewById(R.id.btn_play_mode);
        btnPrevious = findViewById(R.id.btn_previous);
        btnPlayPause = findViewById(R.id.btn_play_pause);
        btnNext = findViewById(R.id.btn_next);
        btnMore = findViewById(R.id.btn_more);
        btnSwitchView = findViewById(R.id.btn_switch_view);
        btnFavorite = findViewById(R.id.btn_favorite);
        seekBar = findViewById(R.id.seek_bar);
        tvCurrentTime = findViewById(R.id.tv_current_time);
        tvTotalTime = findViewById(R.id.tv_total_time);
        
        // 页面指示器
        tvSwipeHint = findViewById(R.id.tv_swipe_hint);
    }
    
    private void setupListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        btnPlayMode.setOnClickListener(v -> togglePlayMode());
        
        btnPrevious.setOnClickListener(v -> playPrevious());
        
        btnPlayPause.setOnClickListener(v -> togglePlayPause());
        
        btnNext.setOnClickListener(v -> playNext());
        
        btnMore.setOnClickListener(v -> showMoreOptions());
        
        btnSwitchView.setOnClickListener(v -> switchView());
        
        btnFavorite.setOnClickListener(v -> toggleFavorite());
        
        btnPlayMode.setOnLongClickListener(v -> { showPlaylistDialog(); return true; });
        
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && musicPlayer != null) {
                    int duration = musicPlayer.getDuration();
                    int newPosition = (int) ((progress / 100.0) * duration);
                    musicPlayer.seekTo(newPosition);
                    updateCurrentTime(newPosition);
                }
            }
            
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }
    
    private void setupMusicPlayer() {
        musicPlayer = MusicPlayer.getInstance();
        musicPlayer.setContext(this);
        
        // 同步播放模式
        playMode = musicPlayer.getPlayMode();
        updatePlayModeButton();
        
        musicPlayer.setOnPlaybackStateChangeListener(new MusicPlayer.OnPlaybackStateChangeListener() {
            @Override
            public void onPlaybackStateChanged(boolean isPlaying) {
                MusicPlayerActivity.this.isPlaying = isPlaying;
                updatePlayPauseButton();
            }
            
            @Override
            public void onMusicChanged(Music music) {
                // 延迟更新，确保pagerAdapter已经初始化
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMusicInfo(music);
                        updateBackgroundColor(music);
                        // 更新当前索引
                        currentIndex = musicPlayer.getCurrentIndex();
                    }
                }, 100);
            }
        });
    }
    
    private void loadDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            currentIndex = intent.getIntExtra("current_index", 0);
            // 从MusicPlayer获取播放列表
            playlist = musicPlayer.getCurrentPlaylist();
            if (playlist != null && !playlist.isEmpty() && currentIndex < playlist.size()) {
                // 延迟更新，确保pagerAdapter已经初始化
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateMusicInfo(playlist.get(currentIndex));
                        updateBackgroundColor(playlist.get(currentIndex));
                    }
                }, 100);
            }
        }
    }
    
    private void setupViewPager() {
        pagerAdapter = new MusicPlayerPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setUserInputEnabled(true); // 启用滑动
        viewPager.setCurrentItem(0); // 默认显示封面视图（索引0）
        
        // 添加页面切换监听器
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 更新切换视图按钮的图标
                updateSwitchViewButton(position);
                // 隐藏滑动提示
                hideSwipeHint();
            }
        });
        
        // 延迟隐藏滑动提示
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                hideSwipeHint();
            }
        }, 3000); // 3秒后自动隐藏
    }
    
    private void updateSwitchViewButton(int position) {
        // 根据当前页面更新切换视图按钮的图标
        if (position == 0) {
            // 封面视图，显示切换到歌词视图的图标
            btnSwitchView.setImageResource(R.drawable.ic_lyrics);
        } else {
            // 歌词视图，显示切换到封面视图的图标
            btnSwitchView.setImageResource(R.drawable.ic_album);
        }
    }
    
    private void togglePlayMode() {
        playMode = (playMode + 1) % 3;
        updatePlayModeButton();
        
        // 设置MusicPlayer的播放模式
        musicPlayer.setPlayMode(playMode);
        
        String modeText = "";
        switch (playMode) {
            case 0:
                modeText = "顺序播放";
                break;
            case 1:
                modeText = "单曲循环";
                break;
            case 2:
                modeText = "随机播放";
                break;
        }
        
        Toast.makeText(this, modeText, Toast.LENGTH_SHORT).show();
    }
    
    private void updatePlayModeButton() {
        btnPlayMode.setImageResource(playModeIcons[playMode]);
    }
    
    private void playPrevious() {
        musicPlayer.previous();
        // 更新当前索引
        currentIndex = musicPlayer.getCurrentIndex();
    }
    
    private void playNext() {
        musicPlayer.next();
        // 更新当前索引
        currentIndex = musicPlayer.getCurrentIndex();
    }
    
    private void togglePlayPause() {
        if (musicPlayer.isPlaying()) {
            musicPlayer.pause();
        } else {
            musicPlayer.resume();
        }
    }
    
    private void updatePlayPauseButton() {
        if (isPlaying) {
            btnPlayPause.setImageResource(R.drawable.ic_pause);
        } else {
            btnPlayPause.setImageResource(R.drawable.ic_play);
        }
    }
    
    private void showMoreOptions() {
        Toast.makeText(this, "更多功能开发中...", Toast.LENGTH_SHORT).show();
    }
    
    private void switchView() {
        int currentItem = viewPager.getCurrentItem();
        int newItem = (currentItem == 0) ? 1 : 0; // 在歌词视图和封面视图之间切换
        viewPager.setCurrentItem(newItem, true); // 添加动画效果
        
        String viewText = newItem == 0 ? getString(R.string.lyrics_view) : getString(R.string.cover_view);
        Toast.makeText(this, viewText, Toast.LENGTH_SHORT).show();
    }
    
    private void toggleFavorite() {
        if (playlist == null || playlist.isEmpty() || currentIndex < 0 || currentIndex >= playlist.size()) return;
        Music currentMusic = playlist.get(currentIndex);
        boolean willLike = !currentMusic.isLiked();
        // 动画参数
        float startScale = 1.0f;
        float midScale = willLike ? 1.2f : 0.8f;
        float endScale = 1.0f;
        long duration = 1000;
        // 动画：缩放+旋转
        ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(btnFavorite, "scaleX", startScale, midScale);
        ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(btnFavorite, "scaleY", startScale, midScale);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(btnFavorite, "scaleX", midScale, endScale);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(btnFavorite, "scaleY", midScale, endScale);
        ObjectAnimator rotation = ObjectAnimator.ofFloat(btnFavorite, "rotationY", 0f, 360f);
        scaleX1.setDuration(duration/2);
        scaleY1.setDuration(duration/2);
        scaleX2.setDuration(duration/2);
        scaleY2.setDuration(duration/2);
        rotation.setDuration(duration);
        // 动画组合
        android.animation.AnimatorSet set = new android.animation.AnimatorSet();
        set.play(scaleX1).with(scaleY1).with(rotation);
        set.play(scaleX2).with(scaleY2).after(scaleX1);
        set.addListener(new android.animation.AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(android.animation.Animator animation) {
                // 切换本地收藏状态
                MusicDataManager.getInstance().toggleFavorite(currentMusic);
                // 刷新UI
                updateFavoriteButton(currentMusic.isLiked());
                String message = currentMusic.isLiked() ? "已收藏" : "取消收藏";
                Toast.makeText(MusicPlayerActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
        set.start();
    }
    
    private void updateFavoriteButton(boolean isLiked) {
        if (isLiked) {
            btnFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_border);
        }
    }
    
    private void updateMusicInfo(Music music) {
        if (music == null) return;
        
        // 更新封面视图
        if (pagerAdapter != null) {
            pagerAdapter.updateMusicInfo(music);
            
            // 开始旋转动画
            startRotationAnimation();
        }
        
        // 开始进度更新
        startProgressUpdate();
        
        // 更新收藏按钮
        updateFavoriteButton(music.isLiked());
    }
    
    private void updateBackgroundColor(Music music) {
        if (music == null || music.getCoverUrl() == null) return;
        
        Glide.with(this)
            .asBitmap()
            .load(music.getCoverUrl())
            .into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                    Palette.from(resource).generate(palette -> {
                        if (palette != null) {
                            int dominantColor = palette.getDominantColor(Color.GRAY);
                            rootLayout.setBackgroundColor(dominantColor);
                        }
                    });
                }
                
                @Override
                public void onLoadCleared(Drawable placeholder) {}
            });
    }
    
    private void startRotationAnimation() {
        if (pagerAdapter == null) return;
        
        ImageView albumCover = pagerAdapter.getAlbumCover();
        if (albumCover != null) {
            ObjectAnimator rotation = ObjectAnimator.ofFloat(albumCover, "rotation", 0f, 360f);
            rotation.setDuration(10000); // 10秒转一圈
            rotation.setRepeatCount(ObjectAnimator.INFINITE);
            rotation.setRepeatMode(ObjectAnimator.RESTART);
            rotation.start();
        }
    }
    
    private void startProgressUpdate() {
        if (progressRunnable == null) {
            progressRunnable = new Runnable() {
                @Override
                public void run() {
                    if (musicPlayer != null && isPlaying) {
                        int currentPosition = musicPlayer.getCurrentPosition();
                        int duration = musicPlayer.getDuration();
                        
                        updateCurrentTime(currentPosition);
                        updateTotalTime(duration);
                        updateSeekBar(currentPosition, duration);
                        
                        // 更新歌词同步
                        if (pagerAdapter != null) {
                            pagerAdapter.updateCurrentLyric(currentPosition);
                        }
                        
                        handler.postDelayed(this, 1000);
                    }
                }
            };
        }
        handler.post(progressRunnable);
    }
    
    private void updateCurrentTime(int milliseconds) {
        String time = formatTime(milliseconds);
        tvCurrentTime.setText(time);
    }
    
    private void updateTotalTime(int milliseconds) {
        String time = formatTime(milliseconds);
        tvTotalTime.setText(time);
    }
    
    private void updateSeekBar(int currentPosition, int duration) {
        if (duration > 0) {
            int progress = (int) ((currentPosition * 100.0) / duration);
            seekBar.setProgress(progress);
        }
    }
    
    private String formatTime(int milliseconds) {
        int seconds = (milliseconds / 1000) % 60;
        int minutes = (milliseconds / (1000 * 60)) % 60;
        return String.format("%d:%02d", minutes, seconds);
    }
    
    private void hideSwipeHint() {
        // 淡出滑动提示
        tvSwipeHint.animate()
            .alpha(0f)
            .setDuration(500)
            .withEndAction(new Runnable() {
                @Override
                public void run() {
                    tvSwipeHint.setVisibility(View.GONE);
                }
            })
            .start();
    }
    
    private void showPlaylistDialog() {
        if (musicPlayer == null || musicPlayer.getCurrentPlaylist() == null) return;
        List<Music> playlist = new ArrayList<>(musicPlayer.getCurrentPlaylist());
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View dialogView = getLayoutInflater().inflate(R.layout.layout_dialog_playlist, null);
        dialog.setContentView(dialogView);
        TextView tvTitle = dialogView.findViewById(R.id.tv_playlist_title);
        TextView tvCount = dialogView.findViewById(R.id.tv_playlist_count);
        RecyclerView rv = dialogView.findViewById(R.id.rv_playlist);
        tvTitle.setText("播放列表");
        tvCount.setText(playlist.size() + "首");
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this));
        PlaylistDialogAdapter adapter = new PlaylistDialogAdapter(playlist, musicPlayer.getCurrentPlaylist());
        rv.setAdapter(adapter);
        adapter.setOnItemActionListener(new PlaylistDialogAdapter.OnItemActionListener() {
            @Override
            public void onDelete(Music music, int position) {
                boolean isCurrent = (musicPlayer.getCurrentMusic() != null && musicPlayer.getCurrentMusic().getId().equals(music.getId()));
                playlist.remove(position);
                musicPlayer.getCurrentPlaylist().remove(music);
                adapter.notifyItemRemoved(position);
                tvCount.setText(playlist.size() + "首");
                if (playlist.isEmpty()) {
                    dialog.dismiss();
                    finish();
                    return;
                }
                if (isCurrent) {
                    // 删除当前播放
                    int playMode = musicPlayer.getPlayMode();
                    if (playMode == 0 || playMode == 1) { // 顺序/单曲循环
                        int nextIndex = position;
                        if (nextIndex >= playlist.size()) nextIndex = 0;
                        musicPlayer.setPlaylist(playlist);
                        musicPlayer.play(playlist.get(nextIndex));
                        updateMusicInfo(playlist.get(nextIndex));
                    } else if (playMode == 2) { // 随机
                        int nextIndex = new java.util.Random().nextInt(playlist.size());
                        musicPlayer.setPlaylist(playlist);
                        musicPlayer.play(playlist.get(nextIndex));
                        updateMusicInfo(playlist.get(nextIndex));
                    }
                } else {
                    musicPlayer.setPlaylist(playlist);
                }
            }
            @Override
            public void onPlay(Music music, int position) {
                musicPlayer.setPlaylist(playlist);
                musicPlayer.play(music);
                updateMusicInfo(music);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }
    }
} 