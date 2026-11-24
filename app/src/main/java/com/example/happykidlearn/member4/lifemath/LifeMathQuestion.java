package com.example.happykidlearn.member4.lifemath;

import androidx.annotation.DrawableRes;

public class LifeMathQuestion {
    private final String instruction;
    private final String itemName;
    private final int targetQuantity;
    private final int iconResId;
    private final int shelfQuantity;

    public LifeMathQuestion(String instruction, String itemName, int targetQuantity, @DrawableRes int iconResId, int shelfQuantity) {
        this.instruction = instruction;
        this.itemName = itemName;
        this.targetQuantity = targetQuantity;
        this.iconResId = iconResId;
        this.shelfQuantity = shelfQuantity;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getItemName() {
        return itemName;
    }

    public int getTargetQuantity() {
        return targetQuantity;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getShelfQuantity() {
        return shelfQuantity;
    }
}
