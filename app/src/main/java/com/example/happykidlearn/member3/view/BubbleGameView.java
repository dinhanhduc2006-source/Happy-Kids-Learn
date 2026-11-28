package com.example.happykidlearn.member3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.happykidlearn.R;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class BubbleGameView extends View {

    public interface OnBubblePopListener {
        void onPop();
    }

    private static class Bubble {
        float x, y;
        float radius;
        float speed;
        int color;
        Paint paint;

        Bubble(float x, float y, float radius, float speed, int color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.speed = speed;
            this.color = color;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setStyle(Paint.Style.FILL);
            // Hiệu ứng bóng phản chiếu 3D
            paint.setShader(new RadialGradient(x - radius/3, y - radius/3, radius,
                    Color.WHITE, color, Shader.TileMode.CLAMP));
        }

        void updateShader() {
            paint.setShader(new RadialGradient(x - radius/3, y - radius/3, radius,
                    Color.WHITE, color, Shader.TileMode.CLAMP));
        }
    }

    private static class PopEffect {
        float x, y;
        float radius;
        int alpha;
        Paint paint;

        PopEffect(float x, float y, float maxRadius, int color) {
            this.x = x;
            this.y = y;
            this.radius = maxRadius / 2;
            this.alpha = 255;

            paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(6f);
        }
    }

    private final List<Bubble> bubbles = new ArrayList<>();
    private final List<PopEffect> popEffects = new ArrayList<>();

    private boolean isPlaying = false;
    private final Random random = new Random();
    private OnBubblePopListener popListener;

    private int[] bubbleColors;
    private float density;
    private float touchBufferPx;

    private long lastSpawnTime = 0;
    private int spawnIntervalMs = 800; // Mặc định 800ms tạo 1 quả mới

    public BubbleGameView(Context context) {
        super(context);
        init();
    }

    public BubbleGameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        density = getResources().getDisplayMetrics().density;
        // 12dp vùng đệm bấm cho trẻ dễ bấm trúng
        touchBufferPx = 12f * density;

        bubbleColors = new int[]{
                ContextCompat.getColor(getContext(), R.color.m3_color_red),
                ContextCompat.getColor(getContext(), R.color.m3_color_orange),
                ContextCompat.getColor(getContext(), R.color.m3_color_yellow),
                ContextCompat.getColor(getContext(), R.color.m3_color_green),
                ContextCompat.getColor(getContext(), R.color.m3_color_blue),
                ContextCompat.getColor(getContext(), R.color.m3_color_purple),
                ContextCompat.getColor(getContext(), R.color.m3_color_pink)
        };
    }

    public void setOnBubblePopListener(OnBubblePopListener listener) {
        this.popListener = listener;
    }

    public void startGame() {
        if (!isPlaying) {
            isPlaying = true;
            lastSpawnTime = System.currentTimeMillis();
            postInvalidateOnAnimation();
        }
    }

    public void pauseGame() {
        isPlaying = false;
    }

    public void resetGame() {
        isPlaying = false;
        bubbles.clear();
        popEffects.clear();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (getWidth() == 0 || getHeight() == 0) return;

        if (isPlaying) {
            updateGameState();
        }

        // Vẽ bong bóng
        for (Bubble b : bubbles) {
            canvas.drawCircle(b.x, b.y, b.radius, b.paint);
        }

        // Vẽ hiệu ứng nổ
        for (PopEffect p : popEffects) {
            p.paint.setAlpha(p.alpha);
            canvas.drawCircle(p.x, p.y, p.radius, p.paint);
        }

        if (isPlaying || !popEffects.isEmpty()) {
            postInvalidateOnAnimation();
        }
    }

    private void updateGameState() {
        long currentTime = System.currentTimeMillis();

        // Sinh bóng mới
        if (currentTime - lastSpawnTime > spawnIntervalMs) {
            spawnBubble();
            lastSpawnTime = currentTime;
            // Tăng tốc độ sinh ngẫu nhiên một chút
            spawnIntervalMs = 600 + random.nextInt(400);
        }

        // Di chuyển bóng bay lên
        Iterator<Bubble> iterator = bubbles.iterator();
        while (iterator.hasNext()) {
            Bubble b = iterator.next();
            b.y -= b.speed;
            b.updateShader();

            // Xóa bóng đã bay khỏi màn hình trên cùng
            if (b.y + b.radius < 0) {
                iterator.remove();
            }
        }

        // Cập nhật hiệu ứng nổ
        Iterator<PopEffect> popIter = popEffects.iterator();
        while (popIter.hasNext()) {
            PopEffect p = popIter.next();
            p.radius += 2f * density;
            p.alpha -= 15;
            if (p.alpha <= 0) {
                popIter.remove();
            }
        }
    }

    private void spawnBubble() {
        // Bán kính từ 30dp đến 50dp
        float radius = (30f + random.nextInt(21)) * density;
        float x = radius + random.nextInt((int) (getWidth() - 2 * radius));
        float y = getHeight() + radius;
        // Tốc độ từ 2dp đến 5dp mỗi khung hình
        float speed = (2f + random.nextFloat() * 3f) * density;
        int color = bubbleColors[random.nextInt(bubbleColors.length)];

        bubbles.add(new Bubble(x, y, radius, speed, color));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN && isPlaying) {
            float tx = event.getX();
            float ty = event.getY();

            // Duyệt ngược để ưu tiên bóng vẽ đè trên cùng
            for (int i = bubbles.size() - 1; i >= 0; i--) {
                Bubble b = bubbles.get(i);
                float dx = tx - b.x;
                float dy = ty - b.y;
                float distSq = dx * dx + dy * dy;
                float hitRadius = b.radius + touchBufferPx;

                if (distSq <= hitRadius * hitRadius) {
                    // Trúng bong bóng!
                    popEffects.add(new PopEffect(b.x, b.y, b.radius, b.color));
                    bubbles.remove(i);

                    if (popListener != null) {
                        popListener.onPop();
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
}
