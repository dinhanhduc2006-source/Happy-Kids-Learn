package com.example.happykidlearn.member3.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FloodFillView extends View {

    public interface OnFillListener {
        void onFillSuccess();
    }

    private Bitmap bitmap;
    private int currentDrawableResId = -1;
    private int fillColor = Color.RED;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isFilling = false;
    private OnFillListener listener;

    public FloodFillView(Context context) {
        super(context);
    }

    public FloodFillView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnFillListener(OnFillListener listener) {
        this.listener = listener;
    }

    public void setFillColor(int color) {
        this.fillColor = color;
    }

    public void setDrawableRes(int resId) {
        this.currentDrawableResId = resId;
        resetCanvas();
    }

    public void resetCanvas() {
        if (getWidth() > 0 && getHeight() > 0 && currentDrawableResId != -1) {
            Bitmap newBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(newBitmap);
            canvas.drawColor(Color.WHITE); // Nền trắng chuẩn

            Drawable drawable = ContextCompat.getDrawable(getContext(), currentDrawableResId);
            if (drawable != null) {
                // Tính toán để căn giữa tranh
                int w = drawable.getIntrinsicWidth();
                int h = drawable.getIntrinsicHeight();

                // Scale tranh vừa màn hình nhưng giữ tỷ lệ
                float scale = Math.min((float) getWidth() / w, (float) getHeight() / h) * 0.9f;
                int scaledW = (int) (w * scale);
                int scaledH = (int) (h * scale);

                int left = (getWidth() - scaledW) / 2;
                int top = (getHeight() - scaledH) / 2;

                drawable.setBounds(left, top, left + scaledW, top + scaledH);
                drawable.draw(canvas);
            }

            bitmap = newBitmap;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        resetCanvas();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (bitmap != null && x >= 0 && x < bitmap.getWidth() && y >= 0 && y < bitmap.getHeight()) {
                if (!isFilling) {
                    floodFillAsync(x, y);
                }
            }
            return true;
        }
        return super.onTouchEvent(event);
    }

    private void floodFillAsync(int x, int y) {
        int targetColor = bitmap.getPixel(x, y);

        // Không tô nếu màu đã giống hoặc chạm vào viền đen
        if (targetColor == fillColor || isBlackBorder(targetColor)) {
            return;
        }

        isFilling = true;
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // Lấy toàn bộ pixels ra mảng 1D để xử lý tốc độ cao
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        int fillC = this.fillColor; // Lưu màu hiện tại vào biến final cho thread

        executorService.execute(() -> {
            Queue<Point> queue = new ArrayDeque<>();
            queue.add(new Point(x, y));

            while (!queue.isEmpty()) {
                Point p = queue.poll();
                int px = p.x;
                int py = p.y;

                if (pixels[py * width + px] != targetColor) continue;

                // Span left
                int left = px;
                while (left > 0 && pixels[py * width + (left - 1)] == targetColor) {
                    left--;
                }

                // Span right
                int right = px;
                while (right < width - 1 && pixels[py * width + (right + 1)] == targetColor) {
                    right++;
                }

                for (int i = left; i <= right; i++) {
                    pixels[py * width + i] = fillC;
                    if (py > 0 && pixels[(py - 1) * width + i] == targetColor) {
                        queue.add(new Point(i, py - 1));
                    }
                    if (py < height - 1 && pixels[(py + 1) * width + i] == targetColor) {
                        queue.add(new Point(i, py + 1));
                    }
                }
            }

            // Đổ mảng pixels ngược lại vào Bitmap
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            isFilling = false;

            post(() -> {
                invalidate();
                if (listener != null) {
                    listener.onFillSuccess();
                }
            });
        });
    }

    private boolean isBlackBorder(int color) {
        // Kiểm tra xem màu có phải đen hay gần đen (do viền vector có khử răng cưa anti-alias)
        return Color.red(color) < 50 && Color.green(color) < 50 && Color.blue(color) < 50;
    }
}
