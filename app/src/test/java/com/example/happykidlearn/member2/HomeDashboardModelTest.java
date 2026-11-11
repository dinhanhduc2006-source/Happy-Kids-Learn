package com.example.happykidlearn.member2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import android.app.Activity;

import com.example.happykidlearn.MainActivity;
import com.example.happykidlearn.R;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.dashboard.HomeGameUiModel;
import com.example.happykidlearn.member2.dashboard.Member2GameRouter;
import com.example.happykidlearn.models.GameModel;
import com.example.happykidlearn.registry.GameRegistry;

import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeDashboardModelTest {

    @Test
    public void gameRegistry_has15DynamicGames_withDistinctMappedIconsAndBookCovers() {
        List<GameModel> games = GameRegistry.getInstance().getGames();
        assertNotNull(games);
        assertEquals(15, games.size());

        Set<Integer> distinctIcons = new HashSet<>();
        Set<Integer> distinctBookCovers = new HashSet<>();
        for (GameModel game : games) {
            int resolvedIcon = Member2ContentCatalog.resolveGameIcon(game);
            assertNotEquals(0, resolvedIcon);
            assertNotEquals(R.drawable.ic_launcher_foreground, resolvedIcon);
            distinctIcons.add(resolvedIcon);

            int resolvedBookCover = Member2ContentCatalog.resolveBookCoverIllustration(game);
            assertNotEquals(0, resolvedBookCover);
            distinctBookCovers.add(resolvedBookCover);
        }
        assertEquals("Mỗi game trong 15 game phải có 1 icon vector riêng biệt", 15, distinctIcons.size());
        assertEquals("Mỗi game trong 15 game phải có 1 bìa sách minh họa 2.5D riêng biệt", 15, distinctBookCovers.size());
    }

    @Test
    public void homeGameUiModel_handlesZeroTotalWithoutDivisionByZero() {
        GameModel sample = GameRegistry.getInstance().getGameById("counting");
        assertNotNull(sample);

        HomeGameUiModel unknownTotalModel = new HomeGameUiModel(
                sample,
                Member2ContentCatalog.resolveGameIcon(sample),
                sample.getColorResId(),
                Member2ContentCatalog.resolveSoftBackgroundTint(sample.getColorResId()),
                0,
                0,
                0,
                false,
                false
        );
        assertFalse(unknownTotalModel.hasKnownTotal());
        assertEquals(0, unknownTotalModel.getProgressPercent());

        HomeGameUiModel knownTotalModel = new HomeGameUiModel(
                sample,
                Member2ContentCatalog.resolveGameIcon(sample),
                sample.getColorResId(),
                Member2ContentCatalog.resolveSoftBackgroundTint(sample.getColorResId()),
                10,
                20,
                15,
                true,
                true
        );
        assertTrue(knownTotalModel.hasKnownTotal());
        assertEquals(50, knownTotalModel.getProgressPercent());
    }

    @Test
    public void member2GameRouter_routes4Member2GamesAndBlocks11Placeholders() {
        List<GameModel> games = GameRegistry.getInstance().getGames();
        int routedMember2Games = 0;
        int safeBlockedPlaceholders = 0;

        for (GameModel game : games) {
            Class<? extends Activity> target = Member2GameRouter.resolveTargetActivity(game);
            if (Member2ContentCatalog.isMember2OwnedGame(game.getId())) {
                assertNotNull("Game của Thành viên 2 phải có Activity đích: " + game.getId(), target);
                assertNotEquals(MainActivity.class, target);
                routedMember2Games++;
            } else {
                assertNull("Game placeholder của thành viên khác không được mở MainActivity: " + game.getId(), target);
                safeBlockedPlaceholders++;
            }
        }

        assertEquals(4, routedMember2Games);
        assertEquals(11, safeBlockedPlaceholders);
    }
}
