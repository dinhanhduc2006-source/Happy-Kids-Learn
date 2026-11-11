package com.example.happykidlearn.member2.storybook;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

public final class StoryPage {

    private final int pageIndex;
    private final String pageText;
    private final String sceneCaption;
    @DrawableRes
    private final int illustrationResId;

    public StoryPage(
            int pageIndex,
            @NonNull String pageText,
            @NonNull String sceneCaption,
            @DrawableRes int illustrationResId
    ) {
        this.pageIndex = pageIndex;
        this.pageText = pageText;
        this.sceneCaption = sceneCaption;
        this.illustrationResId = illustrationResId;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    @NonNull
    public String getPageText() {
        return pageText;
    }

    @NonNull
    public String getSceneCaption() {
        return sceneCaption;
    }

    @DrawableRes
    public int getIllustrationResId() {
        return illustrationResId;
    }
}

