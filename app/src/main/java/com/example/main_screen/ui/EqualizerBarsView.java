package com.example.main_screen.ui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

/**
 * Простая анимация «эквалайзера» при воспроизведении аудио (TTS).
 */
public class EqualizerBarsView extends View {

    private static final int BARS = 5;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final float[] levels = new float[BARS];
    private ValueAnimator animator;
    private boolean playing;

    public EqualizerBarsView(Context context) {
        super(context);
        init();
    }

    public EqualizerBarsView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EqualizerBarsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(0xFFFFFFFF);
    }

    public void setPlaying(boolean play) {
        if (playing == play) {
            return;
        }
        playing = play;
        if (play) {
            startAnimator();
        } else {
            stopAnimator();
            for (int i = 0; i < BARS; i++) {
                levels[i] = 0.15f;
            }
            invalidate();
        }
    }

    private void startAnimator() {
        stopAnimator();
        animator = ValueAnimator.ofFloat(0f, 1f);
        animator.setDuration(350);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            float t = (float) a.getAnimatedValue();
            for (int i = 0; i < BARS; i++) {
                double phase = t * Math.PI * 2 + i * 0.7;
                levels[i] = (float) (0.2 + 0.8 * (0.5 + 0.5 * Math.sin(phase)));
            }
            invalidate();
        });
        animator.start();
    }

    private void stopAnimator() {
        if (animator != null) {
            animator.cancel();
            animator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        if (w <= 0 || h <= 0) {
            return;
        }
        float gap = w * 0.08f;
        float barW = (w - gap * (BARS + 1)) / BARS;
        float left = gap;
        for (int i = 0; i < BARS; i++) {
            float bh = h * levels[i];
            float top = h - bh;
            canvas.drawRoundRect(left, top, left + barW, h, barW / 2f, barW / 2f, paint);
            left += barW + gap;
        }
    }
}
