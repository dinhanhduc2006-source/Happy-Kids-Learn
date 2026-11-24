package com.example.happykidlearn.member4.coding;

import java.util.List;

public class CodingMapLevel {
    private final String title;
    private final int rows;
    private final int cols;
    private final int startRow;
    private final int startCol;
    private final int targetRow;
    private final int targetCol;
    private final List<int[]> obstacles;

    public CodingMapLevel(String title, int rows, int cols, int startRow, int startCol, int targetRow, int targetCol, List<int[]> obstacles) {
        this.title = title;
        this.rows = rows;
        this.cols = cols;
        this.startRow = startRow;
        this.startCol = startCol;
        this.targetRow = targetRow;
        this.targetCol = targetCol;
        this.obstacles = obstacles;
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public int getStartRow() {
        return startRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public boolean isObstacle(int r, int c) {
        if (obstacles == null) return false;
        for (int[] obs : obstacles) {
            if (obs[0] == r && obs[1] == c) {
                return true;
            }
        }
        return false;
    }
}
