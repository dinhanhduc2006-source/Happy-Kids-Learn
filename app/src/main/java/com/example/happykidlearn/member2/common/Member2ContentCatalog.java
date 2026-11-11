package com.example.happykidlearn.member2.common;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;

import com.example.happykidlearn.R;
import com.example.happykidlearn.models.GameModel;

public final class Member2ContentCatalog {

    public static final String GAME_ID_ALPHABET = "alphabet";
    public static final String GAME_ID_SPELLING = "spelling";
    public static final String GAME_ID_STORYBOOK = "storybook";
    public static final String GAME_ID_ENGLISH = "english";

    public static final int TOTAL_ALPHABET_ITEMS = 29;
    public static final int TOTAL_SPELLING_ITEMS = 16;
    public static final int TOTAL_STORYBOOK_ITEMS = 3;
    public static final int TOTAL_ENGLISH_ITEMS = 24;

    private Member2ContentCatalog() {
    }

    @DrawableRes
    public static int resolveGameIcon(GameModel gameModel) {
        if (gameModel == null) {
            return R.drawable.m2_ic_game_alphabet;
        }
        int rawIcon = gameModel.getIconResId();
        if (rawIcon != 0 && rawIcon != R.drawable.ic_launcher_foreground) {
            return rawIcon;
        }
        String id = gameModel.getId() != null ? gameModel.getId().toLowerCase() : "";
        switch (id) {
            case "alphabet":
                return R.drawable.m2_ic_game_alphabet;
            case "spelling":
                return R.drawable.m2_ic_game_spelling;
            case "handwriting":
                return R.drawable.m2_ic_game_handwriting;
            case "storybook":
                return R.drawable.m2_ic_game_storybook;
            case "english":
                return R.drawable.m2_ic_game_english;
            case "counting":
                return R.drawable.m2_ic_game_counting;
            case "fun_math":
                return R.drawable.m2_ic_game_fun_math;
            case "life_math":
                return R.drawable.m2_ic_game_life_math;
            case "memory":
                return R.drawable.m2_ic_game_memory;
            case "logic":
                return R.drawable.m2_ic_game_logic;
            case "coding":
                return R.drawable.m2_ic_game_coding;
            case "reflexes":
                return R.drawable.m2_ic_game_reflexes;
            case "music":
                return R.drawable.m2_ic_game_music;
            case "coloring":
                return R.drawable.m2_ic_game_coloring;
            case "versus":
                return R.drawable.m2_ic_game_versus;
            default:
                return R.drawable.m2_ic_game_alphabet;
        }
    }

    @DrawableRes
    public static int resolveBookCoverIllustration(GameModel gameModel) {
        if (gameModel == null || gameModel.getId() == null) {
            return R.drawable.m2_cover_alphabet;
        }
        switch (gameModel.getId().toLowerCase()) {
            case "alphabet":
                return R.drawable.m2_cover_alphabet;
            case "spelling":
                return R.drawable.m2_cover_spelling;
            case "handwriting":
                return R.drawable.m2_cover_handwriting;
            case "storybook":
                return R.drawable.m2_cover_storybook;
            case "english":
                return R.drawable.m2_cover_english;
            case "counting":
                return R.drawable.m2_cover_counting;
            case "fun_math":
                return R.drawable.m2_cover_fun_math;
            case "life_math":
                return R.drawable.m2_cover_life_math;
            case "memory":
                return R.drawable.m2_cover_memory;
            case "logic":
                return R.drawable.m2_cover_logic;
            case "coding":
                return R.drawable.m2_cover_coding;
            case "reflexes":
                return R.drawable.m2_cover_reflexes;
            case "music":
                return R.drawable.m2_cover_music;
            case "coloring":
                return R.drawable.m2_cover_coloring;
            case "versus":
                return R.drawable.m2_cover_versus;
            default:
                return R.drawable.m2_cover_alphabet;
        }
    }

    @ColorRes
    public static int resolveBookCoverColor(GameModel gameModel) {
        if (gameModel == null || gameModel.getId() == null) {
            return R.color.m2_book_cover_coral;
        }
        switch (gameModel.getId().toLowerCase()) {
            case "alphabet":
                return R.color.m2_book_cover_coral;
            case "spelling":
                return R.color.m2_book_cover_teal;
            case "english":
                return R.color.m2_book_cover_purple;
            case "storybook":
                return R.color.m2_book_cover_gold;
            case "handwriting":
                return R.color.m2_book_cover_green;
            case "counting":
                return R.color.m2_book_cover_gold;
            case "fun_math":
                return R.color.m2_book_cover_orange;
            case "life_math":
                return R.color.m2_book_cover_green;
            case "memory":
                return R.color.m2_book_cover_purple;
            case "logic":
                return R.color.m2_book_cover_pink;
            case "coding":
                return R.color.m2_book_cover_sky;
            case "reflexes":
                return R.color.m2_book_cover_orange;
            case "music":
                return R.color.m2_book_cover_pink;
            case "coloring":
                return R.color.m2_book_cover_teal;
            case "versus":
                return R.color.m2_book_cover_coral;
            default:
                return gameModel.getColorResId() != 0 ? gameModel.getColorResId() : R.color.m2_book_cover_teal;
        }
    }

    @DrawableRes
    public static int resolveCategoryIcon(String categoryKey) {
        if (categoryKey == null) {
            return R.drawable.m2_ic_cat_all;
        }
        String lower = categoryKey.trim().toLowerCase();
        if (lower.contains("ngôn ngữ")) {
            return R.drawable.m2_ic_cat_language;
        } else if (lower.contains("toán")) {
            return R.drawable.m2_ic_cat_math;
        } else if (lower.contains("tư duy")) {
            return R.drawable.m2_ic_cat_logic;
        } else if (lower.contains("nghệ thuật") || lower.contains("sáng tạo")) {
            return R.drawable.m2_ic_cat_creative;
        }
        return R.drawable.m2_ic_cat_all;
    }

    @DrawableRes
    public static int resolveCategoryPillBackground(String categoryKey) {
        if (categoryKey == null) {
            return R.drawable.m2_bg_cat_pill_all;
        }
        String lower = categoryKey.trim().toLowerCase();
        if (lower.contains("ngôn ngữ")) {
            return R.drawable.m2_bg_cat_pill_language;
        } else if (lower.contains("toán")) {
            return R.drawable.m2_bg_cat_pill_math;
        } else if (lower.contains("tư duy")) {
            return R.drawable.m2_bg_cat_pill_logic;
        } else if (lower.contains("nghệ thuật") || lower.contains("sáng tạo")) {
            return R.drawable.m2_bg_cat_pill_creative;
        }
        return R.drawable.m2_bg_cat_pill_all;
    }

    public static String resolveCategoryDisplayLabel(String categoryKey) {
        if (categoryKey == null) {
            return "";
        }
        if ("Nghệ thuật".equalsIgnoreCase(categoryKey.trim())) {
            return "Sáng tạo";
        }
        return categoryKey.trim();
    }

    public static int getDefaultTotalLevels(String gameId) {
        if (gameId == null) {
            return 0;
        }
        switch (gameId.toLowerCase()) {
            case GAME_ID_ALPHABET:
                return TOTAL_ALPHABET_ITEMS;
            case GAME_ID_SPELLING:
                return TOTAL_SPELLING_ITEMS;
            case GAME_ID_STORYBOOK:
                return TOTAL_STORYBOOK_ITEMS;
            case GAME_ID_ENGLISH:
                return TOTAL_ENGLISH_ITEMS;
            default:
                return 0;
        }
    }

    public static boolean isMember2OwnedGame(String gameId) {
        if (gameId == null) {
            return false;
        }
        switch (gameId.toLowerCase()) {
            case GAME_ID_ALPHABET:
            case GAME_ID_SPELLING:
            case GAME_ID_STORYBOOK:
            case GAME_ID_ENGLISH:
                return true;
            default:
                return false;
        }
    }

    @ColorRes
    public static int resolveSoftBackgroundTint(@ColorRes int colorResId) {
        if (colorResId == R.color.purple_500 || colorResId == R.color.purple_700
                || colorResId == R.color.m2_book_cover_purple) {
            return R.color.m2_accent_purple_light;
        } else if (colorResId == R.color.accent_orange || colorResId == R.color.m2_book_cover_orange) {
            return R.color.m2_accent_orange_light;
        } else if (colorResId == R.color.accent_green || colorResId == R.color.m2_book_cover_green) {
            return R.color.m2_accent_green_light;
        } else if (colorResId == R.color.accent_pink || colorResId == R.color.m2_book_cover_coral
                || colorResId == R.color.m2_book_cover_pink) {
            return R.color.m2_accent_pink_light;
        } else if (colorResId == R.color.accent_yellow || colorResId == R.color.m2_book_cover_gold) {
            return R.color.m2_accent_yellow_light;
        }
        return R.color.m2_accent_blue_light;
    }
}
