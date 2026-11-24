package com.example.happykidlearn.member4.funmath;

import androidx.annotation.DrawableRes;

public class FunMathQuestion {
    private final int num1;
    private final int num2;
    private final String operator; // "+" hoặc "-"
    private final String itemName;
    private final int iconResId;
    private final int[] options;

    public FunMathQuestion(int num1, int num2, String operator, String itemName, @DrawableRes int iconResId, int[] options) {
        this.num1 = num1;
        this.num2 = num2;
        this.operator = operator;
        this.itemName = itemName;
        this.iconResId = iconResId;
        this.options = options;
    }

    public int getNum1() {
        return num1;
    }

    public int getNum2() {
        return num2;
    }

    public String getOperator() {
        return operator;
    }

    public String getItemName() {
        return itemName;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int[] getOptions() {
        return options;
    }

    public int getCorrectAnswer() {
        if ("-".equals(operator)) {
            return num1 - num2;
        }
        return num1 + num2;
    }
}
