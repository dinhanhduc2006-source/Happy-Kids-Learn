package com.example.happykidlearn.member3.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HandwritingBoardView extends View {

    private static class Stroke {
        Path path;
        Paint paint;

        Stroke(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }

    private final List<Stroke> strokes = new ArrayList<>();
    private Path currentPath;
    private Paint currentPaint;

    private Paint gridPaint;
    private Paint guidePaint;

    private String guideText = "A";
    private int currentColor = Color.BLACK;
    private float strokeWidthPx;
    private boolean isEraserMode = false;

    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4f;
    private boolean isMoved = false;

    public HandwritingBoardView(Context context) {
        super(context);
        init();
    }

    public HandwritingBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HandwritingBoardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        float density = getResources().getDisplayMetrics().density;
        strokeWidthPx = 24f * density; // Nét bút dày 24dp cho bé dễ tô

        // Paint kẻ dòng ô ly mờ kiểu vở tập viết
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#CBD5E1"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1.5f * density);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{8f * density, 6f * density}, 0));

        // Paint chữ mẫu nét đứt mờ
        guidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guidePaint.setColor(Color.parseColor("#94A3B8"));
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(12f * density);
        guidePaint.setStrokeCap(Paint.Cap.ROUND);
        guidePaint.setStrokeJoin(Paint.Join.ROUND);
        guidePaint.setPathEffect(new DashPathEffect(new float[]{20f * density, 16f * density}, 0));
        guidePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    private void setupNewPaint() {
        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        // 1. Vẽ các đường kẻ ngang ô ly mờ kiểu vở học sinh
        float lineSpacing = height / 5f;
        for (int i = 1; i < 5; i++) {
            float y = lineSpacing * i;
            canvas.drawLine(20, y, width - 20, y, gridPaint);
        }

        // 2. Vẽ chữ mẫu nét đứt căn chính giữa
        if (guideText != null && !guideText.isEmpty()) {
            guidePaint.setTextSize(height * 0.60f);
            Rect bounds = new Rect();
            guidePaint.getTextBounds(guideText, 0, guideText.length(), bounds);

            float textX = (width - bounds.width()) / 2f - bounds.left;
            float textY = (height + bounds.height()) / 2f - bounds.bottom;

            canvas.drawText(guideText, textX, textY, guidePaint);
        }

        // 3. Vẽ các nét tô của trẻ em
        int saveCount = canvas.saveLayer(0, 0, width, height, null);

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
                    currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                    lastX = x;
                    lastY = y;
                    isMoved = true;
                }
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                if (!isMoved) {
                    currentPath.lineTo(x, y + 0.1f);
                } else {
                    currentPath.lineTo(lastX, lastY);
                }
                strokes.add(new Stroke(currentPath, currentPaint));
                currentPath = null;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    // ================= PUBLIC API ================= //

    public void setGuideText(String text) {
        this.guideText = text;
        clearCanvas();
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
        currentPath = null;
        invalidate();
    }

    public boolean isEmpty() {
        return strokes.isEmpty();
    }

    /**
     * Tính toán phần trăm diện tích chữ mẫu được trẻ tô kín (Bitmap 128x128 trong bộ nhớ)
     */
    public float calculateCoveragePercentage() {
        if (strokes.isEmpty() || guideText == null || guideText.isEmpty() || getWidth() == 0 || getHeight() == 0) {
            return 0f;
        }

        int size = 128;
        Bitmap guideBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas guideCanvas = new Canvas(guideBitmap);

        Bitmap userBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas userCanvas = new Canvas(userBitmap);

        float scaleX = (float) size / getWidth();
        float scaleY = (float) size / getHeight();

        // 1. Dựng chữ mẫu nét đặc trong Bitmap nhỏ
        Paint gPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gPaint.setColor(Color.BLACK);
        gPaint.setStyle(Paint.Style.STROKE);
        gPaint.setStrokeWidth(guidePaint.getStrokeWidth() * scaleY);
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setStrokeJoin(Paint.Join.ROUND);
        gPaint.setTypeface(guidePaint.getTypeface());
        gPaint.setTextSize(guidePaint.getTextSize() * scaleY);

        Rect bounds = new Rect();
        gPaint.getTextBounds(guideText, 0, guideText.length(), bounds);
        float textX = (size - bounds.width()) / 2f - bounds.left;
        float textY = (size + bounds.height()) / 2f - bounds.bottom;
        guideCanvas.drawText(guideText, textX, textY, gPaint);

        // 2. Dựng nét vẽ của trẻ em trong Bitmap nhỏ
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);

        for (Stroke stroke : strokes) {
            Path scaledPath = new Path(stroke.path);
            scaledPath.transform(matrix);

            Paint sPaint = new Paint(stroke.paint);
            sPaint.setStrokeWidth(stroke.paint.getStrokeWidth() * scaleY);
            userCanvas.drawPath(scaledPath, sPaint);
        }

        // 3. Đếm số điểm ảnh trùng khớp
        int[] guidePixels = new int[size * size];
        int[] userPixels = new int[size * size];
        guideBitmap.getPixels(guidePixels, 0, size, 0, 0, size, size);
        userBitmap.getPixels(userPixels, 0, size, 0, 0, size, size);

        int totalGuidePixels = 0;
        int coveredPixels = 0;

        for (int i = 0; i < guidePixels.length; i++) {
            boolean isGuidePixel = Color.alpha(guidePixels[i]) > 50;
            if (isGuidePixel) {
                totalGuidePixels++;
                boolean isUserPixel = Color.alpha(userPixels[i]) > 50;
                if (isUserPixel) {
                    coveredPixels++;
                }
            }
        }

        guideBitmap.recycle();
        userBitmap.recycle();

        if (totalGuidePixels == 0) return 0f;
        return (coveredPixels * 100f) / totalGuidePixels;
    }
}
