package com.example.music_tttaaayyyx.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.music_tttaaayyyx.Music;
import com.example.music_tttaaayyyx.R;
import java.util.List;

public class PlaylistDialogAdapter extends RecyclerView.Adapter<PlaylistDialogAdapter.ViewHolder> {
    private List<Music> musicList;
    private OnItemActionListener actionListener;
    private List<Music> playlistForAdd; // 用于去重

    public PlaylistDialogAdapter(List<Music> musicList, List<Music> playlistForAdd) {
        this.musicList = musicList;
        this.playlistForAdd = playlistForAdd;
    }

    public void setOnItemActionListener(OnItemActionListener listener) {
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_playlist_music, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Music music = musicList.get(position);
        holder.tvTitle.setText(music.getTitle());
        holder.tvArtist.setText(music.getArtist());
        // 只保留删除和播放点击事件
        holder.btnDelete.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onDelete(music, position);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onPlay(music, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return musicList != null ? musicList.size() : 0;
    }

    public void removeAt(int position) {
        if (musicList != null && position >= 0 && position < musicList.size()) {
            musicList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public interface OnItemActionListener {
        void onDelete(Music music, int position);
        void onPlay(Music music, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArtist;
        ImageButton btnDelete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_music_title);
            tvArtist = itemView.findViewById(R.id.tv_music_artist);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
} 