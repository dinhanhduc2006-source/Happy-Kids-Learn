package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.happykidlearn.member2.dashboard.Member2GameRouter;
import com.example.happykidlearn.member2.storybook.NarrationController;
import com.example.happykidlearn.member2.storybook.StoryContent;
import com.example.happykidlearn.member2.storybook.StoryPage;
import com.example.happykidlearn.member2.storybook.StorybookActivity;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;

import org.junit.Test;

import java.util.List;

public class StoryContentTest {

    @Test
    public void storyContent_has3OriginalVietnameseStoriesWith4To6PagesEach() {
        List<StoryContent.StoryBook> stories = StoryContent.getAllStories();
        assertNotNull(stories);
        assertEquals(3, stories.size());

        for (StoryContent.StoryBook story : stories) {
            assertFalse(story.getId().trim().isEmpty());
            assertFalse(story.getTitle().trim().isEmpty());
            assertFalse(story.getShortDescription().trim().isEmpty());
            assertFalse(story.getMoralLesson().trim().isEmpty());
            assertFalse(story.getMiniQuestion().trim().isEmpty());
            assertFalse(story.getCorrectAnswer().trim().isEmpty());
            assertFalse(story.getWrongAnswer().trim().isEmpty());
            assertNotEquals(0, story.getCoverIllustrationResId());

            int pageCount = story.getPages().size();
            assertTrue(
                    "Mỗi truyện phải có từ 4 đến 6 trang (thực tế: " + pageCount + ")",
                    pageCount >= 4 && pageCount <= 6
            );

            for (int i = 0; i < pageCount; i++) {
                StoryPage page = story.getPages().get(i);
                assertEquals(i, page.getPageIndex());
                assertFalse(page.getPageText().trim().isEmpty());
                assertFalse(page.getSceneCaption().trim().isEmpty());
                assertNotEquals(0, page.getIllustrationResId());
            }
        }
    }

    @Test
    public void narrationController_splitsStoryPagesIntoAccurateSentenceSpans() {
        for (StoryContent.StoryBook story : StoryContent.getAllStories()) {
            for (StoryPage page : story.getPages()) {
                String text = page.getPageText();
                List<NarrationController.SentenceSegment> segments =
                        NarrationController.splitIntoSentences(text);

                assertTrue("Mỗi trang truyện phải có ít nhất 2 câu ngắn", segments.size() >= 2);
                for (NarrationController.SentenceSegment seg : segments) {
                    assertTrue(seg.getStartCharIndex() >= 0);
                    assertTrue(seg.getEndCharIndex() <= text.length());
                    assertTrue(seg.getEndCharIndex() > seg.getStartCharIndex());
                    String extracted = text.substring(seg.getStartCharIndex(), seg.getEndCharIndex()).trim();
                    assertEquals(seg.getSentenceText(), extracted);
                }
            }
        }
    }

    @Test
    public void router_resolvesStorybookGameToStorybookActivity() {
        GameModel storybookGame = GameRegistry.getInstance().getGameById("storybook");
        assertNotNull(storybookGame);
        assertEquals(StorybookActivity.class, Member2GameRouter.resolveTargetActivity(storybookGame));
    }
}

