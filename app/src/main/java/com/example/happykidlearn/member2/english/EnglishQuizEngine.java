package com.example.happykidlearn.member2.english;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class EnglishQuizEngine {

    public static final int QUESTIONS_PER_QUIZ = 5;
    public static final int OPTIONS_PER_QUESTION = 4;

    private EnglishQuizEngine() {
    }

    @NonNull
    public static List<EnglishQuestion> generateQuizForTopic(@NonNull String topicId, long seed) {
        EnglishContent.Topic topic = EnglishContent.getTopicById(topicId);
        if (topic == null || topic.getItems().size() < OPTIONS_PER_QUESTION) {
            return Collections.emptyList();
        }

        Random random = new Random(seed);
        List<EnglishContent.VocabItem> pool = new ArrayList<>(topic.getItems());
        Collections.shuffle(pool, random);

        int questionCount = Math.min(QUESTIONS_PER_QUIZ, pool.size());
        List<EnglishQuestion> questions = new ArrayList<>(questionCount);

        for (int q = 0; q < questionCount; q++) {
            EnglishContent.VocabItem target = pool.get(q);

            List<EnglishContent.VocabItem> distractors = new ArrayList<>();
            for (EnglishContent.VocabItem candidate : topic.getItems()) {
                if (!candidate.getId().equals(target.getId())) {
                    distractors.add(candidate);
                }
            }
            Collections.shuffle(distractors, random);

            List<EnglishContent.VocabItem> options = new ArrayList<>(OPTIONS_PER_QUESTION);
            options.add(target);
            for (int d = 0; d < OPTIONS_PER_QUESTION - 1 && d < distractors.size(); d++) {
                options.add(distractors.get(d));
            }
            Collections.shuffle(options, random);

            int correctIdx = 0;
            for (int i = 0; i < options.size(); i++) {
                if (options.get(i).getId().equals(target.getId())) {
                    correctIdx = i;
                    break;
                }
            }

            questions.add(new EnglishQuestion(q, target, options, correctIdx));
        }

        return Collections.unmodifiableList(questions);
    }

    public static boolean isAnswerCorrect(@NonNull EnglishQuestion question, int selectedOptionIndex) {
        return selectedOptionIndex >= 0
                && selectedOptionIndex < question.getOptions().size()
                && selectedOptionIndex == question.getCorrectOptionIndex();
    }

    public static int calculateStars(int correctAnswers, int totalQuestions) {
        if (totalQuestions <= 0) {
            return 1;
        }
        if (correctAnswers >= totalQuestions) {
            return 3;
        }
        if (correctAnswers >= Math.ceil(totalQuestions * 0.6)) {
            return 2;
        }
        return 1;
    }
}

