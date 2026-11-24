package com.example.happykidlearn.member4.counting;

import androidx.annotation.DrawableRes;

public class CountingQuestion {
    private final String itemName;
    private final int imageResId;
    private final int count;
    private final int[] options;

    public CountingQuestion(String itemName, @DrawableRes int imageResId, int count, int[] options) {
        this.itemName = itemName;
        this.imageResId = imageResId;
        this.count = count;
        this.options = options;
    }

    public String getItemName() {
        return itemName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getCount() {
        return count;
    }

    public int[] getOptions() {
        return options;
    }
}
