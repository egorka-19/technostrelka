package com.example.main_screen.adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.main_screen.R;
import com.example.main_screen.model.ArtObject;
import java.util.List;

public class ArtObjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_COLLAPSED = 0;
    private static final int VIEW_TYPE_EXPANDED = 1;

    private final Context context;
    private final List<ArtObject> artObjects;
    private final OnArtObjectClickListener listener;
    private int expandedPosition = -1;
    private MediaPlayer mediaPlayer;
    private int currentPlayingPosition = -1;

    public interface OnArtObjectClickListener {
        void onArtObjectClick(ArtObject artObject);
    }

    public ArtObjectAdapter(Context context, List<ArtObject> artObjects, OnArtObjectClickListener listener) {
        this.context = context;
        this.artObjects = artObjects;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        return position == expandedPosition ? VIEW_TYPE_EXPANDED : VIEW_TYPE_COLLAPSED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == VIEW_TYPE_EXPANDED) {
            View view = inflater.inflate(R.layout.item_art_object_expanded, parent, false);
            return new ExpandedViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_art_object_collapsed, parent, false);
            return new CollapsedViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ArtObject artObject = artObjects.get(position);
        
        if (holder instanceof CollapsedViewHolder) {
            ((CollapsedViewHolder) holder).bind(artObject);
        } else if (holder instanceof ExpandedViewHolder) {
            ((ExpandedViewHolder) holder).bind(artObject);
        }
    }

    @Override
    public int getItemCount() {
        return artObjects.size();
    }

    private class CollapsedViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageButton expandButton;
        private final View cardView;

        CollapsedViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.art_object_name);
            expandButton = itemView.findViewById(R.id.expand_button);
            cardView = itemView;

            // Обработка клика по всей карточке
            cardView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    expandCard(position);
                }
            });

            // Обработка клика по кнопке развертывания
            expandButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    expandCard(position);
                }
            });
        }

        void bind(ArtObject artObject) {
            nameTextView.setText(artObject.getName());
        }
    }

    private class ExpandedViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameTextView;
        private final ImageView imageView;
        private final TextView descriptionTextView;
        private final ImageButton collapseButton;
        private final ImageButton playButton;
        private final ImageButton pauseButton;
        private final View cardView;

        ExpandedViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.art_object_name);
            imageView = itemView.findViewById(R.id.art_object_image);
            descriptionTextView = itemView.findViewById(R.id.art_object_description);
            collapseButton = itemView.findViewById(R.id.collapse_button);
            playButton = itemView.findViewById(R.id.play_button);
            pauseButton = itemView.findViewById(R.id.pause_button);
            cardView = itemView;

            // Обработка клика по кнопке сворачивания
            collapseButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    collapseCard(position);
                }
            });

            // Обработка клика по кнопке воспроизведения
            playButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    playAudio(position);
                }
            });

            // Обработка клика по кнопке паузы
            pauseButton.setOnClickListener(v -> {
                stopAudio();
            });
        }

        void bind(ArtObject artObject) {
            nameTextView.setText(artObject.getName());
            imageView.setImageResource(artObject.getImageResourceId());
            descriptionTextView.setText(artObject.getDescription());

            if (getAdapterPosition() == currentPlayingPosition) {
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
            } else {
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
            }
        }
    }

    private void expandCard(int position) {
        int previousExpandedPosition = expandedPosition;
        expandedPosition = position;
        notifyItemChanged(previousExpandedPosition);
        notifyItemChanged(position);
        if (listener != null) {
            listener.onArtObjectClick(artObjects.get(position));
        }
    }

    private void collapseCard(int position) {
        expandedPosition = -1;
        notifyItemChanged(position);
    }

    private void playAudio(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }

        ArtObject artObject = artObjects.get(position);
        try {
            mediaPlayer = MediaPlayer.create(context, artObject.getAudioResourceId());
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(mp -> {
                    stopAudio();
                });
                mediaPlayer.start();
                currentPlayingPosition = position;
                notifyItemChanged(position);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        int previousPosition = currentPlayingPosition;
        currentPlayingPosition = -1;
        if (previousPosition != -1) {
            notifyItemChanged(previousPosition);
        }
    }

    public void stopPlayback() {
        stopAudio();
    }

    public void onDestroy() {
        stopAudio();
    }
} 