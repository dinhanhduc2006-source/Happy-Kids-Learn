package com.example.happykidlearn.member3.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BaseCanvasView extends View {

    private static class Stroke {
        Path path;
        Paint paint;

        Stroke(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }

    public interface OnStrokeListener {
        void onStrokeDrawn();
    }

    private List<Stroke> strokes = new ArrayList<>();
    private Path currentPath;
    private Paint currentPaint;

    private int currentColor = Color.BLACK;
    private float strokeWidthPx;
    private boolean isEraserMode = false;

    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4f;
    private boolean isMoved = false;

    private OnStrokeListener strokeListener;

    public BaseCanvasView(Context context) {
        super(context);
        init();
    }

    public BaseCanvasView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        // Quy đổi độ dày nét bút mặc định 10dp sang pixels
        strokeWidthPx = 10f * getResources().getDisplayMetrics().density;
    }

    private void setupNewPaint() {
        currentPaint = new Paint();
        currentPaint.setAntiAlias(true);
        currentPaint.setDither(true);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(strokeWidthPx);

        if (isEraserMode) {
            currentPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            currentPaint.setColor(Color.TRANSPARENT);
        } else {
            currentPaint.setXfermode(null);
            currentPaint.setColor(currentColor);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Dùng saveLayer để tạo một bitmap off-screen cho quá trình vẽ.
        // Điều này giúp PorterDuff.Mode.CLEAR chỉ tẩy nét vẽ của chính canvas này
        // mà không làm lủng/đen background hay mất các View nằm dưới nó (ví dụ: nét chữ mẫu).
        int saveCount = canvas.saveLayer(0, 0, getWidth(), getHeight(), null);

        for (Stroke stroke : strokes) {
            canvas.drawPath(stroke.path, stroke.paint);
        }

        if (currentPath != null) {
            canvas.drawPath(currentPath, currentPaint);
        }

        canvas.restoreToCount(saveCount);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                currentPath = new Path();
                setupNewPaint();
                currentPath.moveTo(x, y);
                lastX = x;
                lastY = y;
                isMoved = false;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - lastX);
                float dy = Math.abs(y - lastY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    // Dùng quadTo (Bezier bậc 2) để vẽ đường cong mượt mà thay vì lineTo
                    currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                    lastX = x;
                    lastY = y;
                    isMoved = true;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                if (!isMoved) {
                    // Xử lý chạm 1 điểm (chấm tròn nhỏ) nếu không di chuyển ngón tay
                    currentPath.lineTo(x, y + 0.1f);
                } else {
                    currentPath.lineTo(lastX, lastY);
                }

                strokes.add(new Stroke(currentPath, currentPaint));
                currentPath = null;

                // Gọi callback để Activity biết có nét vẽ mới
                if (strokeListener != null) {
                    strokeListener.onStrokeDrawn();
                }
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    // ================= PUBLIC API ================= //

    public void setOnStrokeListener(OnStrokeListener listener) {
        this.strokeListener = listener;
    }

    public void setStrokeColor(int color) {
        this.currentColor = color;
        this.isEraserMode = false;
    }

    public void setStrokeWidthDp(float widthDp) {
        this.strokeWidthPx = widthDp * getResources().getDisplayMetrics().density;
    }

    public void setEraserMode(boolean enabled) {
        this.isEraserMode = enabled;
    }

    public void undo() {
        if (!strokes.isEmpty()) {
            strokes.remove(strokes.size() - 1);
            invalidate();
        }
    }

    public void clearCanvas() {
        strokes.clear();
        invalidate();
    }

    public boolean isEmpty() {
        return strokes.isEmpty();
    }

    public int getStrokeCount() {
        return strokes.size();
    }
}
