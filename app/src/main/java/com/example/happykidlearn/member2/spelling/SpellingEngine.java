package com.example.happykidlearn.member2.spelling;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class SpellingEngine {

    private static final Locale LOCALE_VI = Locale.forLanguageTag("vi-VN");
    private static final List<SpellingExercise> EXERCISES;

    static {
        List<SpellingExercise> list = new ArrayList<>(16);
        list.add(new SpellingExercise(
                "sp_01", "ba",
                Arrays.asList("b", "a"),
                Arrays.asList("c", "o"),
                "Ba của bé",
                "Ghép âm b và âm a: b + a = ba",
                R.drawable.m2_illust_alpha_object
        ));
        list.add(new SpellingExercise(
                "sp_02", "bé",
                Arrays.asList("b", "é"),
                Arrays.asList("m", "e"),
                "Em bé ngoan",
                "Ghép âm b và âm é: b + é = bé",
                R.drawable.m2_illust_alpha_object
        ));
        list.add(new SpellingExercise(
                "sp_03", "cá",
                Arrays.asList("c", "á"),
                Arrays.asList("g", "a"),
                "Con cá vàng",
                "Ghép âm c và âm á: c + á = cá",
                R.drawable.m2_illust_alpha_animal
        ));
        list.add(new SpellingExercise(
                "sp_04", "mẹ",
                Arrays.asList("m", "ẹ"),
                Arrays.asList("n", "é"),
                "Mẹ yêu bé",
                "Ghép âm m và âm ẹ: m + ẹ = mẹ",
                R.drawable.m2_illust_alpha_flower
        ));
        list.add(new SpellingExercise(
                "sp_05", "gà",
                Arrays.asList("g", "à"),
                Arrays.asList("c", "á"),
                "Con gà trống",
                "Ghép âm g và âm à: g + à = gà",
                R.drawable.m2_illust_alpha_animal
        ));
        list.add(new SpellingExercise(
                "sp_06", "lá",
                Arrays.asList("l", "á"),
                Arrays.asList("h", "a"),
                "Chiếc lá xanh",
                "Ghép âm l và âm á: l + á = lá",
                R.drawable.m2_illust_alpha_nature
        ));
        list.add(new SpellingExercise(
                "sp_07", "xe",
                Arrays.asList("x", "e"),
                Arrays.asList("s", "ê"),
                "Chiếc xe đạp",
                "Ghép âm x và âm e: x + e = xe",
                R.drawable.m2_illust_alpha_vehicle
        ));
        list.add(new SpellingExercise(
                "sp_08", "bi",
                Arrays.asList("b", "i"),
                Arrays.asList("d", "u"),
                "Viên bi tròn",
                "Ghép âm b và âm i: b + i = bi",
                R.drawable.m2_illust_alpha_object
        ));
        list.add(new SpellingExercise(
                "sp_09", "cờ",
                Arrays.asList("c", "ờ"),
                Arrays.asList("t", "ơ"),
                "Lá cờ đỏ",
                "Ghép âm c và âm ờ: c + ờ = cờ",
                R.drawable.m2_illust_alpha_star
        ));
        list.add(new SpellingExercise(
                "sp_10", "nhà",
                Arrays.asList("nh", "à"),
                Arrays.asList("ch", "a"),
                "Ngôi nhà nhỏ",
                "Ghép âm đầu nh và âm à: nh + à = nhà",
                R.drawable.m2_illust_alpha_object
        ));
        list.add(new SpellingExercise(
                "sp_11", "cam",
                Arrays.asList("c", "a", "m"),
                Arrays.asList("n", "o"),
                "Quả cam tươi",
                "Ghép 3 chữ: c - a - m",
                R.drawable.m2_illust_alpha_fruit
        ));
        list.add(new SpellingExercise(
                "sp_12", "mèo",
                Arrays.asList("m", "è", "o"),
                Arrays.asList("b", "e"),
                "Con mèo con",
                "Ghép 3 chữ: m - è - o",
                R.drawable.m2_illust_alpha_animal
        ));
        list.add(new SpellingExercise(
                "sp_13", "hoa",
                Arrays.asList("h", "o", "a"),
                Arrays.asList("k", "ô"),
                "Bông hoa hồng",
                "Ghép 3 chữ: h - o - a",
                R.drawable.m2_illust_alpha_flower
        ));
        list.add(new SpellingExercise(
                "sp_14", "sao",
                Arrays.asList("s", "a", "o"),
                Arrays.asList("x", "ơ"),
                "Ngôi sao sáng",
                "Ghép 3 chữ: s - a - o",
                R.drawable.m2_illust_alpha_star
        ));
        list.add(new SpellingExercise(
                "sp_15", "táo",
                Arrays.asList("t", "á", "o"),
                Arrays.asList("l", "a"),
                "Quả táo đỏ",
                "Ghép 3 chữ: t - á - o",
                R.drawable.m2_illust_alpha_fruit
        ));
        list.add(new SpellingExercise(
                "sp_16", "rùa",
                Arrays.asList("r", "ù", "a"),
                Arrays.asList("v", "u"),
                "Con rùa bơi",
                "Ghép 3 chữ: r - ù - a",
                R.drawable.m2_illust_alpha_animal
        ));
        EXERCISES = Collections.unmodifiableList(list);
    }

    private SpellingEngine() {
    }

    @NonNull
    public static List<SpellingExercise> getAllExercises() {
        return EXERCISES;
    }

    @NonNull
    public static String normalizeUnicode(@Nullable String input) {
        if (input == null) {
            return "";
        }
        return Normalizer.normalize(input.trim(), Normalizer.Form.NFC).toLowerCase(LOCALE_VI);
    }

    @NonNull
    public static List<String> buildTilePool(@NonNull SpellingExercise exercise, long seed) {
        List<String> pool = new ArrayList<>(exercise.getTargetTokens().size() + exercise.getDistractorTokens().size());
        pool.addAll(exercise.getTargetTokens());
        pool.addAll(exercise.getDistractorTokens());
        Collections.shuffle(pool, new Random(seed));
        return pool;
    }

    public static boolean isAttemptComplete(@NonNull SpellingExercise exercise, @Nullable List<String> placedTokens) {
        if (placedTokens == null || placedTokens.size() != exercise.getSlotCount()) {
            return false;
        }
        for (String token : placedTokens) {
            if (token == null || token.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnswerCorrect(@NonNull SpellingExercise exercise, @Nullable List<String> placedTokens) {
        if (!isAttemptComplete(exercise, placedTokens)) {
            return false;
        }
        StringBuilder joined = new StringBuilder();
        List<String> expectedTokens = exercise.getTargetTokens();
        for (int i = 0; i < expectedTokens.size(); i++) {
            String expected = normalizeUnicode(expectedTokens.get(i));
            String actual = normalizeUnicode(placedTokens.get(i));
            if (!expected.equals(actual)) {
                return false;
            }
            joined.append(actual);
        }
        return normalizeUnicode(exercise.getTargetWord()).equals(normalizeUnicode(joined.toString()));
    }

    public static int findFirstSlotNeedingHelp(@NonNull SpellingExercise exercise, @NonNull List<String> currentSlots) {
        List<String> expected = exercise.getTargetTokens();
        for (int i = 0; i < expected.size(); i++) {
            String placed = i < currentSlots.size() ? currentSlots.get(i) : null;
            if (placed == null || !normalizeUnicode(expected.get(i)).equals(normalizeUnicode(placed))) {
                return i;
            }
        }
        return -1;
    }
}

