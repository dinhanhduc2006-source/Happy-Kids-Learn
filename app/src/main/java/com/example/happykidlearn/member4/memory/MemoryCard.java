package com.example.happykidlearn.member4.memory;

import androidx.annotation.DrawableRes;

public class MemoryCard {
    private final int id;
    private final int imageResId;
    private boolean isFaceUp;
    private boolean isMatched;

    public MemoryCard(int id, @DrawableRes int imageResId) {
        this.id = id;
        this.imageResId = imageResId;
        this.isFaceUp = false;
        this.isMatched = false;
    }

    public int getId() {
        return id;
    }

    public int getImageResId() {
        return imageResId;
    }

    public boolean isFaceUp() {
        return isFaceUp;
    }

    public void setFaceUp(boolean faceUp) {
        isFaceUp = faceUp;
    }

    public boolean isMatched() {
        return isMatched;
    }

    public void setMatched(boolean matched) {
        isMatched = matched;
    }
}
