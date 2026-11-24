package com.example.happykidlearn.member4.coding;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.happykidlearn.R;
import com.example.happykidlearn.base.BaseGameActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CodingActivity extends BaseGameActivity {

    private TextView tvQuestionTitle;
    private GridLayout glMapMatrix;
    private LinearLayout llCommandQueue;
    private Button btnCmdUp, btnCmdDown, btnCmdLeft, btnCmdRight;
    private Button btnClearCommands, btnRunCoding;

    private List<CodingMapLevel> levels;
    private int currentLevel = 0;

    private List<Command> commandQueue;
    private boolean isExecuting = false;
    private int totalStarsEarned = 0;

    private int currentRobotRow;
    private int currentRobotCol;
    private FrameLayout[][] gridCellViews;

    private final Handler executionHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.m4_activity_coding);

        // Khởi tạo Header từ BaseGameActivity
        setupHeader("Lập Trình Robot");

        initViews();
        initLevelData();
        loadLevel(currentLevel);
    }

    private void initViews() {
        tvQuestionTitle = findViewById(R.id.tv_question_title);
        glMapMatrix = findViewById(R.id.gl_map_matrix);
        llCommandQueue = findViewById(R.id.ll_command_queue);

        btnCmdUp = findViewById(R.id.btn_cmd_up);
        btnCmdDown = findViewById(R.id.btn_cmd_down);
        btnCmdLeft = findViewById(R.id.btn_cmd_left);
        btnCmdRight = findViewById(R.id.btn_cmd_right);

        btnClearCommands = findViewById(R.id.btn_clear_commands);
        btnRunCoding = findViewById(R.id.btn_run_coding);

        btnCmdUp.setOnClickListener(v -> addCommand(Command.UP));
        btnCmdDown.setOnClickListener(v -> addCommand(Command.DOWN));
        btnCmdLeft.setOnClickListener(v -> addCommand(Command.LEFT));
        btnCmdRight.setOnClickListener(v -> addCommand(Command.RIGHT));

        btnClearCommands.setOnClickListener(v -> clearCommands());
        btnRunCoding.setOnClickListener(v -> runCodingExecution());
    }

    private void initLevelData() {
        levels = new ArrayList<>();

        // Màn 1: Ma trận 4x4 (Start: 3,0 | Target: 0,3 | Obstacles: 2,1 & 1,2)
        levels.add(new CodingMapLevel(
                "🤖 Màn 1: Lập trình Robot đến Cờ Đích!",
                4, 4,
                3, 0,
                0, 3,
                Arrays.asList(new int[]{2, 1}, new int[]{1, 2})
        ));

        // Màn 2: Mê cung Chướng ngại vật 4x4
        levels.add(new CodingMapLevel(
                "🤖 Màn 2: Mê cung chướng ngại vật đá!",
                4, 4,
                3, 0,
                0, 2,
                Arrays.asList(new int[]{3, 1}, new int[]{1, 1}, new int[]{2, 2})
        ));

        // Màn 3: Thử thách Bàn cờ 5x5
        levels.add(new CodingMapLevel(
                "🤖 Màn 3: Thử thách bàn cờ lớn 5x5!",
                5, 5,
                4, 0,
                0, 4,
                Arrays.asList(new int[]{3, 1}, new int[]{2, 2}, new int[]{1, 3}, new int[]{3, 3})
        ));
    }

    private void loadLevel(int levelIndex) {
        if (levelIndex < 0 || levelIndex >= levels.size()) {
            finishGame();
            return;
        }

        CodingMapLevel level = levels.get(levelIndex);
        currentRobotRow = level.getStartRow();
        currentRobotCol = level.getStartCol();

        commandQueue = new ArrayList<>();
        llCommandQueue.removeAllViews();

        // Cập nhật thanh tiến độ header (1/3, 2/3...)
        setGameProgress(levelIndex + 1, levels.size());

        tvQuestionTitle.setText(level.getTitle());

        // Render ma trận bàn cờ
        int rows = level.getRows();
        int cols = level.getCols();
        glMapMatrix.removeAllViews();
        glMapMatrix.setColumnCount(cols);

        gridCellViews = new FrameLayout[rows][cols];

        int cellSize = (int) ((rows == 5 ? 54 : 64) * getResources().getDisplayMetrics().density);
        int margin = (int) (3 * getResources().getDisplayMetrics().density);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                FrameLayout cellView = new FrameLayout(this);

                // Cấu hình nền cho ô ma trận
                if (r == level.getStartRow() && c == level.getStartCol()) {
                    cellView.setBackgroundResource(R.drawable.m4_bg_coding_start_cell);
                } else if (r == level.getTargetRow() && c == level.getTargetCol()) {
                    cellView.setBackgroundResource(R.drawable.m4_bg_coding_target_cell);
                } else if (level.isObstacle(r, c)) {
                    cellView.setBackgroundResource(R.drawable.m4_bg_coding_obstacle_cell);
                } else {
                    cellView.setBackgroundResource(R.drawable.m4_bg_coding_grid_cell);
                }

                // Cấu hình biểu tượng vật thể bên trong ô
                ImageView iconImg = new ImageView(this);
                int iconSize = (int) (36 * getResources().getDisplayMetrics().density);
                FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(iconSize, iconSize);
                iconParams.gravity = android.view.Gravity.CENTER;
                iconImg.setLayoutParams(iconParams);

                if (r == level.getStartRow() && c == level.getStartCol()) {
                    iconImg.setImageResource(R.drawable.m4_ic_coding_robot);
                    iconImg.setTag("ROBOT");
                } else if (r == level.getTargetRow() && c == level.getTargetCol()) {
                    iconImg.setImageResource(R.drawable.m4_ic_coding_flag);
                } else if (level.isObstacle(r, c)) {
                    iconImg.setImageResource(R.drawable.m4_ic_coding_rock);
                } else {
                    iconImg.setImageDrawable(null);
                }

                cellView.addView(iconImg);

                GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
                gridParams.width = cellSize;
                gridParams.height = cellSize;
                gridParams.setMargins(margin, margin, margin, margin);
                cellView.setLayoutParams(gridParams);

                gridCellViews[r][c] = cellView;
                glMapMatrix.addView(cellView);
            }
        }

        enableControls(true);
    }

    private void addCommand(Command cmd) {
        if (isExecuting) return;

        if (commandQueue.size() >= 12) {
            Toast.makeText(this, "💡 Hàng đợi đã đầy (tối đa 12 khối lệnh)!", Toast.LENGTH_SHORT).show();
            return;
        }

        commandQueue.add(cmd);

        // Render thẻ mũi tên vào hàng đợi lệnh
        LayoutInflater inflater = LayoutInflater.from(this);
        View tileView = inflater.inflate(R.layout.m4_item_command_tile, llCommandQueue, false);
        ImageView imgIcon = tileView.findViewById(R.id.img_command_icon);
        imgIcon.setImageResource(cmd.getIconResId());

        llCommandQueue.addView(tileView);

        // Tự động cuộn sang phải
        View parentScroll = (View) llCommandQueue.getParent();
        if (parentScroll != null) {
            parentScroll.post(() -> parentScroll.scrollTo(llCommandQueue.getWidth(), 0));
        }

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }
    }

    private void clearCommands() {
        if (isExecuting) return;

        commandQueue.clear();
        llCommandQueue.removeAllViews();
        resetRobotToStart();

        Toast.makeText(this, "🧹 Đã xóa sạch các khối lệnh!", Toast.LENGTH_SHORT).show();
    }

    private void runCodingExecution() {
        if (isExecuting) return;

        if (commandQueue.isEmpty()) {
            Toast.makeText(this, "💡 Bé hãy chọn ít nhất 1 khối lệnh mũi tên trước nhé!", Toast.LENGTH_SHORT).show();
            return;
        }

        isExecuting = true;
        enableControls(false);
        resetRobotToStart();

        // Chạy từng khối lệnh theo chu kỳ Handler 600ms
        executeCommandStep(0);
    }

    private void executeCommandStep(int stepIndex) {
        if (stepIndex >= commandQueue.size()) {
            // Đã chạy hết toàn bộ khối lệnh nhưng chưa tới đích
            CodingMapLevel level = levels.get(currentLevel);
            if (currentRobotRow != level.getTargetRow() || currentRobotCol != level.getTargetCol()) {
                Toast.makeText(this, "💡 Robot chưa tới được cờ đích. Bé hãy chọn thêm lệnh nhé!", Toast.LENGTH_LONG).show();
                resetRobotToStart();
                isExecuting = false;
                enableControls(true);
            }
            return;
        }

        Command cmd = commandQueue.get(stepIndex);
        CodingMapLevel level = levels.get(currentLevel);

        int nextRow = currentRobotRow;
        int nextCol = currentRobotCol;

        switch (cmd) {
            case UP:
                nextRow--;
                break;
            case DOWN:
                nextRow++;
                break;
            case LEFT:
                nextCol--;
                break;
            case RIGHT:
                nextCol++;
                break;
        }

        // 1. Kiểm tra ra ngoài bản đồ
        if (nextRow < 0 || nextRow >= level.getRows() || nextCol < 0 || nextCol >= level.getCols()) {
            Toast.makeText(this, "⚠️ Robot đi chệch khỏi đường ray! Bé hãy lập trình lại nhé!", Toast.LENGTH_LONG).show();
            shakeMatrix();
            resetRobotToStart();
            isExecuting = false;
            enableControls(true);
            return;
        }

        // 2. Kiểm tra đụng chướng ngại vật
        if (level.isObstacle(nextRow, nextCol)) {
            Toast.makeText(this, "💥 Robot va phải chướng ngại vật đá! Bé hãy thử lại nhé!", Toast.LENGTH_LONG).show();
            shakeMatrix();
            resetRobotToStart();
            isExecuting = false;
            enableControls(true);
            return;
        }

        // 3. Di chuyển bước hợp lệ
        moveRobotUI(currentRobotRow, currentRobotCol, nextRow, nextCol);
        currentRobotRow = nextRow;
        currentRobotCol = nextCol;

        if (soundManager != null) {
            soundManager.playSfx(R.raw.m2_sfx_celebrate);
        }

        // 4. Kiểm tra có chạm Đích thành công hay không
        if (currentRobotRow == level.getTargetRow() && currentRobotCol == level.getTargetCol()) {
            totalStarsEarned++;
            saveGameProgress("coding", currentLevel + 1, levels.size(), totalStarsEarned);
            Toast.makeText(this, "🎉 HOAN HÔ! Robot đã về đích thành công!", Toast.LENGTH_SHORT).show();

            if (soundManager != null) {
                soundManager.playSfx(R.raw.m2_sfx_celebrate);
            }

            executionHandler.postDelayed(() -> {
                isExecuting = false;
                if (currentLevel < levels.size() - 1) {
                    currentLevel++;
                    loadLevel(currentLevel);
                } else {
                    finishGame();
                }
            }, 1000);
            return;
        }

        // Lên lịch thực hiện bước tiếp theo sau 600ms
        final int nextStep = stepIndex + 1;
        executionHandler.postDelayed(() -> executeCommandStep(nextStep), 600);
    }

    private void moveRobotUI(int fromR, int fromC, int toR, int toC) {
        FrameLayout oldCell = gridCellViews[fromR][fromC];
        FrameLayout newCell = gridCellViews[toR][toC];

        ImageView robotImg = null;
        for (int i = 0; i < oldCell.getChildCount(); i++) {
            View child = oldCell.getChildAt(i);
            if ("ROBOT".equals(child.getTag())) {
                robotImg = (ImageView) child;
                break;
            }
        }

        if (robotImg != null) {
            oldCell.removeView(robotImg);
            newCell.addView(robotImg);

            ObjectAnimator anim = ObjectAnimator.ofFloat(robotImg, "scaleX", 0.5f, 1.2f, 1.0f);
            anim.setDuration(250);
            anim.start();
        }
    }

    private void resetRobotToStart() {
        CodingMapLevel level = levels.get(currentLevel);

        // Xóa hình Robot ở ô hiện tại
        if (gridCellViews != null) {
            for (int r = 0; r < level.getRows(); r++) {
                for (int c = 0; c < level.getCols(); c++) {
                    FrameLayout cell = gridCellViews[r][c];
                    for (int i = cell.getChildCount() - 1; i >= 0; i--) {
                        View child = cell.getChildAt(i);
                        if ("ROBOT".equals(child.getTag())) {
                            cell.removeViewAt(i);
                        }
                    }
                }
            }
            // Thêm lại Robot ở ô xuất phát
            currentRobotRow = level.getStartRow();
            currentRobotCol = level.getStartCol();
            FrameLayout startCell = gridCellViews[currentRobotRow][currentRobotCol];

            ImageView robotImg = new ImageView(this);
            robotImg.setImageResource(R.drawable.m4_ic_coding_robot);
            robotImg.setTag("ROBOT");
            int iconSize = (int) (36 * getResources().getDisplayMetrics().density);
            FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(iconSize, iconSize);
            iconParams.gravity = android.view.Gravity.CENTER;
            robotImg.setLayoutParams(iconParams);

            startCell.addView(robotImg);
        }
    }

    private void shakeMatrix() {
        ObjectAnimator shake = ObjectAnimator.ofFloat(glMapMatrix, View.TRANSLATION_X, 0, 15, -15, 10, -10, 0);
        shake.setDuration(400);
        shake.start();
    }

    private void enableControls(boolean enable) {
        btnCmdUp.setEnabled(enable);
        btnCmdDown.setEnabled(enable);
        btnCmdLeft.setEnabled(enable);
        btnCmdRight.setEnabled(enable);
        btnClearCommands.setEnabled(enable);
        btnRunCoding.setEnabled(enable);
    }

    private void finishGame() {
        int finalStars = totalStarsEarned >= 3 ? 3 : (totalStarsEarned >= 2 ? 2 : 1);
        saveGameProgress("coding", levels.size(), levels.size(), finalStars);
        showVictoryDialog(finalStars,
                this::finish,
                () -> {
                    currentLevel = 0;
                    totalStarsEarned = 0;
                    loadLevel(currentLevel);
                }
        );
    }

    @Override
    protected void onDestroy() {
        executionHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
