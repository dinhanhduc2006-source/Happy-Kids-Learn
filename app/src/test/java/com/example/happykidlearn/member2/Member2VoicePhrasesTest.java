package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.common.Member2VoicePhrases;

import org.junit.Test;

import java.util.List;
import java.util.Random;

public class Member2VoicePhrasesTest {

    @Test
    public void forCategory_returnsExactRequiredPhrasesCount() {
        List<String> correct = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.CORRECT);
        List<String> wrong = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.WRONG);
        List<String> complete = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.COMPLETE);

        assertEquals("Phải có đủ 4 câu khen đúng", 4, correct.size());
        assertEquals("Phải có đủ 4 câu động viên sai", 4, wrong.size());
        assertEquals("Phải có đủ 3 câu chúc mừng hoàn thành", 3, complete.size());
    }

    @Test
    public void phrases_containRequiredContentKeywords() {
        List<String> correct = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.CORRECT);
        assertTrue(correct.contains("Chính xác rồi! Bé giỏi lắm!"));
        assertTrue(correct.contains("Tuyệt vời! Bé làm đúng rồi!"));
        assertTrue(correct.contains("Xuất sắc! Tiếp tục nào!"));
        assertTrue(correct.contains("Hay quá! Bé thật thông minh!"));

        List<String> wrong = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.WRONG);
        assertTrue(wrong.contains("Chưa đúng rồi, mình thử lại nhé!"));
        assertTrue(wrong.contains("Không sao đâu, bé thử lần nữa nào!"));
        assertTrue(wrong.contains("Cố lên! Bé sắp làm được rồi!"));
        assertTrue(wrong.contains("Thử lại một lần nữa nhé!"));

        List<String> complete = Member2VoicePhrases.forCategory(Member2VoicePhrases.Category.COMPLETE);
        assertTrue(complete.contains("Chúc mừng! Bé đã hoàn thành bài học!"));
        assertTrue(complete.contains("Tuyệt vời! Bé đã vượt qua thử thách!"));
        assertTrue(complete.contains("Giỏi lắm! Cùng khám phá bài tiếp theo nhé!"));
    }

    @Test
    public void allPhrases_areNonEmptyAndChildFriendly() {
        for (Member2VoicePhrases.Category cat : Member2VoicePhrases.Category.values()) {
            List<String> phrases = Member2VoicePhrases.forCategory(cat);
            assertNotNull(phrases);
            assertFalse(phrases.isEmpty());
            for (String phrase : phrases) {
                assertNotNull(phrase);
                assertTrue("Câu nói không được để trống", phrase.trim().length() > 5);
                // Không được chứa từ ngữ chê bai tiêu cực
                assertFalse(phrase.toLowerCase().contains("kém"));
                assertFalse(phrase.toLowerCase().contains("dở"));
                assertFalse(phrase.toLowerCase().contains("sai bét"));
            }
        }
    }

    @Test
    public void pickIndex_neverRepeatsConsecutivelyWhenPoolLargerThanOne() {
        Random random = new Random(42L);
        int poolSize = 4;
        int last = -1;

        for (int i = 0; i < 50; i++) {
            int current = Member2VoicePhrases.pickIndex(poolSize, last, random);
            assertTrue("Chỉ số phải hợp lệ trong khoảng [0, poolSize)", current >= 0 && current < poolSize);
            if (last != -1) {
                assertNotEquals("Không được chọn trùng câu vừa phát liền trước", last, current);
            }
            last = current;
        }
    }

    @Test
    public void pickIndex_handlesPoolOfSizeOne() {
        Random random = new Random(123L);
        assertEquals(0, Member2VoicePhrases.pickIndex(1, 0, random));
        assertEquals(0, Member2VoicePhrases.pickIndex(1, -1, random));
    }

    @Test
    public void passesCooldown_enforcesIntervalWithinSameCategory() {
        long t0 = 10000L;
        // Lần đầu tiên phát: luôn cho phép
        assertTrue(Member2VoicePhrases.passesCooldown(
                Member2VoicePhrases.Category.CORRECT,
                t0,
                null,
                0L
        ));

        // Nhấp liên tục trong vòng 500ms cùng loại: bị từ chối chống spam
        assertFalse(Member2VoicePhrases.passesCooldown(
                Member2VoicePhrases.Category.CORRECT,
                t0 + 500L,
                Member2VoicePhrases.Category.CORRECT,
                t0
        ));

        // Nhấp sau khi hết 1500ms cooldown: cho phép
        assertTrue(Member2VoicePhrases.passesCooldown(
                Member2VoicePhrases.Category.CORRECT,
                t0 + 1501L,
                Member2VoicePhrases.Category.CORRECT,
                t0
        ));
    }

    @Test
    public void passesCooldown_allowsImmediateSwitchBetweenDifferentCategories() {
        long t0 = 10000L;
        // Trẻ vừa sai, 200ms sau thử lại và làm đúng ngay: cho phép khen mà không bị chặn bởi cooldown của câu sai
        assertTrue(Member2VoicePhrases.passesCooldown(
                Member2VoicePhrases.Category.CORRECT,
                t0 + 200L,
                Member2VoicePhrases.Category.WRONG,
                t0
        ));
    }
}

