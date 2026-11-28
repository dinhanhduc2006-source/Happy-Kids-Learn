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

public class HandwritingBoardView extends View {

    public interface OnStrokeFinishListener {
        void onStrokeFinish();
    }

    private Path currentPath;
    private Paint currentPaint;

    private Paint gridPaint;
    private Paint guidePaint;
    private Paint startDotOuterPaint;
    private Paint startDotInnerPaint;

    private String guideText = "o";
    private int currentColor = Color.BLACK;
    private float strokeWidthPx;

    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4f;
    private boolean isMoved = false;

    private OnStrokeFinishListener strokeFinishListener;

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
        strokeWidthPx = 24f * density;

        // Dòng kẻ mờ vở ô ly
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#CBD5E1"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1.5f * density);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{8f * density, 6f * density}, 0));

        // Chữ mẫu nét đứt mờ
        guidePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guidePaint.setColor(Color.parseColor("#94A3B8"));
        guidePaint.setStyle(Paint.Style.STROKE);
        guidePaint.setStrokeWidth(12f * density);
        guidePaint.setStrokeCap(Paint.Cap.ROUND);
        guidePaint.setStrokeJoin(Paint.Join.ROUND);
        guidePaint.setPathEffect(new DashPathEffect(new float[]{18f * density, 14f * density}, 0));
        guidePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));

        // Chấm tròn điểm đặt bút màu cam san hô
        startDotOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startDotOuterPaint.setColor(Color.parseColor("#F28B50"));
        startDotOuterPaint.setStyle(Paint.Style.FILL);

        startDotInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startDotInnerPaint.setColor(Color.WHITE);
        startDotInnerPaint.setStyle(Paint.Style.FILL);

        setupNewPaint();
    }

    private void setupNewPaint() {
        currentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        currentPaint.setDither(true);
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeJoin(Paint.Join.ROUND);
        currentPaint.setStrokeCap(Paint.Cap.ROUND);
        currentPaint.setStrokeWidth(strokeWidthPx);
        currentPaint.setColor(currentColor);
    }

    public void setOnStrokeFinishListener(OnStrokeFinishListener listener) {
        this.strokeFinishListener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        if (width == 0 || height == 0) return;

        // 1. Vẽ dòng kẻ ngang ô ly mờ
        float lineSpacing = height / 5f;
        for (int i = 1; i < 5; i++) {
            float y = lineSpacing * i;
            canvas.drawLine(20, y, width - 20, y, gridPaint);
        }

        // 2. Vẽ chữ mẫu nét đứt thường cỡ lớn chính giữa
        float textX = 0, textY = 0;
        Rect bounds = new Rect();

        if (guideText != null && !guideText.isEmpty()) {
            guidePaint.setTextSize(height * 0.55f);
            guidePaint.getTextBounds(guideText, 0, guideText.length(), bounds);

            textX = (width - bounds.width()) / 2f - bounds.left;
            textY = (height + bounds.height()) / 2f - bounds.bottom;

            canvas.drawText(guideText, textX, textY, guidePaint);
        }

        // 3. Vẽ điểm đặt bút gợi ý (Chấm cam san hô)
        if (bounds.width() > 0 && bounds.height() > 0) {
            float[] startRel = getStartPointRelativeOffset(guideText);
            float dotX = textX + bounds.left + bounds.width() * startRel[0];
            float dotY = textY + bounds.top + bounds.height() * startRel[1];

            float density = getResources().getDisplayMetrics().density;
            canvas.drawCircle(dotX, dotY, 10f * density, startDotOuterPaint);
            canvas.drawCircle(dotX, dotY, 4f * density, startDotInnerPaint);
        }

        // 4. Vẽ duy nhất 1 nét liền mạch của trẻ em
        if (currentPath != null) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    private float[] getStartPointRelativeOffset(String letter) {
        if (letter == null) return new float[]{0.5f, 0.2f};
        switch (letter.toLowerCase()) {
            case "o":
            case "c":
                return new float[]{0.70f, 0.25f}; // Góc trên bên phải
            case "e":
                return new float[]{0.30f, 0.50f}; // Giữa bên trái
            case "l":
                return new float[]{0.45f, 0.10f}; // Đỉnh trên
            case "s":
                return new float[]{0.60f, 0.20f};
            case "n":
            case "m":
            case "v":
            case "r":
            case "u":
            default:
                return new float[]{0.25f, 0.25f}; // Góc trên bên trái
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // RÀNG BUỘC 1 NÉT: Xóa sạch nét cũ khi chạm ngón tay mới
                currentPath = new Path();
                setupNewPaint();
                currentPath.moveTo(x, y);
                lastX = x;
                lastY = y;
                isMoved = false;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                if (currentPath != null) {
                    float dx = Math.abs(x - lastX);
                    float dy = Math.abs(y - lastY);
                    if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                        currentPath.quadTo(lastX, lastY, (x + lastX) / 2, (y + lastY) / 2);
                        lastX = x;
                        lastY = y;
                        isMoved = true;
                    }
                    invalidate();
                }
                return true;

            case MotionEvent.ACTION_UP:
                if (currentPath != null) {
                    if (!isMoved) {
                        currentPath.lineTo(x, y + 0.1f);
                    } else {
                        currentPath.lineTo(lastX, lastY);
                    }
                    invalidate();

                    if (strokeFinishListener != null) {
                        strokeFinishListener.onStrokeFinish();
                    }
                }
                return true;
        }
        return super.onTouchEvent(event);
    }

    // ================= PUBLIC API ================= //

    public void setGuideText(String text) {
        this.guideText = text != null ? text.toLowerCase() : "o";
        clearCanvas();
    }

    public void setStrokeColor(int color) {
        this.currentColor = color;
    }

    public void clearCanvas() {
        currentPath = null;
        invalidate();
    }

    public boolean isEmpty() {
        return currentPath == null;
    }

    /**
     * Tính toán phần trăm diện tích chữ mẫu được trẻ tô kín (Bitmap 128x128 trong bộ nhớ)
     */
    public float calculateCoveragePercentage() {
        if (currentPath == null || guideText == null || guideText.isEmpty() || getWidth() == 0 || getHeight() == 0) {
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

        // 2. Dựng 1 nét vẽ của trẻ em trong Bitmap nhỏ
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);

        Path scaledPath = new Path(currentPath);
        scaledPath.transform(matrix);

        Paint sPaint = new Paint(currentPaint);
        sPaint.setStrokeWidth(currentPaint.getStrokeWidth() * scaleY);
        userCanvas.drawPath(scaledPath, sPaint);

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
