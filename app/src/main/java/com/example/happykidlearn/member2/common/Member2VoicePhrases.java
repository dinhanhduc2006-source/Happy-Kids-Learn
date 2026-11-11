package com.example.happykidlearn.member2.common;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Bộ câu phản hồi bằng giọng nói tiếng Việt (đúng / sai / hoàn thành) cho 4 mini-game Thành viên 2.
 * Lớp thuần Java (không phụ thuộc Android) để có thể kiểm thử bằng unit test:
 * - Chọn ngẫu nhiên nhưng không lặp lại câu vừa nói ở cùng nhóm.
 * - Chính sách chống phát lặp khi trẻ bấm liên tục (cooldown theo nhóm).
 */
public final class Member2VoicePhrases {

    public enum Category {
        CORRECT,
        WRONG,
        COMPLETE
    }

    /** Khoảng nghỉ tối thiểu giữa hai lần phát cùng một nhóm câu (chống bấm liên tục). */
    public static final long SAME_CATEGORY_COOLDOWN_MS = 1500L;

    public static final List<String> CORRECT_PHRASES = Collections.unmodifiableList(Arrays.asList(
            "Chính xác rồi! Bé giỏi lắm!",
            "Tuyệt vời! Bé làm đúng rồi!",
            "Xuất sắc! Tiếp tục nào!",
            "Hay quá! Bé thật thông minh!"
    ));

    public static final List<String> WRONG_PHRASES = Collections.unmodifiableList(Arrays.asList(
            "Chưa đúng rồi, mình thử lại nhé!",
            "Không sao đâu, bé thử lần nữa nào!",
            "Cố lên! Bé sắp làm được rồi!",
            "Thử lại một lần nữa nhé!"
    ));

    public static final List<String> COMPLETE_PHRASES = Collections.unmodifiableList(Arrays.asList(
            "Chúc mừng! Bé đã hoàn thành bài học!",
            "Tuyệt vời! Bé đã vượt qua thử thách!",
            "Giỏi lắm! Cùng khám phá bài tiếp theo nhé!"
    ));

    private Member2VoicePhrases() {
    }

    @NonNull
    public static List<String> forCategory(@NonNull Category category) {
        switch (category) {
            case CORRECT:
                return CORRECT_PHRASES;
            case WRONG:
                return WRONG_PHRASES;
            case COMPLETE:
            default:
                return COMPLETE_PHRASES;
        }
    }

    /**
     * Chọn chỉ số câu ngẫu nhiên, không trùng với câu vừa phát ({@code lastIndex}).
     */
    public static int pickIndex(int size, int lastIndex, @NonNull Random random) {
        if (size <= 1) {
            return 0;
        }
        if (lastIndex < 0 || lastIndex >= size) {
            return random.nextInt(size);
        }
        int idx = random.nextInt(size - 1);
        if (idx >= lastIndex) {
            idx++;
        }
        return idx;
    }

    /**
     * Trả về true nếu được phép phát nhóm câu {@code category} tại thời điểm {@code nowMs}.
     * Chặn phát lặp cùng một nhóm trong {@link #SAME_CATEGORY_COOLDOWN_MS}.
     */
    public static boolean passesCooldown(
            @NonNull Category category,
            long nowMs,
            @Nullable Category lastCategory,
            long lastSpokenAtMs
    ) {
        if (lastCategory == null || lastCategory != category) {
            return true;
        }
        return nowMs - lastSpokenAtMs >= SAME_CATEGORY_COOLDOWN_MS;
    }
}

