package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.dashboard.Member2GameRouter;
import com.example.happykidlearn.member2.english.EnglishActivity;
import com.example.happykidlearn.member2.english.EnglishContent;
import com.example.happykidlearn.member2.english.EnglishQuestion;
import com.example.happykidlearn.member2.english.EnglishQuizEngine;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnglishQuizEngineTest {

    @Test
    public void englishContent_has4TopicsWithAtLeast6ItemsEach() {
        List<EnglishContent.Topic> topics = EnglishContent.getAllTopics();
        assertNotNull(topics);
        assertEquals(4, topics.size());
        assertEquals(24, EnglishContent.getTotalVocabularyCount());

        for (EnglishContent.Topic topic : topics) {
            assertTrue("Mỗi chủ đề phải có ít nhất 6 từ vựng", topic.getItems().size() >= 6);
            assertFalse("Mỗi chủ đề phải có phụ đề thế giới nhỏ", topic.getWorldSubtitle().trim().isEmpty());
            for (EnglishContent.VocabItem item : topic.getItems()) {
                assertFalse(item.getEnglishWord().trim().isEmpty());
                assertFalse(item.getVietnameseMeaning().trim().isEmpty());
                assertFalse(item.getExampleSentence().trim().isEmpty());
                assertNotEquals(0, item.getIllustrationResId());
            }
        }
    }

    @Test
    public void englishQuizEngine_generates20QuestionsAcross4TopicsWithUniqueCorrectOption() {
        int totalGeneratedQuestions = 0;
        for (EnglishContent.Topic topic : EnglishContent.getAllTopics()) {
            List<EnglishQuestion> quiz = EnglishQuizEngine.generateQuizForTopic(topic.getId(), 12345L);
            assertEquals(5, quiz.size());
            totalGeneratedQuestions += quiz.size();

            for (EnglishQuestion q : quiz) {
                List<EnglishContent.VocabItem> options = q.getOptions();
                assertEquals(4, options.size());

                Set<String> distinctOptionIds = new HashSet<>();
                int matchingCorrectCount = 0;

                for (int i = 0; i < options.size(); i++) {
                    EnglishContent.VocabItem opt = options.get(i);
                    distinctOptionIds.add(opt.getId());
                    if (opt.getId().equals(q.getTargetItem().getId())) {
                        matchingCorrectCount++;
                        assertEquals(i, q.getCorrectOptionIndex());
                        assertTrue(EnglishQuizEngine.isAnswerCorrect(q, i));
                    } else {
                        assertFalse(EnglishQuizEngine.isAnswerCorrect(q, i));
                    }
                }

                assertEquals("4 đáp án trong 1 câu hỏi phải hoàn toàn khác nhau", 4, distinctOptionIds.size());
                assertEquals("Chỉ có duy nhất 1 đáp án đúng trong mỗi câu hỏi", 1, matchingCorrectCount);
            }
        }
        assertEquals("Tổng cộng 4 chủ đề phải có ít nhất 20 câu hỏi quiz", 20, totalGeneratedQuestions);
    }

    @Test
    public void englishQuizEngine_calculatesStarsConsistently() {
        assertEquals(3, EnglishQuizEngine.calculateStars(5, 5));
        assertEquals(2, EnglishQuizEngine.calculateStars(3, 5));
        assertEquals(1, EnglishQuizEngine.calculateStars(2, 5));
        assertEquals(1, EnglishQuizEngine.calculateStars(0, 5));
    }

    @Test
    public void router_resolvesEnglishGameToEnglishActivity() {
        GameModel englishGame = GameRegistry.getInstance().getGameById("english");
        assertNotNull(englishGame);
        assertEquals(EnglishActivity.class, Member2GameRouter.resolveTargetActivity(englishGame));
    }
}

