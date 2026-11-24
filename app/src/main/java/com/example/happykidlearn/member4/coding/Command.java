package com.example.happykidlearn.member4.coding;

import androidx.annotation.DrawableRes;

import com.example.happykidlearn.R;

public enum Command {
    UP("Tiến", R.drawable.m4_ic_coding_arrow_up),
    DOWN("Lùi", R.drawable.m4_ic_coding_arrow_down),
    LEFT("Trái", R.drawable.m4_ic_coding_arrow_left),
    RIGHT("Phải", R.drawable.m4_ic_coding_arrow_right);

    private final String label;
    private final int iconResId;

    Command(String label, @DrawableRes int iconResId) {
        this.label = label;
        this.iconResId = iconResId;
    }

    public String getLabel() {
        return label;
    }

    public int getIconResId() {
        return iconResId;
    }
}
