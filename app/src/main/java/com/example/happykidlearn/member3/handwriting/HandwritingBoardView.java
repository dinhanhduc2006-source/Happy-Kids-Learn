package com.example.happykidlearn.member3.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
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
    private Paint guideThickPaint;
    private Paint guideCorePaint;
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

        // Nét chữ mẫu 1 nét dày màu xám nhạt mờ (15dp)
        guideThickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guideThickPaint.setColor(Color.parseColor("#CBD5E1"));
        guideThickPaint.setStyle(Paint.Style.STROKE);
        guideThickPaint.setStrokeWidth(15f * density);
        guideThickPaint.setStrokeCap(Paint.Cap.ROUND);
        guideThickPaint.setStrokeJoin(Paint.Join.ROUND);

        // Nét đứt lõi chính giữa (2dp) cho cảm giác chữ nét đứt đúng vở tập viết
        guideCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guideCorePaint.setColor(Color.parseColor("#94A3B8"));
        guideCorePaint.setStyle(Paint.Style.STROKE);
        guideCorePaint.setStrokeWidth(2f * density);
        guideCorePaint.setStrokeCap(Paint.Cap.ROUND);
        guideCorePaint.setStrokeJoin(Paint.Join.ROUND);
        guideCorePaint.setPathEffect(new DashPathEffect(new float[]{8f * density, 6f * density}, 0));

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

        // 1. Vẽ dòng kẻ ngang ô ly mờ kiểu vở tiểu học
        float lineSpacing = height / 5f;
        for (int i = 1; i < 5; i++) {
            float y = lineSpacing * i;
            canvas.drawLine(20, y, width - 20, y, gridPaint);
        }

        // 2. Tạo Path chữ mẫu 1 nét duy nhất (single-stroke centerline)
        float[] startPoint = new float[2];
        Path guidePath = createCenterlineLetterPath(guideText, width, height, startPoint);

        // Vẽ lớp nét mờ rộng và nét đứt lõi chính giữa
        canvas.drawPath(guidePath, guideThickPaint);
        canvas.drawPath(guidePath, guideCorePaint);

        // 3. Vẽ chấm tròn điểm đặt bút (Cam san hô) ĐÚNG tại tọa độ đầu tiên (x0, y0) của Path
        float density = getResources().getDisplayMetrics().density;
        canvas.drawCircle(startPoint[0], startPoint[1], 9f * density, startDotOuterPaint);
        canvas.drawCircle(startPoint[0], startPoint[1], 4f * density, startDotInnerPaint);

        // 4. Vẽ duy nhất 1 nét liền mạch do trẻ tô
        if (currentPath != null) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    /**
     * Dựng Path nét đơn (Centerline Path) 100% chữ thường theo chuẩn ô ly vở tập viết
     */
    private Path createCenterlineLetterPath(String letter, float width, float height, float[] outStartPoint) {
        Path path = new Path();
        float lineSpacing = height / 5f;

        float y1 = lineSpacing * 1; // đường kẻ trên cùng
        float y2 = lineSpacing * 2; // đường trung bình trên
        float y3 = lineSpacing * 3; // đường kẻ giữa (đỉnh chữ thường)
        float y4 = lineSpacing * 4; // đường kẻ đáy (chân chữ)

        float h = y4 - y3;       // chiều cao chuẩn của chữ thường (o, c, e...)
        float w = h * 0.70f;     // chiều rộng chuẩn của chữ
        float cx = width / 2f;
        float cy = (y3 + y4) / 2f;

        float topY = y3;
        float bottomY = y4;

        if (letter == null) letter = "o";

        switch (letter.toLowerCase()) {
            case "o": {
                // Khởi đầu từ đỉnh bên phải, xoay vòng tròn sang trái mượt mà
                float x0 = cx + w * 0.35f;
                float y0 = topY + h * 0.15f;
                path.moveTo(x0, y0);
                path.cubicTo(
                        cx - w * 0.6f, topY - h * 0.05f,
                        cx - w * 0.6f, bottomY + h * 0.05f,
                        cx, bottomY
                );
                path.cubicTo(
                        cx + w * 0.6f, bottomY + h * 0.05f,
                        cx + w * 0.6f, topY - h * 0.05f,
                        x0, y0
                );
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "c": {
                float x0 = cx + w * 0.40f;
                float y0 = topY + h * 0.20f;
                path.moveTo(x0, y0);
                path.cubicTo(
                        cx - w * 0.5f, topY - h * 0.05f,
                        cx - w * 0.5f, bottomY + h * 0.05f,
                        x0, bottomY - h * 0.15f
                );
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "e": {
                float x0 = cx - w * 0.35f;
                float y0 = cy + h * 0.08f;
                path.moveTo(x0, y0);
                path.lineTo(cx + w * 0.35f, y0);
                path.cubicTo(
                        cx + w * 0.5f, topY - h * 0.10f,
                        cx - w * 0.5f, topY - h * 0.10f,
                        cx - w * 0.4f, cy + h * 0.2f
                );
                path.cubicTo(
                        cx - w * 0.3f, bottomY + h * 0.08f,
                        cx + w * 0.25f, bottomY + h * 0.08f,
                        cx + w * 0.40f, bottomY - h * 0.15f
                );
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "l": {
                // Chữ l vươn cao lên đường kẻ y1
                float x0 = cx - w * 0.20f;
                float y0 = y2 + h * 0.3f;
                path.moveTo(x0, y0);
                path.cubicTo(
                        cx + w * 0.35f, y1 - h * 0.05f,
                        cx + w * 0.05f, y1 - h * 0.05f,
                        cx - w * 0.05f, y2 + h * 0.5f
                );
                path.lineTo(cx - w * 0.05f, y4 - h * 0.2f);
                path.quadTo(cx - w * 0.05f, y4, cx + w * 0.35f, y4 - h * 0.05f);
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "s": {
                float x0 = cx + w * 0.35f;
                float y0 = topY + h * 0.18f;
                path.moveTo(x0, y0);
                path.cubicTo(
                        cx - w * 0.4f, topY - h * 0.08f,
                        cx - w * 0.3f, cy - h * 0.05f,
                        cx, cy
                );
                path.cubicTo(
                        cx + w * 0.4f, cy + h * 0.05f,
                        cx + w * 0.4f, bottomY + h * 0.08f,
                        cx - w * 0.35f, bottomY - h * 0.1f
                );
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "n": {
                float x0 = cx - w * 0.35f;
                float y0 = topY + h * 0.1f;
                path.moveTo(x0, y0);
                path.lineTo(x0, bottomY);
                path.moveTo(x0, topY + h * 0.35f);
                path.cubicTo(
                        cx - w * 0.15f, topY - h * 0.05f,
                        cx + w * 0.35f, topY - h * 0.05f,
                        cx + w * 0.35f, topY + h * 0.5f
                );
                path.lineTo(cx + w * 0.35f, bottomY - h * 0.1f);
                path.quadTo(cx + w * 0.35f, bottomY, cx + w * 0.50f, bottomY);
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "m": {
                float wM = w * 1.3f;
                float x0 = cx - wM * 0.40f;
                float y0 = topY + h * 0.1f;
                path.moveTo(x0, y0);
                path.lineTo(x0, bottomY);

                path.moveTo(x0, topY + h * 0.35f);
                path.cubicTo(
                        cx - wM * 0.20f, topY - h * 0.05f,
                        cx, topY - h * 0.05f,
                        cx, topY + h * 0.5f
                );
                path.lineTo(cx, bottomY);

                path.moveTo(cx, topY + h * 0.35f);
                path.cubicTo(
                        cx + wM * 0.20f, topY - h * 0.05f,
                        cx + wM * 0.40f, topY - h * 0.05f,
                        cx + wM * 0.40f, topY + h * 0.5f
                );
                path.lineTo(cx + wM * 0.40f, bottomY - h * 0.1f);
                path.quadTo(cx + wM * 0.40f, bottomY, cx + wM * 0.52f, bottomY);
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "v": {
                float x0 = cx - w * 0.45f;
                float y0 = topY + h * 0.1f;
                path.moveTo(x0, y0);
                path.lineTo(cx - w * 0.05f, bottomY - h * 0.05f);
                path.quadTo(cx, bottomY, cx + w * 0.1f, bottomY - h * 0.05f);
                path.lineTo(cx + w * 0.45f, topY + h * 0.1f);
                path.quadTo(cx + w * 0.55f, topY, cx + w * 0.6f, topY + h * 0.05f);
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "r": {
                float x0 = cx - w * 0.35f;
                float y0 = topY + h * 0.1f;
                path.moveTo(x0, y0);
                path.lineTo(x0, bottomY);
                path.moveTo(x0, topY + h * 0.35f);
                path.cubicTo(
                        cx - w * 0.10f, topY - h * 0.05f,
                        cx + w * 0.35f, topY - h * 0.05f,
                        cx + w * 0.45f, topY + h * 0.2f
                );
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
            case "u":
            default: {
                float x0 = cx - w * 0.40f;
                float y0 = topY + h * 0.1f;
                path.moveTo(x0, y0);
                path.lineTo(x0, bottomY - h * 0.25f);
                path.cubicTo(
                        x0, bottomY + h * 0.05f,
                        cx + w * 0.30f, bottomY + h * 0.05f,
                        cx + w * 0.30f, bottomY - h * 0.25f
                );
                path.lineTo(cx + w * 0.30f, topY + h * 0.1f);
                path.moveTo(cx + w * 0.30f, topY + h * 0.35f);
                path.lineTo(cx + w * 0.30f, bottomY - h * 0.1f);
                path.quadTo(cx + w * 0.30f, bottomY, cx + w * 0.45f, bottomY);
                if (outStartPoint != null) { outStartPoint[0] = x0; outStartPoint[1] = y0; }
                break;
            }
        }
        return path;
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
     * Tính toán phần trăm diện tích chữ mẫu 1 nét được trẻ tô kín (Bitmap 128x128 trong bộ nhớ)
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

        // 1. Dựng Path chữ mẫu 1 nét đặc trong Bitmap nhỏ
        Path guidePath = createCenterlineLetterPath(guideText, size, size, null);

        Paint gPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gPaint.setColor(Color.BLACK);
        gPaint.setStyle(Paint.Style.STROKE);
        gPaint.setStrokeWidth(guideThickPaint.getStrokeWidth() * scaleY);
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setStrokeJoin(Paint.Join.ROUND);

        guideCanvas.drawPath(guidePath, gPaint);

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
