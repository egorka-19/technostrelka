package com.example.main_screen.adapter;

import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.main_screen.R;
import com.example.main_screen.data.RoutePromoPreferences;
import com.example.main_screen.model.RouteStop;
import com.example.main_screen.ui.EqualizerBarsView;
import com.example.main_screen.utils.MediaUrlUtils;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RouteItineraryAdapter extends RecyclerView.Adapter<RouteItineraryAdapter.Holder> {

    public interface Callbacks {
        void onPlayRequest(int adapterPosition, RouteStop stop);

        void onPauseRequest();

        void onGiftClick(RouteStop stop);
    }

    private final List<RouteStop> stops;
    private final Callbacks callbacks;
    private final Set<Integer> expandedPositions = new HashSet<>();
    /** Строка, для которой открыта сессия аудио (воспроизведение или пауза). */
    private int audioActivePosition = RecyclerView.NO_POSITION;
    /** Идёт озвучка (эквалайзер + иконка паузы). */
    private boolean ttsSpeaking = false;

    public RouteItineraryAdapter(List<RouteStop> stops, Callbacks callbacks, int initiallyExpandedIndex) {
        this.stops = stops;
        this.callbacks = callbacks;
        if (initiallyExpandedIndex >= 0 && initiallyExpandedIndex < stops.size()) {
            expandedPositions.add(initiallyExpandedIndex);
        }
    }

    /**
     * @param activePosition позиция точки с активным аудио или {@link RecyclerView#NO_POSITION}
     * @param speaking       true — сейчас идёт речь; false — пауза или тишина (показываем «play»)
     */
    public void setAudioState(int activePosition, boolean speaking) {
        int oldActive = audioActivePosition;
        boolean oldSpeaking = ttsSpeaking;
        audioActivePosition = activePosition;
        ttsSpeaking = speaking;
        if (oldActive != audioActivePosition) {
            if (oldActive != RecyclerView.NO_POSITION) {
                notifyItemChanged(oldActive, PAYLOAD_AUDIO);
            }
            if (audioActivePosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(audioActivePosition, PAYLOAD_AUDIO);
            }
        } else if (audioActivePosition != RecyclerView.NO_POSITION && oldSpeaking != ttsSpeaking) {
            notifyItemChanged(audioActivePosition, PAYLOAD_AUDIO);
        }
    }

    private static final Object PAYLOAD_AUDIO = new Object();

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            holder.bindAudio(position, audioActivePosition, ttsSpeaking);
            return;
        }
        super.onBindViewHolder(holder, position, payloads);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_itinerary_stop, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RouteStop s = stops.get(position);
        boolean ex = expandedPositions.contains(position);
        holder.bind(s, position + 1, ex, position, audioActivePosition, ttsSpeaking, callbacks);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    final class Holder extends RecyclerView.ViewHolder {
        private final TextView order;
        private final TextView title;
        private final ImageView expandIcon;
        private final LinearLayout header;
        private final LinearLayout expanded;
        private final ShapeableImageView image;
        private final TextView address;
        private final TextView text;
        private final ImageButton audioBtn;
        private final EqualizerBarsView equalizer;
        private final TextView giftUsed;
        private final com.google.android.material.button.MaterialButton giftBtn;

        Holder(@NonNull View itemView) {
            super(itemView);
            order = itemView.findViewById(R.id.stop_order);
            title = itemView.findViewById(R.id.stop_title);
            expandIcon = itemView.findViewById(R.id.stop_expand_icon);
            header = itemView.findViewById(R.id.stop_header);
            expanded = itemView.findViewById(R.id.stop_expanded);
            image = itemView.findViewById(R.id.stop_image);
            address = itemView.findViewById(R.id.stop_address);
            text = itemView.findViewById(R.id.stop_text);
            audioBtn = itemView.findViewById(R.id.stop_audio_btn);
            equalizer = itemView.findViewById(R.id.stop_equalizer);
            giftUsed = itemView.findViewById(R.id.stop_gift_used);
            giftBtn = itemView.findViewById(R.id.stop_gift_btn);
        }

        void bindAudio(int adapterPosition, int audioActivePosition, boolean ttsSpeaking) {
            boolean activeHere = audioActivePosition == adapterPosition;
            boolean anim = activeHere && ttsSpeaking;
            equalizer.setPlaying(anim);
            audioBtn.setImageResource(anim ? R.drawable.ic_pause_filled : R.drawable.ic_play_filled);
        }

        void bind(RouteStop s, int orderNum, boolean isExpanded, int adapterPosition,
                int audioPosAtBind, boolean speakingAtBind, Callbacks cb) {
            order.setText(String.valueOf(orderNum));
            title.setText(s.getTitle());
            expandIcon.setRotation(isExpanded ? 90f : 0f);
            expanded.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

            String addr = s.getAddress();
            if (!TextUtils.isEmpty(addr)) {
                address.setVisibility(View.VISIBLE);
                address.setText(addr);
            } else {
                address.setVisibility(View.GONE);
            }

            String body = s.getText();
            if (!TextUtils.isEmpty(body)) {
                text.setVisibility(View.VISIBLE);
                text.setText(body);
            } else {
                text.setVisibility(View.GONE);
            }

            String imgUrl = MediaUrlUtils.resolveForApiClient(s.getPrimaryImageUrl());
            if (!TextUtils.isEmpty(imgUrl)) {
                image.setVisibility(View.VISIBLE);
                Glide.with(image.getContext())
                        .load(imgUrl)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                    Target<Drawable> target, boolean isFirstResource) {
                                image.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                    Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .centerCrop()
                        .into(image);
            } else {
                image.setVisibility(View.GONE);
                Glide.with(image.getContext()).clear(image);
            }

            bindAudio(adapterPosition, audioPosAtBind, speakingAtBind);

            audioBtn.setOnClickListener(v -> {
                int pos = getBindingAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }
                boolean activeHere = RouteItineraryAdapter.this.audioActivePosition == pos;
                boolean speakingHere = activeHere && RouteItineraryAdapter.this.ttsSpeaking;
                if (speakingHere) {
                    cb.onPauseRequest();
                } else {
                    cb.onPlayRequest(pos, s);
                }
            });

            boolean partner = s.isPartnerPoi();
            boolean redeemed = RoutePromoPreferences.isRedeemed(itemView.getContext(), s.getStopId());
            if (partner) {
                if (redeemed) {
                    giftBtn.setVisibility(View.GONE);
                    giftUsed.setVisibility(View.VISIBLE);
                } else {
                    giftUsed.setVisibility(View.GONE);
                    giftBtn.setVisibility(View.VISIBLE);
                    giftBtn.setOnClickListener(v -> cb.onGiftClick(s));
                }
            } else {
                giftBtn.setVisibility(View.GONE);
                giftUsed.setVisibility(View.GONE);
            }

            header.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) {
                    return;
                }
                if (expandedPositions.contains(pos)) {
                    expandedPositions.remove(pos);
                } else {
                    expandedPositions.add(pos);
                }
                notifyItemChanged(pos);
            });
        }
    }
}
