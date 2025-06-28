package com.example.music_tttaaayyyx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.music_tttaaayyyx.R;
import com.example.music_tttaaayyyx.network.HomePageResponse;
import java.util.List;

public class SingleColumnMusicAdapter extends RecyclerView.Adapter<SingleColumnMusicAdapter.ViewHolder> {
    
    private List<HomePageResponse.MusicInfo> musicList;
    private OnMusicClickListener musicClickListener;
    private OnAddToPlaylistListener addToPlaylistListener;
    
    public SingleColumnMusicAdapter(List<HomePageResponse.MusicInfo> musicList) {
        this.musicList = musicList;
    }
    
    public void setOnMusicClickListener(OnMusicClickListener listener) {
        this.musicClickListener = listener;
    }
    
    public void setOnAddToPlaylistListener(OnAddToPlaylistListener listener) {
        this.addToPlaylistListener = listener;
    }
    
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_single_column_music, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(musicList.get(position));
    }
    
    @Override
    public int getItemCount() {
        return musicList.size();
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageButton btnAdd;
        
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvArtist = itemView.findViewById(R.id.tv_artist);
            btnAdd = itemView.findViewById(R.id.btn_add);
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (musicClickListener != null) {
                        musicClickListener.onMusicClick(musicList.get(getAdapterPosition()));
                    }
                }
            });
            
            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (addToPlaylistListener != null) {
                        addToPlaylistListener.onAddToPlaylist(musicList.get(getAdapterPosition()));
                    }
                }
            });
        }
        
        public void bind(HomePageResponse.MusicInfo music) {
            tvTitle.setText(music.getMusicName());
            tvArtist.setText(music.getAuthor());
            
            Glide.with(ivCover.getContext())
                    .load(music.getCoverUrl())
                    .placeholder(R.drawable.placeholder_music)
                    .error(R.drawable.placeholder_music)
                    .into(ivCover);
        }
    }
    
    public interface OnMusicClickListener {
        void onMusicClick(HomePageResponse.MusicInfo music);
    }
    
    public interface OnAddToPlaylistListener {
        void onAddToPlaylist(HomePageResponse.MusicInfo music);
    }
} 