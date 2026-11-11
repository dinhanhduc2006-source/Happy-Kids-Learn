package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.dashboard.Member2GameRouter;
import com.example.happykidlearn.member2.spelling.SpellingActivity;
import com.example.happykidlearn.member2.spelling.SpellingEngine;
import com.example.happykidlearn.member2.spelling.SpellingExercise;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;

import org.junit.Test;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SpellingEngineTest {

    @Test
    public void spellingEngine_hasAtLeast15ValidVietnameseExercises() {
        List<SpellingExercise> exercises = SpellingEngine.getAllExercises();
        assertNotNull(exercises);
        assertTrue("Phải có ít nhất 15 bài ghép vần", exercises.size() >= 15);
        assertEquals(16, exercises.size());

        for (SpellingExercise ex : exercises) {
            assertNotNull(ex.getId());
            assertFalse(ex.getTargetWord().isEmpty());
            assertTrue(ex.getSlotCount() >= 2);
            assertFalse(ex.getDistractorTokens().isEmpty());
            assertNotEquals(0, ex.getIllustrationResId());

            assertTrue(
                    "Đáp án chuẩn của bài " + ex.getId() + " phải được chấm ĐÚNG",
                    SpellingEngine.isAnswerCorrect(ex, ex.getTargetTokens())
            );

            List<String> pool = SpellingEngine.buildTilePool(ex, 42L);
            assertEquals(ex.getTargetTokens().size() + ex.getDistractorTokens().size(), pool.size());
            assertTrue(pool.containsAll(ex.getTargetTokens()));
        }
    }

    @Test
    public void spellingEngine_distinguishesVietnameseDiacriticsAndNfdUnicode() {
        List<SpellingExercise> exercises = SpellingEngine.getAllExercises();
        SpellingExercise beExercise = exercises.get(1); // "bé" = ["b", "é"]
        assertEquals("bé", beExercise.getTargetWord());

        // Sai dấu thanh ("b" + "e" thay vì "b" + "é") phải trả về FALSE
        assertFalse(SpellingEngine.isAnswerCorrect(beExercise, Arrays.asList("b", "e")));

        // Chuỗi ở dạng tổ hợp NFD ("e\u0301") vẫn phải được chuẩn hóa NFC và chấm ĐÚNG
        String nfdEAcute = Normalizer.normalize("é", Normalizer.Form.NFD);
        assertTrue(SpellingEngine.isAnswerCorrect(beExercise, Arrays.asList("b", nfdEAcute)));

        // Chưa điền đủ ô phải trả về FALSE
        assertFalse(SpellingEngine.isAnswerCorrect(beExercise, Arrays.asList("b", null)));
        assertFalse(SpellingEngine.isAnswerCorrect(beExercise, Collections.singletonList("b")));
    }

    @Test
    public void spellingEngine_findFirstSlotNeedingHelpWorksCorrectly() {
        SpellingExercise camExercise = SpellingEngine.getAllExercises().get(10); // "cam" = ["c", "a", "m"]
        assertEquals(0, SpellingEngine.findFirstSlotNeedingHelp(camExercise, Arrays.asList(null, null, null)));
        assertEquals(1, SpellingEngine.findFirstSlotNeedingHelp(camExercise, Arrays.asList("c", "o", null)));
        assertEquals(2, SpellingEngine.findFirstSlotNeedingHelp(camExercise, Arrays.asList("c", "a", null)));
        assertEquals(-1, SpellingEngine.findFirstSlotNeedingHelp(camExercise, Arrays.asList("c", "a", "m")));
    }

    @Test
    public void router_resolvesSpellingGameToSpellingActivity() {
        GameModel spellingGame = GameRegistry.getInstance().getGameById("spelling");
        assertNotNull(spellingGame);
        assertEquals(SpellingActivity.class, Member2GameRouter.resolveTargetActivity(spellingGame));
    }
}

