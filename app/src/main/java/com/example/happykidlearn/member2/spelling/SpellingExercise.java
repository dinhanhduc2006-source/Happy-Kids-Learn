package com.example.happykidlearn.member2.spelling;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class SpellingExercise {

    private final String id;
    private final String targetWord;
    private final List<String> targetTokens;
    private final List<String> distractorTokens;
    private final String contextPhrase;
    private final String hintText;
    @DrawableRes
    private final int illustrationResId;

    public SpellingExercise(
            @NonNull String id,
            @NonNull String targetWord,
            @NonNull List<String> targetTokens,
            @NonNull List<String> distractorTokens,
            @NonNull String contextPhrase,
            @NonNull String hintText,
            @DrawableRes int illustrationResId
    ) {
        this.id = id;
        this.targetWord = targetWord;
        this.targetTokens = Collections.unmodifiableList(new ArrayList<>(targetTokens));
        this.distractorTokens = Collections.unmodifiableList(new ArrayList<>(distractorTokens));
        this.contextPhrase = contextPhrase;
        this.hintText = hintText;
        this.illustrationResId = illustrationResId;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getTargetWord() {
        return targetWord;
    }

    @NonNull
    public List<String> getTargetTokens() {
        return targetTokens;
    }

    @NonNull
    public List<String> getDistractorTokens() {
        return distractorTokens;
    }

    public int getSlotCount() {
        return targetTokens.size();
    }

    @NonNull
    public String getContextPhrase() {
        return contextPhrase;
    }

    @NonNull
    public String getHintText() {
        return hintText;
    }

    @DrawableRes
    public int getIllustrationResId() {
        return illustrationResId;
    }
}

