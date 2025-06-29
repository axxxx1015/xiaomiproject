package com.example.music_tttaaayyyx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.R;
import com.example.music_tttaaayyyx.network.HomePageResponse;
import java.util.ArrayList;
import java.util.List;

public class MusicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final int TYPE_BANNER = 1;
    private static final int TYPE_HORIZONTAL_CARD = 2;
    private static final int TYPE_SINGLE_COLUMN = 3;
    private static final int TYPE_TWO_COLUMN = 4;
    
    private List<HomePageResponse.HomePageInfo> homePageData = new ArrayList<>();
    private OnMusicClickListener musicClickListener;
    private OnAddToPlaylistListener addToPlaylistListener;
    
    public void updateData(List<HomePageResponse.HomePageInfo> data) {
        this.homePageData = data;
        notifyDataSetChanged();
    }
    
    public void setOnMusicClickListener(OnMusicClickListener listener) {
        this.musicClickListener = listener;
    }
    
    public void setOnAddToPlaylistListener(OnAddToPlaylistListener listener) {
        this.addToPlaylistListener = listener;
    }
    
    @Override
    public int getItemViewType(int position) {
        HomePageResponse.HomePageInfo info = homePageData.get(position);
        int style = info.getStyle();
        
        // 热门金曲使用双列布局
        if (style == 4) {
            return TYPE_TWO_COLUMN;
        }
        
        return style;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_HORIZONTAL_CARD:
                View horizontalView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_horizontal_card, parent, false);
                return new HorizontalCardViewHolder(horizontalView);
            case TYPE_SINGLE_COLUMN:
                View singleView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_single_column, parent, false);
                return new SingleColumnViewHolder(singleView);
            case TYPE_TWO_COLUMN:
                View twoColumnView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_two_column, parent, false);
                return new TwoColumnViewHolder(twoColumnView);
            default:
                View defaultView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_single_column, parent, false);
                return new SingleColumnViewHolder(defaultView);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        HomePageResponse.HomePageInfo info = homePageData.get(position);
        
        switch (holder.getItemViewType()) {
            case TYPE_HORIZONTAL_CARD:
                ((HorizontalCardViewHolder) holder).bind(info);
                break;
            case TYPE_SINGLE_COLUMN:
                ((SingleColumnViewHolder) holder).bind(info);
                break;
            case TYPE_TWO_COLUMN:
                ((TwoColumnViewHolder) holder).bind(info);
                break;
        }
    }
    
    @Override
    public int getItemCount() {
        return homePageData.size();
    }
    
    // 横滑大卡ViewHolder
    class HorizontalCardViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleTitle;
        private RecyclerView recyclerView;
        
        public HorizontalCardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleTitle = itemView.findViewById(R.id.tv_module_title);
            recyclerView = itemView.findViewById(R.id.recycler_horizontal);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
        }
        
        public void bind(HomePageResponse.HomePageInfo info) {
            tvModuleTitle.setText(info.getModuleName());
            HorizontalMusicAdapter adapter = new HorizontalMusicAdapter(info.getMusicInfoList());
            recyclerView.setAdapter(adapter);
            
            adapter.setOnMusicClickListener(new HorizontalMusicAdapter.OnMusicClickListener() {
                @Override
                public void onMusicClick(HomePageResponse.MusicInfo music) {
                    if (musicClickListener != null) {
                        musicClickListener.onMusicClick(music);
                    }
                }
            });
            adapter.setOnAddToPlaylistListener(new HorizontalMusicAdapter.OnAddToPlaylistListener() {
                @Override
                public void onAddToPlaylist(HomePageResponse.MusicInfo music) {
                    if (addToPlaylistListener != null) {
                        addToPlaylistListener.onAddToPlaylist(music);
                    }
                }
            });
        }
    }
    
    // 一行一列ViewHolder
    class SingleColumnViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleTitle;
        private RecyclerView recyclerView;
        
        public SingleColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleTitle = itemView.findViewById(R.id.tv_module_title);
            recyclerView = itemView.findViewById(R.id.recycler_single_column);
            recyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
        
        public void bind(HomePageResponse.HomePageInfo info) {
            tvModuleTitle.setText(info.getModuleName());
            SingleColumnMusicAdapter adapter = new SingleColumnMusicAdapter(info.getMusicInfoList());
            recyclerView.setAdapter(adapter);
            
            adapter.setOnMusicClickListener(new SingleColumnMusicAdapter.OnMusicClickListener() {
                @Override
                public void onMusicClick(HomePageResponse.MusicInfo music) {
                    if (musicClickListener != null) {
                        musicClickListener.onMusicClick(music);
                    }
                }
            });
            adapter.setOnAddToPlaylistListener(new SingleColumnMusicAdapter.OnAddToPlaylistListener() {
                @Override
                public void onAddToPlaylist(HomePageResponse.MusicInfo music) {
                    if (addToPlaylistListener != null) {
                        addToPlaylistListener.onAddToPlaylist(music);
                    }
                }
            });
        }
    }
    
    // 一行两列ViewHolder
    class TwoColumnViewHolder extends RecyclerView.ViewHolder {
        private TextView tvModuleTitle;
        private RecyclerView recyclerView;
        
        public TwoColumnViewHolder(@NonNull View itemView) {
            super(itemView);
            tvModuleTitle = itemView.findViewById(R.id.tv_module_title);
            recyclerView = itemView.findViewById(R.id.recycler_two_column);
            
            // 使用GridLayoutManager，每行显示2个
            recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), 2));
        }
        
        public void bind(HomePageResponse.HomePageInfo info) {
            tvModuleTitle.setText(info.getModuleName());
            TwoColumnMusicAdapter adapter = new TwoColumnMusicAdapter(info.getMusicInfoList());
            recyclerView.setAdapter(adapter);
            
            // 创建适配器来转换接口类型
            adapter.setOnMusicClickListener(new TwoColumnMusicAdapter.OnMusicClickListener() {
                @Override
                public void onMusicClick(HomePageResponse.MusicInfo music) {
                    if (musicClickListener != null) {
                        musicClickListener.onMusicClick(music);
                    }
                }
            });
            adapter.setOnAddToPlaylistListener(new TwoColumnMusicAdapter.OnAddToPlaylistListener() {
                @Override
                public void onAddToPlaylist(HomePageResponse.MusicInfo music) {
                    if (addToPlaylistListener != null) {
                        addToPlaylistListener.onAddToPlaylist(music);
                    }
                }
            });
        }
    }
    
    public interface OnMusicClickListener {
        void onMusicClick(HomePageResponse.MusicInfo music);
    }
    
    public interface OnAddToPlaylistListener {
        void onAddToPlaylist(HomePageResponse.MusicInfo music);
    }
} 