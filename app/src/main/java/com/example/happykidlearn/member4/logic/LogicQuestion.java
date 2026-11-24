package com.example.happykidlearn.member4.logic;

import androidx.annotation.DrawableRes;

public class LogicQuestion {

    public enum PuzzleType {
        SEQUENCE,    // Quy luật chuỗi hình
        SHADOW,      // Ghép bóng đen
        CLASSIFICATION // Phân loại đồ vật
    }

    private final String title;
    private final String promptText;
    private final PuzzleType type;
    private final int[] sequenceIconResIds;
    private final int shadowIconResId;
    private final int[] optionIconResIds;
    private final String[] optionTexts;
    private final int correctOptionIndex;

    public LogicQuestion(String title, String promptText, PuzzleType type,
                         int[] sequenceIconResIds, @DrawableRes int shadowIconResId,
                         int[] optionIconResIds, String[] optionTexts, int correctOptionIndex) {
        this.title = title;
        this.promptText = promptText;
        this.type = type;
        this.sequenceIconResIds = sequenceIconResIds;
        this.shadowIconResId = shadowIconResId;
        this.optionIconResIds = optionIconResIds;
        this.optionTexts = optionTexts;
        this.correctOptionIndex = correctOptionIndex;
    }

    public String getTitle() {
        return title;
    }

    public String getPromptText() {
        return promptText;
    }

    public PuzzleType getType() {
        return type;
    }

    public int[] getSequenceIconResIds() {
        return sequenceIconResIds;
    }

    public int getShadowIconResId() {
        return shadowIconResId;
    }

    public int[] getOptionIconResIds() {
        return optionIconResIds;
    }

    public String[] getOptionTexts() {
        return optionTexts;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}
