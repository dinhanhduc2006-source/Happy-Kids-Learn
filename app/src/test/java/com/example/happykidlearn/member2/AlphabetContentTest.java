package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.alphabet.AlphabetActivity;
import com.example.happykidlearn.member2.alphabet.AlphabetContent;
import com.example.happykidlearn.member2.dashboard.Member2GameRouter;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AlphabetContentTest {

    private static final String[] EXPECTED_UPPER = {
            "A", "Ă", "Â", "B", "C", "D", "Đ", "E", "Ê", "G",
            "H", "I", "K", "L", "M", "N", "O", "Ô", "Ơ", "P",
            "Q", "R", "S", "T", "U", "Ư", "V", "X", "Y"
    };

    private static final String[] EXPECTED_LOWER = {
            "a", "ă", "â", "b", "c", "d", "đ", "e", "ê", "g",
            "h", "i", "k", "l", "m", "n", "o", "ô", "ơ", "p",
            "q", "r", "s", "t", "u", "ư", "v", "x", "y"
    };

    @Test
    public void alphabetContent_hasExact29VietnameseLettersInCorrectOrder() {
        List<AlphabetContent.AlphabetCardItem> cards = AlphabetContent.getAllCards();
        assertNotNull(cards);
        assertEquals(29, cards.size());

        Set<String> forbiddenLetters = new HashSet<>(Arrays.asList("F", "J", "W", "Z", "f", "j", "w", "z"));

        for (int i = 0; i < EXPECTED_UPPER.length; i++) {
            AlphabetContent.AlphabetCardItem item = cards.get(i);
            assertEquals("Sai thứ tự chữ in hoa tại vị trí " + i, EXPECTED_UPPER[i], item.getUpperLetter());
            assertEquals("Sai thứ tự chữ thường tại vị trí " + i, EXPECTED_LOWER[i], item.getLowerLetter());
            assertFalse(forbiddenLetters.contains(item.getUpperLetter()));
            assertFalse(forbiddenLetters.contains(item.getLowerLetter()));
        }
    }

    @Test
    public void alphabetContent_everyExampleWordContainsTargetLetterAndValidIllustration() {
        List<AlphabetContent.AlphabetCardItem> cards = AlphabetContent.getAllCards();
        for (AlphabetContent.AlphabetCardItem item : cards) {
            assertNotNull(item.getExampleWord());
            assertFalse(item.getExampleWord().trim().isEmpty());
            assertNotNull(item.getDiscoverySentence());
            assertFalse(item.getDiscoverySentence().trim().isEmpty());
            assertNotEquals(0, item.getIllustrationResId());

            if ("A".equals(item.getUpperLetter())) {
                assertNotEquals("Không dùng ví dụ sai A - Con cá", "Con cá", item.getExampleWord());
            }

            int matchIndex = AlphabetContent.findTargetLetterIndex(
                    item.getExampleWord(),
                    item.getLowerLetter()
            );
            assertTrue(
                    "Từ ví dụ '" + item.getExampleWord() + "' phải chứa chữ '" + item.getLowerLetter() + "'",
                    matchIndex >= 0
            );
        }
    }

    @Test
    public void alphabetContent_buildListeningChoices_containsTargetAndThreeDistinctLetters() {
        List<AlphabetContent.AlphabetCardItem> allCards = AlphabetContent.getAllCards();
        for (int i = 0; i < allCards.size(); i++) {
            AlphabetContent.AlphabetCardItem target = allCards.get(i);
            List<AlphabetContent.AlphabetCardItem> choices = AlphabetContent.buildListeningChoices(i, 1000L + i);
            assertEquals(3, choices.size());

            Set<String> uniqueUpper = new HashSet<>();
            boolean foundTarget = false;
            for (AlphabetContent.AlphabetCardItem choice : choices) {
                uniqueUpper.add(choice.getUpperLetter());
                if (choice.getIndex() == target.getIndex()) {
                    foundTarget = true;
                }
            }
            assertEquals(3, uniqueUpper.size());
            assertTrue("Danh sách lựa chọn nghe phải chứa chữ mục tiêu " + target.getUpperLetter(), foundTarget);
        }
    }

    @Test
    public void router_resolvesAlphabetGameToAlphabetActivity() {
        GameModel alphabetGame = GameRegistry.getInstance().getGameById("alphabet");
        assertNotNull(alphabetGame);
        assertEquals(AlphabetActivity.class, Member2GameRouter.resolveTargetActivity(alphabetGame));
    }
}

