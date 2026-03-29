package com.example.main_screen;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.main_screen.adapter.RouteItineraryAdapter;
import com.example.main_screen.data.RoutePromoPreferences;
import com.example.main_screen.model.RouteModel;
import com.example.main_screen.model.RouteStop;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RouteItineraryActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String STATE_TTS_POS = "route_tts_pos";
    private static final String STATE_TTS_CHUNK = "route_tts_chunk";
    private static final String STATE_TTS_PAUSED = "route_tts_paused";
    private static final String STATE_TTS_SESSION = "route_tts_session";

    private TextToSpeech tts;
    private int ttsStatus = TextToSpeech.ERROR;
    private RouteItineraryAdapter adapter;
    private List<RouteStop> stops = new ArrayList<>();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** Сессия TTS: увеличивается при новом запуске с точки, чтобы игнорировать устаревшие onDone. */
    private int ttsSession = 0;
    private int ttsActivePosition = RecyclerView.NO_POSITION;
    private List<String> ttsChunks = new ArrayList<>();
    private int ttsChunkIndex = 0;
    private boolean ttsPaused = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_route_itinerary);

        RouteModel route = readRoute();
        String titleText = route != null && route.getName() != null && !route.getName().isEmpty()
                ? route.getName()
                : getString(R.string.road);

        TextView title = findViewById(R.id.itinerary_title);
        title.setText(titleText);

        RecyclerView list = findViewById(R.id.itinerary_list);
        TextView empty = findViewById(R.id.itinerary_empty);

        stops.clear();
        if (route != null && route.getStops() != null) {
            stops.addAll(route.getStops());
        }

        if (stops.isEmpty()) {
            list.setVisibility(android.view.View.GONE);
            empty.setVisibility(android.view.View.VISIBLE);
            empty.setText(R.string.route_itinerary_placeholder);
        } else {
            empty.setVisibility(android.view.View.GONE);
            list.setVisibility(android.view.View.VISIBLE);
            list.setLayoutManager(new LinearLayoutManager(this));
            adapter = new RouteItineraryAdapter(stops, new RouteItineraryAdapter.Callbacks() {
                @Override
                public void onPlayRequest(int adapterPosition, RouteStop stop) {
                    playStop(adapterPosition, stop);
                }

                @Override
                public void onPauseRequest() {
                    pauseTts();
                }

                @Override
                public void onGiftClick(RouteStop stop) {
                    showGiftDialog(stop);
                }
            }, 0);
            list.setAdapter(adapter);
            if (savedInstanceState != null) {
                restoreTtsState(savedInstanceState);
            }
        }

        ImageButton back = findViewById(R.id.itinerary_back);
        back.setOnClickListener(v -> finish());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.itinerary_root), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        tts = new TextToSpeech(this, this);
    }

    @Override
    public void onInit(int status) {
        ttsStatus = status;
        if (status == TextToSpeech.SUCCESS && tts != null) {
            int r = tts.setLanguage(Locale.forLanguageTag("ru-RU"));
            if (r == TextToSpeech.LANG_MISSING_DATA || r == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts.setLanguage(Locale.getDefault());
            }
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                }

                @Override
                public void onDone(String utteranceId) {
                    mainHandler.post(() -> handleUtteranceDone(utteranceId));
                }

                @Override
                public void onError(String utteranceId) {
                    mainHandler.post(() -> {
                        if (!ttsPaused) {
                            Toast.makeText(RouteItineraryActivity.this,
                                    "Не удалось озвучить фрагмент", Toast.LENGTH_SHORT).show();
                            finishAudio();
                        }
                    });
                }
            });
        }
    }

    private void handleUtteranceDone(String utteranceId) {
        if (ttsPaused || utteranceId == null) {
            return;
        }
        String[] p = utteranceId.split("_");
        if (p.length != 2) {
            finishAudio();
            return;
        }
        try {
            int sess = Integer.parseInt(p[0]);
            int idx = Integer.parseInt(p[1]);
            if (sess != ttsSession || idx != ttsChunkIndex || ttsChunks == null) {
                return;
            }
        } catch (NumberFormatException e) {
            return;
        }
        ttsChunkIndex++;
        if (ttsChunks != null && ttsChunkIndex < ttsChunks.size()) {
            playNextChunk();
        } else {
            finishAudio();
        }
    }

    private void playStop(int position, RouteStop stop) {
        if (tts == null || ttsStatus != TextToSpeech.SUCCESS) {
            Toast.makeText(this, "Аудиогид недоступен: проверьте синтез речи в настройках", Toast.LENGTH_LONG).show();
            return;
        }
        if (position == ttsActivePosition && ttsPaused && ttsChunks != null
                && ttsChunkIndex < ttsChunks.size()) {
            ttsPaused = false;
            tts.stop();
            playNextChunk();
            return;
        }
        String full = buildSpeechText(stop);
        if (TextUtils.isEmpty(full)) {
            Toast.makeText(this, "Нет текста для озвучки", Toast.LENGTH_SHORT).show();
            return;
        }
        ttsSession++;
        ttsActivePosition = position;
        ttsPaused = false;
        ttsChunks = splitIntoChunks(full);
        ttsChunkIndex = 0;
        if (ttsChunks.isEmpty()) {
            Toast.makeText(this, "Нет текста для озвучки", Toast.LENGTH_SHORT).show();
            return;
        }
        tts.stop();
        playNextChunk();
    }

    private void playNextChunk() {
        if (tts == null || ttsStatus != TextToSpeech.SUCCESS) {
            return;
        }
        if (ttsChunks == null || ttsChunkIndex >= ttsChunks.size()) {
            finishAudio();
            return;
        }
        String chunk = ttsChunks.get(ttsChunkIndex).trim();
        if (chunk.isEmpty()) {
            ttsChunkIndex++;
            playNextChunk();
            return;
        }
        if (adapter != null && ttsActivePosition != RecyclerView.NO_POSITION) {
            adapter.setAudioState(ttsActivePosition, true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle params = new Bundle();
            String uttId = ttsSession + "_" + ttsChunkIndex;
            tts.speak(chunk, TextToSpeech.QUEUE_FLUSH, params, uttId);
        } else {
            tts.speak(chunk, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    private static List<String> splitIntoChunks(String text) {
        List<String> out = new ArrayList<>();
        if (TextUtils.isEmpty(text)) {
            return out;
        }
        String normalized = text.replace('\n', ' ')
                .replace('\u2026', '.')
                .replaceAll("\\s+", " ")
                .trim();
        String[] parts = normalized.split("(?<=[\\.!?])\\s+");
        for (String part : parts) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        if (out.isEmpty()) {
            out.add(normalized);
        }
        return out;
    }

    private static String buildSpeechText(RouteStop stop) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(stop.getTitle())) {
            sb.append(stop.getTitle()).append(". ");
        }
        if (!TextUtils.isEmpty(stop.getAddress())) {
            sb.append("Адрес: ").append(stop.getAddress()).append(". ");
        }
        if (!TextUtils.isEmpty(stop.getText())) {
            sb.append(stop.getText());
        }
        return sb.toString().trim();
    }

    private void pauseTts() {
        boolean wasSpeaking = !ttsPaused
                && ttsActivePosition != RecyclerView.NO_POSITION
                && ttsChunks != null
                && !ttsChunks.isEmpty()
                && ttsChunkIndex < ttsChunks.size();
        ttsPaused = true;
        if (tts != null) {
            tts.stop();
        }
        // После stop() поздний onDone не должен совпадать по сессии с следующим speak().
        if (wasSpeaking) {
            ttsSession++;
        }
        if (adapter != null && ttsActivePosition != RecyclerView.NO_POSITION) {
            adapter.setAudioState(ttsActivePosition, false);
        }
    }

    private void finishAudio() {
        ttsActivePosition = RecyclerView.NO_POSITION;
        ttsChunks = null;
        ttsChunkIndex = 0;
        ttsPaused = false;
        if (adapter != null) {
            adapter.setAudioState(RecyclerView.NO_POSITION, false);
        }
    }

    private void showGiftDialog(RouteStop stop) {
        String code = RoutePromoPreferences.getFullPromoCode(this);
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.route_gift_title)
                .setMessage(getString(R.string.route_gift_message) + "\n\n" + code)
                .setPositiveButton(R.string.route_gift_ok, (d, w) -> {
                    RoutePromoPreferences.markRedeemed(this, stop.getStopId());
                    refreshStopById(stop.getStopId());
                })
                .show();
    }

    private void refreshStopById(String stopId) {
        if (adapter == null || stopId == null) {
            return;
        }
        for (int i = 0; i < stops.size(); i++) {
            if (stopId.equals(stops.get(i).getStopId())) {
                adapter.notifyItemChanged(i);
                break;
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (ttsActivePosition != RecyclerView.NO_POSITION && ttsChunks != null && !ttsChunks.isEmpty()) {
            outState.putInt(STATE_TTS_POS, ttsActivePosition);
            outState.putInt(STATE_TTS_CHUNK, ttsChunkIndex);
            outState.putBoolean(STATE_TTS_PAUSED, ttsPaused);
            outState.putInt(STATE_TTS_SESSION, ttsSession);
        }
    }

    private void restoreTtsState(@NonNull Bundle b) {
        int pos = b.getInt(STATE_TTS_POS, RecyclerView.NO_POSITION);
        if (pos == RecyclerView.NO_POSITION || pos < 0 || pos >= stops.size()) {
            return;
        }
        ttsActivePosition = pos;
        ttsChunkIndex = b.getInt(STATE_TTS_CHUNK, 0);
        ttsPaused = b.getBoolean(STATE_TTS_PAUSED, true);
        ttsSession = b.getInt(STATE_TTS_SESSION, 0);
        String full = buildSpeechText(stops.get(pos));
        ttsChunks = splitIntoChunks(full);
        if (ttsChunks.isEmpty()) {
            finishAudio();
            return;
        }
        if (ttsChunkIndex >= ttsChunks.size()) {
            ttsChunkIndex = Math.max(0, ttsChunks.size() - 1);
        }
        ttsSession++;
        if (adapter != null) {
            adapter.setAudioState(ttsActivePosition, !ttsPaused);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTts();
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Nullable
    private RouteModel readRoute() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(RoutePreviewActivity.EXTRA_ROUTE, RouteModel.class);
        }
        Object o = getIntent().getSerializableExtra(RoutePreviewActivity.EXTRA_ROUTE);
        return o instanceof RouteModel ? (RouteModel) o : null;
    }
}
