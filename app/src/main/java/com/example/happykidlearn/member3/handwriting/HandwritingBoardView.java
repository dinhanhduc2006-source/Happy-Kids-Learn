package com.example.happykidlearn.member3.handwriting;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HandwritingBoardView extends View {

    public interface OnStrokeFinishListener {
        void onStrokeFinish();
    }

    public static class GuideStroke {
        public Path path;
        public float startX;
        public float startY;

        public GuideStroke(Path path, float startX, float startY) {
            this.path = path;
            this.startX = startX;
            this.startY = startY;
        }
    }

    public static class UserStroke {
        public Path path;
        public Paint paint;

        public UserStroke(Path path, Paint paint) {
            this.path = path;
            this.paint = paint;
        }
    }

    private final List<UserStroke> userStrokes = new ArrayList<>();
    private Path currentPath;
    private Paint currentPaint;

    private Paint gridPaint;
    private Paint gridBasePaint;
    private Paint guideThickPaint;
    private Paint guideCorePaint;
    private Paint startDotOuterPaint;
    private Paint startDotInnerPaint;
    private Paint numberTextPaint;

    private String guideText = "a";
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
        strokeWidthPx = 25f * density;

        // Dòng kẻ ô ly mờ
        gridPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridPaint.setColor(Color.parseColor("#CBD5E1"));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(1.5f * density);
        gridPaint.setPathEffect(new DashPathEffect(new float[]{8f * density, 6f * density}, 0));

        // Dòng kẻ đáy (baseline) đậm hơn nhẹ
        gridBasePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gridBasePaint.setColor(Color.parseColor("#94A3B8"));
        gridBasePaint.setStyle(Paint.Style.STROKE);
        gridBasePaint.setStrokeWidth(2f * density);

        // Nét chữ mẫu 1 nét/nhiều nét dày mờ (22dp)
        guideThickPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guideThickPaint.setColor(Color.parseColor("#CBD5E1"));
        guideThickPaint.setStyle(Paint.Style.STROKE);
        guideThickPaint.setStrokeWidth(22f * density);
        guideThickPaint.setStrokeCap(Paint.Cap.ROUND);
        guideThickPaint.setStrokeJoin(Paint.Join.ROUND);

        // Nét đứt lõi chính giữa (3dp)
        guideCorePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        guideCorePaint.setColor(Color.parseColor("#94A3B8"));
        guideCorePaint.setStyle(Paint.Style.STROKE);
        guideCorePaint.setStrokeWidth(3f * density);
        guideCorePaint.setStrokeCap(Paint.Cap.ROUND);
        guideCorePaint.setStrokeJoin(Paint.Join.ROUND);
        guideCorePaint.setPathEffect(new DashPathEffect(new float[]{10f * density, 8f * density}, 0));

        // Chấm tròn điểm đặt bút màu cam san hô
        startDotOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startDotOuterPaint.setColor(Color.parseColor("#F28B50"));
        startDotOuterPaint.setStyle(Paint.Style.FILL);

        startDotInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        startDotInnerPaint.setColor(Color.WHITE);
        startDotInnerPaint.setStyle(Paint.Style.FILL);

        numberTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        numberTextPaint.setColor(Color.WHITE);
        numberTextPaint.setTextSize(11f * density);
        numberTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        numberTextPaint.setTextAlign(Paint.Align.CENTER);

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

        float u = Math.min(width * 0.28f, height * 0.27f);
        float cy = height / 2f;

        float baseY = cy + 0.45f * u;
        float xTopY = baseY - 1.10f * u;
        float ascY  = baseY - 2.00f * u;
        float descY = baseY + 1.00f * u;

        // 1. Vẽ các đường kẻ ngang ô ly mờ chuẩn vở tiểu học
        canvas.drawLine(20, ascY, width - 20, ascY, gridPaint);
        canvas.drawLine(20, xTopY, width - 20, xTopY, gridPaint);
        canvas.drawLine(20, cy, width - 20, cy, gridPaint);
        canvas.drawLine(20, baseY, width - 20, baseY, gridBasePaint);
        if (descY < height - 10) {
            canvas.drawLine(20, descY, width - 20, descY, gridPaint);
        }

        // 2. Dựng danh sách nét mẫu của chữ hiện tại
        List<GuideStroke> guideStrokes = createVietnameseLowercaseLetterStrokes(guideText, width, height);

        // Vẽ các nét mẫu
        for (GuideStroke stroke : guideStrokes) {
            canvas.drawPath(stroke.path, guideThickPaint);
            canvas.drawPath(stroke.path, guideCorePaint);
        }

        // 3. Vẽ điểm đặt bút (Cam san hô) cho từng nét
        float density = getResources().getDisplayMetrics().density;
        for (int i = 0; i < guideStrokes.size(); i++) {
            GuideStroke stroke = guideStrokes.get(i);
            canvas.drawCircle(stroke.startX, stroke.startY, 11f * density, startDotOuterPaint);

            if (guideStrokes.size() > 1) {
                // Với chữ nhiều nét: vẽ số 1, 2, 3 trong chấm tròn
                String numStr = String.valueOf(i + 1);
                Rect bounds = new Rect();
                numberTextPaint.getTextBounds(numStr, 0, numStr.length(), bounds);
                float textY = stroke.startY + bounds.height() / 2f - bounds.bottom;
                canvas.drawText(numStr, stroke.startX, textY, numberTextPaint);
            } else {
                canvas.drawCircle(stroke.startX, stroke.startY, 4.5f * density, startDotInnerPaint);
            }
        }

        // 4. Vẽ các nét tô của trẻ em
        for (UserStroke stroke : userStrokes) {
            canvas.drawPath(stroke.path, stroke.paint);
        }

        if (currentPath != null) {
            canvas.drawPath(currentPath, currentPaint);
        }
    }

    /**
     * Dựng bộ Path chuẩn mực cho 29 chữ cái thường tiếng Việt
     */
    private List<GuideStroke> createVietnameseLowercaseLetterStrokes(String letter, float width, float height) {
        List<GuideStroke> strokes = new ArrayList<>();

        float u = Math.min(width * 0.28f, height * 0.27f);
        float cx = width / 2f;
        float cy = height / 2f;

        float baseY = cy + 0.45f * u;
        float xTopY = baseY - 1.10f * u;
        float ascY  = baseY - 2.00f * u;
        float descY = baseY + 1.00f * u;

        float h = baseY - xTopY; // Chiều cao thân chữ thường
        float w = h * 0.70f;     // Chiều rộng chuẩn

        String target = letter != null ? letter.toLowerCase() : "a";

        // Hàm hỗ trợ tạo nét bụng oval chuẩn cho a, ă, â, b, d, đ, g, o, ô, ơ, p, q
        Path ovalPath = new Path();
        float xO = cx + w * 0.35f;
        float yO = xTopY + h * 0.15f;
        ovalPath.moveTo(xO, yO);
        ovalPath.cubicTo(
                cx - w * 0.58f, xTopY - h * 0.05f,
                cx - w * 0.58f, baseY + h * 0.05f,
                cx, baseY
        );
        ovalPath.cubicTo(
                cx + w * 0.58f, baseY + h * 0.05f,
                cx + w * 0.58f, xTopY - h * 0.05f,
                xO, yO
        );

        // Mũ ^ cho â, ê, ô
        Path hat1 = new Path(); hat1.moveTo(cx - w * 0.3f, xTopY - u * 0.1f); hat1.lineTo(cx, xTopY - u * 0.35f);
        Path hat2 = new Path(); hat2.moveTo(cx, xTopY - u * 0.35f); hat2.lineTo(cx + w * 0.3f, xTopY - u * 0.1f);

        // Á ˘ cho ă
        Path breve = new Path();
        breve.moveTo(cx - w * 0.3f, xTopY - u * 0.35f);
        breve.quadTo(cx, xTopY - u * 0.08f, cx + w * 0.3f, xTopY - u * 0.35f);

        // Râu cho ơ, ư
        Path horn = new Path();
        horn.moveTo(cx + w * 0.35f, xTopY + h * 0.1f);
        horn.quadTo(cx + w * 0.55f, xTopY - u * 0.1f, cx + w * 0.38f, xTopY - u * 0.25f);

        switch (target) {
            case "a": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                Path p2 = new Path();
                p2.moveTo(cx + w * 0.35f, xTopY);
                p2.lineTo(cx + w * 0.35f, baseY - h * 0.18f);
                p2.quadTo(cx + w * 0.35f, baseY, cx + w * 0.55f, baseY);
                strokes.add(new GuideStroke(p2, cx + w * 0.35f, xTopY));
                break;
            }
            case "ă": {
                strokes.addAll(createVietnameseLowercaseLetterStrokes("a", width, height));
                strokes.add(new GuideStroke(breve, cx - w * 0.3f, xTopY - u * 0.35f));
                break;
            }
            case "â": {
                strokes.addAll(createVietnameseLowercaseLetterStrokes("a", width, height));
                strokes.add(new GuideStroke(hat1, cx - w * 0.3f, xTopY - u * 0.1f));
                strokes.add(new GuideStroke(hat2, cx, xTopY - u * 0.35f));
                break;
            }
            case "b": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.25f, xTopY + h * 0.3f);
                p1.cubicTo(cx + w * 0.35f, ascY - h * 0.05f, cx + w * 0.05f, ascY - h * 0.05f, cx - w * 0.05f, xTopY + h * 0.5f);
                p1.lineTo(cx - w * 0.05f, baseY);
                strokes.add(new GuideStroke(p1, cx - w * 0.25f, xTopY + h * 0.3f));

                Path p2 = new Path();
                p2.moveTo(cx - w * 0.05f, xTopY + h * 0.3f);
                p2.cubicTo(cx + w * 0.6f, xTopY - h * 0.05f, cx + w * 0.6f, baseY + h * 0.05f, cx - w * 0.05f, baseY);
                strokes.add(new GuideStroke(p2, cx - w * 0.05f, xTopY + h * 0.3f));
                break;
            }
            case "c": {
                Path p1 = new Path();
                float xC = cx + w * 0.42f; float yC = xTopY + h * 0.20f;
                p1.moveTo(xC, yC);
                p1.cubicTo(cx - w * 0.52f, xTopY - h * 0.05f, cx - w * 0.52f, baseY + h * 0.05f, xC, baseY - h * 0.18f);
                strokes.add(new GuideStroke(p1, xC, yC));
                break;
            }
            case "d": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                Path p2 = new Path();
                p2.moveTo(cx + w * 0.35f, ascY);
                p2.lineTo(cx + w * 0.35f, baseY - h * 0.18f);
                p2.quadTo(cx + w * 0.35f, baseY, cx + w * 0.55f, baseY);
                strokes.add(new GuideStroke(p2, cx + w * 0.35f, ascY));
                break;
            }
            case "đ": {
                strokes.addAll(createVietnameseLowercaseLetterStrokes("d", width, height));
                Path p3 = new Path();
                p3.moveTo(cx + w * 0.1f, ascY + u * 0.4f);
                p3.lineTo(cx + w * 0.6f, ascY + u * 0.4f);
                strokes.add(new GuideStroke(p3, cx + w * 0.1f, ascY + u * 0.4f));
                break;
            }
            case "e": {
                Path p1 = new Path();
                float xE = cx - w * 0.38f; float yE = cy + h * 0.08f;
                p1.moveTo(xE, yE);
                p1.lineTo(cx + w * 0.38f, yE);
                p1.cubicTo(cx + w * 0.52f, xTopY - h * 0.10f, cx - w * 0.52f, xTopY - h * 0.10f, cx - w * 0.42f, cy + h * 0.2f);
                p1.cubicTo(cx - w * 0.32f, baseY + h * 0.08f, cx + w * 0.28f, baseY + h * 0.08f, cx + w * 0.42f, baseY - h * 0.18f);
                strokes.add(new GuideStroke(p1, xE, yE));
                break;
            }
            case "ê": {
                strokes.addAll(createVietnameseLowercaseLetterStrokes("e", width, height));
                strokes.add(new GuideStroke(hat1, cx - w * 0.3f, xTopY - u * 0.1f));
                strokes.add(new GuideStroke(hat2, cx, xTopY - u * 0.35f));
                break;
            }
            case "g": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                Path p2 = new Path();
                p2.moveTo(cx + w * 0.35f, xTopY);
                p2.lineTo(cx + w * 0.35f, descY - h * 0.3f);
                p2.cubicTo(cx + w * 0.35f, descY + h * 0.2f, cx - w * 0.4f, descY + h * 0.2f, cx - w * 0.3f, descY - h * 0.2f);
                strokes.add(new GuideStroke(p2, cx + w * 0.35f, xTopY));
                break;
            }
            case "h": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.35f, ascY);
                p1.lineTo(cx - w * 0.35f, baseY);
                strokes.add(new GuideStroke(p1, cx - w * 0.35f, ascY));

                Path p2 = new Path();
                p2.moveTo(cx - w * 0.35f, xTopY + h * 0.35f);
                p2.cubicTo(cx - w * 0.15f, xTopY - h * 0.05f, cx + w * 0.38f, xTopY - h * 0.05f, cx + w * 0.38f, xTopY + h * 0.5f);
                p2.lineTo(cx + w * 0.38f, baseY - h * 0.1f);
                p2.quadTo(cx + w * 0.38f, baseY, cx + w * 0.52f, baseY);
                strokes.add(new GuideStroke(p2, cx - w * 0.35f, xTopY + h * 0.35f));
                break;
            }
            case "i": {
                Path p1 = new Path();
                p1.moveTo(cx, xTopY);
                p1.lineTo(cx, baseY - h * 0.18f);
                p1.quadTo(cx, baseY, cx + w * 0.25f, baseY);
                strokes.add(new GuideStroke(p1, cx, xTopY));

                Path p2 = new Path();
                p2.addCircle(cx, xTopY - u * 0.35f, 4f * getResources().getDisplayMetrics().density, Path.Direction.CW);
                strokes.add(new GuideStroke(p2, cx, xTopY - u * 0.35f));
                break;
            }
            case "k": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.35f, ascY);
                p1.lineTo(cx - w * 0.35f, baseY);
                strokes.add(new GuideStroke(p1, cx - w * 0.35f, ascY));

                Path p2 = new Path();
                p2.moveTo(cx + w * 0.35f, xTopY);
                p2.lineTo(cx - w * 0.35f, cy + h * 0.1f);
                p2.lineTo(cx + w * 0.40f, baseY);
                strokes.add(new GuideStroke(p2, cx + w * 0.35f, xTopY));
                break;
            }
            case "l": {
                Path p1 = new Path();
                float xL = cx - w * 0.20f; float yL = cy;
                p1.moveTo(xL, yL);
                p1.cubicTo(cx + w * 0.38f, ascY - h * 0.05f, cx + w * 0.05f, ascY - h * 0.05f, cx - w * 0.05f, xTopY + h * 0.5f);
                p1.lineTo(cx - w * 0.05f, baseY - h * 0.2f);
                p1.quadTo(cx - w * 0.05f, baseY, cx + w * 0.38f, baseY - h * 0.05f);
                strokes.add(new GuideStroke(p1, xL, yL));
                break;
            }
            case "m": {
                float wM = w * 1.35f;
                float xM = cx - wM * 0.40f;
                Path p1 = new Path(); p1.moveTo(xM, xTopY + h * 0.1f); p1.lineTo(xM, baseY);
                strokes.add(new GuideStroke(p1, xM, xTopY + h * 0.1f));

                Path p2 = new Path(); p2.moveTo(xM, xTopY + h * 0.35f);
                p2.cubicTo(cx - wM * 0.20f, xTopY - h * 0.05f, cx, xTopY - h * 0.05f, cx, xTopY + h * 0.5f);
                p2.lineTo(cx, baseY);
                strokes.add(new GuideStroke(p2, xM, xTopY + h * 0.35f));

                Path p3 = new Path(); p3.moveTo(cx, xTopY + h * 0.35f);
                p3.cubicTo(cx + wM * 0.20f, xTopY - h * 0.05f, cx + wM * 0.40f, xTopY - h * 0.05f, cx + wM * 0.40f, xTopY + h * 0.5f);
                p3.lineTo(cx + wM * 0.40f, baseY - h * 0.1f);
                p3.quadTo(cx + wM * 0.40f, baseY, cx + wM * 0.52f, baseY);
                strokes.add(new GuideStroke(p3, cx, xTopY + h * 0.35f));
                break;
            }
            case "n": {
                float xN = cx - w * 0.38f;
                Path p1 = new Path(); p1.moveTo(xN, xTopY + h * 0.1f); p1.lineTo(xN, baseY);
                strokes.add(new GuideStroke(p1, xN, xTopY + h * 0.1f));

                Path p2 = new Path(); p2.moveTo(xN, xTopY + h * 0.35f);
                p2.cubicTo(cx - w * 0.15f, xTopY - h * 0.05f, cx + w * 0.38f, xTopY - h * 0.05f, cx + w * 0.38f, xTopY + h * 0.5f);
                p2.lineTo(cx + w * 0.38f, baseY - h * 0.1f);
                p2.quadTo(cx + w * 0.38f, baseY, cx + w * 0.52f, baseY);
                strokes.add(new GuideStroke(p2, xN, xTopY + h * 0.35f));
                break;
            }
            case "o": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                break;
            }
            case "ô": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                strokes.add(new GuideStroke(hat1, cx - w * 0.3f, xTopY - u * 0.1f));
                strokes.add(new GuideStroke(hat2, cx, xTopY - u * 0.35f));
                break;
            }
            case "ơ": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                strokes.add(new GuideStroke(horn, cx + w * 0.35f, xTopY + h * 0.1f));
                break;
            }
            case "p": {
                Path p1 = new Path(); p1.moveTo(cx - w * 0.35f, xTopY); p1.lineTo(cx - w * 0.35f, descY);
                strokes.add(new GuideStroke(p1, cx - w * 0.35f, xTopY));

                Path p2 = new Path();
                p2.moveTo(cx - w * 0.35f, xTopY + h * 0.2f);
                p2.cubicTo(cx + w * 0.45f, xTopY - h * 0.05f, cx + w * 0.45f, baseY + h * 0.05f, cx - w * 0.35f, baseY);
                strokes.add(new GuideStroke(p2, cx - w * 0.35f, xTopY + h * 0.2f));
                break;
            }
            case "q": {
                strokes.add(new GuideStroke(ovalPath, xO, yO));
                Path p2 = new Path(); p2.moveTo(cx + w * 0.35f, xTopY); p2.lineTo(cx + w * 0.35f, descY);
                strokes.add(new GuideStroke(p2, cx + w * 0.35f, xTopY));
                break;
            }
            case "r": {
                Path p1 = new Path();
                float xR = cx - w * 0.35f; float yR = xTopY + h * 0.10f;
                p1.moveTo(xR, yR);
                p1.lineTo(xR, baseY);
                p1.moveTo(xR, xTopY + h * 0.35f);
                p1.cubicTo(cx - w * 0.10f, xTopY - h * 0.05f, cx + w * 0.38f, xTopY - h * 0.05f, cx + w * 0.48f, xTopY + h * 0.2f);
                strokes.add(new GuideStroke(p1, xR, yR));
                break;
            }
            case "s": {
                Path p1 = new Path();
                float xS = cx + w * 0.38f; float yS = xTopY + h * 0.18f;
                p1.moveTo(xS, yS);
                p1.cubicTo(cx - w * 0.42f, xTopY - h * 0.08f, cx - w * 0.32f, cy - h * 0.05f, cx, cy);
                p1.cubicTo(cx + w * 0.42f, cy + h * 0.05f, cx + w * 0.42f, baseY + h * 0.08f, cx - w * 0.38f, baseY - h * 0.1f);
                strokes.add(new GuideStroke(p1, xS, yS));
                break;
            }
            case "t": {
                Path p1 = new Path();
                p1.moveTo(cx, ascY + u * 0.4f);
                p1.lineTo(cx, baseY - h * 0.18f);
                p1.quadTo(cx, baseY, cx + w * 0.35f, baseY);
                strokes.add(new GuideStroke(p1, cx, ascY + u * 0.4f));

                Path p2 = new Path();
                p2.moveTo(cx - w * 0.25f, xTopY);
                p2.lineTo(cx + w * 0.35f, xTopY);
                strokes.add(new GuideStroke(p2, cx - w * 0.25f, xTopY));
                break;
            }
            case "u": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.38f, xTopY + h * 0.1f);
                p1.lineTo(cx - w * 0.38f, baseY - h * 0.25f);
                p1.cubicTo(cx - w * 0.38f, baseY + h * 0.05f, cx + w * 0.30f, baseY + h * 0.05f, cx + w * 0.30f, baseY - h * 0.25f);
                p1.lineTo(cx + w * 0.30f, xTopY + h * 0.1f);
                strokes.add(new GuideStroke(p1, cx - w * 0.38f, xTopY + h * 0.1f));

                Path p2 = new Path();
                p2.moveTo(cx + w * 0.30f, xTopY + h * 0.35f);
                p2.lineTo(cx + w * 0.30f, baseY - h * 0.10f);
                p2.quadTo(cx + w * 0.30f, baseY, cx + w * 0.48f, baseY);
                strokes.add(new GuideStroke(p2, cx + w * 0.30f, xTopY + h * 0.35f));
                break;
            }
            case "ư": {
                strokes.addAll(createVietnameseLowercaseLetterStrokes("u", width, height));
                strokes.add(new GuideStroke(horn, cx + w * 0.35f, xTopY + h * 0.1f));
                break;
            }
            case "v": {
                Path p1 = new Path();
                float xV = cx - w * 0.45f; float yV = xTopY + h * 0.10f;
                p1.moveTo(xV, yV);
                p1.lineTo(cx - w * 0.05f, baseY - h * 0.05f);
                p1.quadTo(cx, baseY, cx + w * 0.1f, baseY - h * 0.05f);
                p1.lineTo(cx + w * 0.45f, xTopY + h * 0.10f);
                p1.quadTo(cx + w * 0.55f, xTopY, cx + w * 0.6f, xTopY + h * 0.05f);
                strokes.add(new GuideStroke(p1, xV, yV));
                break;
            }
            case "x": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.1f, xTopY + h * 0.2f);
                p1.cubicTo(cx - w * 0.5f, xTopY - h * 0.05f, cx - w * 0.5f, baseY + h * 0.05f, cx - w * 0.1f, baseY - h * 0.18f);
                strokes.add(new GuideStroke(p1, cx - w * 0.1f, xTopY + h * 0.2f));

                Path p2 = new Path();
                p2.moveTo(cx + w * 0.1f, baseY - h * 0.18f);
                p2.cubicTo(cx + w * 0.5f, baseY + h * 0.05f, cx + w * 0.5f, xTopY - h * 0.05f, cx + w * 0.1f, xTopY + h * 0.2f);
                strokes.add(new GuideStroke(p2, cx + w * 0.1f, baseY - h * 0.18f));
                break;
            }
            case "y": {
                Path p1 = new Path();
                p1.moveTo(cx - w * 0.38f, xTopY + h * 0.1f);
                p1.lineTo(cx - w * 0.38f, baseY - h * 0.25f);
                p1.cubicTo(cx - w * 0.38f, baseY + h * 0.05f, cx + w * 0.30f, baseY + h * 0.05f, cx + w * 0.30f, baseY - h * 0.25f);
                p1.lineTo(cx + w * 0.30f, xTopY + h * 0.1f);
                strokes.add(new GuideStroke(p1, cx - w * 0.38f, xTopY + h * 0.1f));

                Path p2 = new Path();
                p2.moveTo(cx + w * 0.30f, xTopY + h * 0.35f);
                p2.lineTo(cx + w * 0.30f, descY - h * 0.3f);
                p2.cubicTo(cx + w * 0.30f, descY + h * 0.2f, cx - w * 0.45f, descY + h * 0.2f, cx - w * 0.35f, descY - h * 0.2f);
                strokes.add(new GuideStroke(p2, cx + w * 0.30f, xTopY + h * 0.35f));
                break;
            }
        }
        return strokes;
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
                    userStrokes.add(new UserStroke(currentPath, currentPaint));
                    currentPath = null;
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
        this.guideText = text != null ? text.toLowerCase() : "a";
        clearCanvas();
    }

    public void setStrokeColor(int color) {
        this.currentColor = color;
    }

    public void undoLastStroke() {
        if (!userStrokes.isEmpty()) {
            userStrokes.remove(userStrokes.size() - 1);
            invalidate();
        }
    }

    public void clearCanvas() {
        userStrokes.clear();
        currentPath = null;
        invalidate();
    }

    public boolean isEmpty() {
        return userStrokes.isEmpty() && currentPath == null;
    }

    /**
     * Tính toán phần trăm diện tích chữ mẫu được trẻ tô kín (Bitmap 128x128 trong bộ nhớ)
     */
    public float calculateCoveragePercentage() {
        if (isEmpty() || guideText == null || guideText.isEmpty() || getWidth() == 0 || getHeight() == 0) {
            return 0f;
        }

        int size = 128;
        Bitmap guideBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas guideCanvas = new Canvas(guideBitmap);

        Bitmap userBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas userCanvas = new Canvas(userBitmap);

        float scaleX = (float) size / getWidth();
        float scaleY = (float) size / getHeight();

        // 1. Dựng tất cả nét mẫu của chữ trong Bitmap nhỏ
        List<GuideStroke> guideStrokes = createVietnameseLowercaseLetterStrokes(guideText, size, size);

        Paint gPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        gPaint.setColor(Color.BLACK);
        gPaint.setStyle(Paint.Style.STROKE);
        gPaint.setStrokeWidth(guideThickPaint.getStrokeWidth() * scaleY);
        gPaint.setStrokeCap(Paint.Cap.ROUND);
        gPaint.setStrokeJoin(Paint.Join.ROUND);

        for (GuideStroke stroke : guideStrokes) {
            guideCanvas.drawPath(stroke.path, gPaint);
        }

        // 2. Dựng tất cả nét vẽ của trẻ em trong Bitmap nhỏ
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);

        for (UserStroke stroke : userStrokes) {
            Path scaledPath = new Path(stroke.path);
            scaledPath.transform(matrix);

            Paint sPaint = new Paint(stroke.paint);
            sPaint.setStrokeWidth(stroke.paint.getStrokeWidth() * scaleY);
            userCanvas.drawPath(scaledPath, sPaint);
        }

        if (currentPath != null) {
            Path scaledPath = new Path(currentPath);
            scaledPath.transform(matrix);

            Paint sPaint = new Paint(currentPaint);
            sPaint.setStrokeWidth(currentPaint.getStrokeWidth() * scaleY);
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
