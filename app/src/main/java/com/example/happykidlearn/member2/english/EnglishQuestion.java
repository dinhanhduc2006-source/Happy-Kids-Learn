package com.example.happykidlearn.member2.english;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnglishQuestion {

    private final int questionIndex;
    private final EnglishContent.VocabItem targetItem;
    private final List<EnglishContent.VocabItem> options;
    private final int correctOptionIndex;

    public EnglishQuestion(
            int questionIndex,
            @NonNull EnglishContent.VocabItem targetItem,
            @NonNull List<EnglishContent.VocabItem> options,
            int correctOptionIndex
    ) {
        this.questionIndex = questionIndex;
        this.targetItem = targetItem;
        this.options = Collections.unmodifiableList(new ArrayList<>(options));
        this.correctOptionIndex = correctOptionIndex;
    }

    public int getQuestionIndex() {
        return questionIndex;
    }

    @NonNull
    public EnglishContent.VocabItem getTargetItem() {
        return targetItem;
    }

    @NonNull
    public List<EnglishContent.VocabItem> getOptions() {
        return options;
    }

    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }
}

