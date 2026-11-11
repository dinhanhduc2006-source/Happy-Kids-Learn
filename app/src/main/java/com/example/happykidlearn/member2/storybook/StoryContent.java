package com.example.happykidlearn.member2.storybook;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class StoryContent {

    public static final class StoryBook {
        private final String id;
        private final String title;
        private final String shortDescription;
        private final String moralLesson;
        @DrawableRes
        private final int coverIllustrationResId;
        @ColorRes
        private final int accentColorResId;
        private final List<StoryPage> pages;
        private final String miniQuestion;
        private final String correctAnswer;
        private final String wrongAnswer;

        public StoryBook(
                @NonNull String id,
                @NonNull String title,
                @NonNull String shortDescription,
                @NonNull String moralLesson,
                @DrawableRes int coverIllustrationResId,
                @ColorRes int accentColorResId,
                @NonNull List<StoryPage> pages
        ) {
            this(
                    id,
                    title,
                    shortDescription,
                    moralLesson,
                    coverIllustrationResId,
                    accentColorResId,
                    pages,
                    "Đố bé: Nhân vật trong truyện đã làm điều gì đáng khen?",
                    "Biết yêu thương và làm việc tốt ✓",
                    "Quên giúp đỡ bạn bè"
            );
        }

        public StoryBook(
                @NonNull String id,
                @NonNull String title,
                @NonNull String shortDescription,
                @NonNull String moralLesson,
                @DrawableRes int coverIllustrationResId,
                @ColorRes int accentColorResId,
                @NonNull List<StoryPage> pages,
                @NonNull String miniQuestion,
                @NonNull String correctAnswer,
                @NonNull String wrongAnswer
        ) {
            this.id = id;
            this.title = title;
            this.shortDescription = shortDescription;
            this.moralLesson = moralLesson;
            this.coverIllustrationResId = coverIllustrationResId;
            this.accentColorResId = accentColorResId;
            this.pages = Collections.unmodifiableList(new ArrayList<>(pages));
            this.miniQuestion = miniQuestion;
            this.correctAnswer = correctAnswer;
            this.wrongAnswer = wrongAnswer;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getTitle() {
            return title;
        }

        @NonNull
        public String getShortDescription() {
            return shortDescription;
        }

        @NonNull
        public String getMoralLesson() {
            return moralLesson;
        }

        @DrawableRes
        public int getCoverIllustrationResId() {
            return coverIllustrationResId;
        }

        @ColorRes
        public int getAccentColorResId() {
            return accentColorResId;
        }

        @NonNull
        public List<StoryPage> getPages() {
            return pages;
        }

        @NonNull
        public String getMiniQuestion() {
            return miniQuestion;
        }

        @NonNull
        public String getCorrectAnswer() {
            return correctAnswer;
        }

        @NonNull
        public String getWrongAnswer() {
            return wrongAnswer;
        }
    }

    private static final List<StoryBook> STORIES;

    static {
        List<StoryPage> rabbitPages = Arrays.asList(
                new StoryPage(
                        0,
                        "Buổi sáng nắng ấm, Thỏ Trắng tung tăng vào khu rừng xanh. Chú tìm thấy một giỏ cà rốt đỏ tươi rất ngon.",
                        "Thỏ Trắng tìm thấy giỏ cà rốt trong rừng",
                        R.drawable.m2_illust_en_rabbit
                ),
                new StoryPage(
                        1,
                        "Trên đường về nhà, Thỏ Trắng gặp bạn Nhím Nhỏ đang đói bụng. Nhím Nhỏ buồn bã vì chưa tìm được quả nào.",
                        "Thỏ Trắng gặp bạn Nhím Nhỏ trên đường",
                        R.drawable.m2_illust_alpha_nature
                ),
                new StoryPage(
                        2,
                        "Thỏ Trắng mỉm cười và tặng cho Nhím Nhỏ hai củ cà rốt to nhất. Nhím Nhỏ cảm ơn bạn rối rít.",
                        "Thỏ Trắng vui vẻ tặng cà rốt cho bạn",
                        R.drawable.m2_illust_alpha_fruit
                ),
                new StoryPage(
                        3,
                        "Đi thêm một đoạn, Thỏ Trắng lại chia phần cà rốt cho bạn Cún Nâu. Các bạn nhỏ cùng nhau ngồi ăn thật vui vẻ.",
                        "Các bạn nhỏ quây quần bên nhau",
                        R.drawable.m2_illust_en_dog
                ),
                new StoryPage(
                        4,
                        "Về đến nhà, mẹ ôm Thỏ Trắng và khen chú biết yêu thương bạn bè. Thỏ Trắng thấy trong lòng ấm áp vô cùng.",
                        "Mẹ khen Thỏ Trắng ngoan ngoãn, biết sẻ chia",
                        R.drawable.m2_illust_alpha_flower
                )
        );

        List<StoryPage> duckPages = Arrays.asList(
                new StoryPage(
                        0,
                        "Bên bờ hồ trong xanh, Vịt Vàng rất thích bơi lội mỗi ngày. Nước hồ mát lành soi bóng những đám mây trắng.",
                        "Vịt Vàng bơi lội trên mặt hồ trong xanh",
                        R.drawable.m2_illust_en_duck
                ),
                new StoryPage(
                        1,
                        "Sau cơn gió lớn, nhiều cành khô và lá rụng rơi đầy mặt hồ. Đàn cá nhỏ không còn chỗ bơi lội vui đùa.",
                        "Lá khô rơi xuống mặt hồ sau cơn gió",
                        R.drawable.m2_illust_alpha_animal
                ),
                new StoryPage(
                        2,
                        "Vịt Vàng liền rủ bạn Chim Nhỏ cùng nhau nhặt cành khô lên bờ. Hai bạn chăm chỉ dọn dẹp thật gọn gàng.",
                        "Vịt Vàng và Chim Nhỏ cùng dọn sạch bờ hồ",
                        R.drawable.m2_illust_en_bird
                ),
                new StoryPage(
                        3,
                        "Chẳng mấy chốc, mặt hồ đã trong vắt và sạch đẹp trở lại. Đàn cá nhỏ tung tăng nhảy múa cảm ơn hai bạn.",
                        "Mặt hồ sạch đẹp, đàn cá vui mừng",
                        R.drawable.m2_illust_alpha_nature
                ),
                new StoryPage(
                        4,
                        "Từ hôm đó, các bạn nhỏ trong rừng luôn nhắc nhau giữ hồ nước sạch sẽ. Khu rừng lúc nào cũng tràn ngập tiếng cười.",
                        "Khu rừng xanh sạch và tràn đầy niềm vui",
                        R.drawable.m2_illust_alpha_star
                )
        );

        List<StoryPage> catPages = Arrays.asList(
                new StoryPage(
                        0,
                        "Khi ông mặt trăng tròn hiện lên trên bầu trời đêm, những ngôi sao nhỏ bắt đầu lấp lánh. Đã đến giờ các bạn nhỏ nghỉ ngơi.",
                        "Ông trăng và những ngôi sao xuất hiện",
                        R.drawable.m2_illust_alpha_moon
                ),
                new StoryPage(
                        1,
                        "Mèo Cam ngoan ngoãn cất gọn đồ chơi vào chiếc hộp nhỏ. Chú tự giác đánh răng sạch sẽ và rửa mặt thơm tho.",
                        "Mèo Cam cất gọn đồ chơi trước khi ngủ",
                        R.drawable.m2_illust_en_cat
                ),
                new StoryPage(
                        2,
                        "Mèo mẹ âu yếm đắp chăn ấm và kể cho Mèo Cam nghe một câu chuyện êm dịu. Đôi mắt Mèo Cam khẽ nhắm lại thật bình yên.",
                        "Mèo Cam nghe mẹ kể chuyện và ngủ ngon",
                        R.drawable.m2_illust_en_family
                ),
                new StoryPage(
                        3,
                        "Sáng hôm sau, khi ông mặt trời thức dậy, Mèo Cam vươn vai khỏe khoắn. Chú sẵn sàng cho một ngày mới tràn ngập niềm vui.",
                        "Mèo Cam thức dậy khỏe mạnh đón nắng mai",
                        R.drawable.m2_illust_alpha_star
                )
        );

        List<StoryBook> list = Arrays.asList(
                new StoryBook(
                        "story_rabbit_carrot",
                        "Thỏ Trắng Biết Chia Sẻ",
                        "Chú Thỏ Trắng tìm được giỏ cà rốt tươi ngon và học cách sẻ chia cùng bạn bè.",
                        "Chia sẻ niềm vui sẽ làm tình bạn thêm gắn kết.",
                        R.drawable.m2_illust_en_rabbit,
                        R.color.accent_pink,
                        rabbitPages,
                        "🤔 Đố bé: Thỏ Trắng đã làm gì khi gặp Nhím Nhỏ và Cún Nâu?",
                        "Chia sẻ cà rốt cho các bạn ★",
                        "Giấu cà rốt ăn một mình"
                ),
                new StoryBook(
                        "story_duck_pond",
                        "Vịt Con Giữ Hồ Sạch Đẹp",
                        "Vịt Vàng và Chim Nhỏ cùng nhau dọn sạch bờ hồ để bảo vệ ngôi nhà chung.",
                        "Giữ gìn vệ sinh giúp môi trường quanh bé luôn xanh sạch.",
                        R.drawable.m2_illust_en_duck,
                        R.color.accent_green,
                        duckPages,
                        "🤔 Đố bé: Vịt Vàng và Chim Nhỏ đã làm gì khi mặt hồ có nhiều cành khô?",
                        "Cùng nhau dọn sạch mặt hồ ★",
                        "Bỏ đi nơi khác chơi"
                ),
                new StoryBook(
                        "story_cat_sleep",
                        "Mèo Con Ngủ Đúng Giờ",
                        "Mèo Cam học thói quen cất đồ chơi, đánh răng và đi ngủ sớm để sáng mai khỏe mạnh.",
                        "Ngủ đúng giờ giúp bé cao lớn và tràn đầy năng lượng.",
                        R.drawable.m2_illust_en_cat,
                        R.color.purple_500,
                        catPages,
                        "🤔 Đố bé: Trước khi đi ngủ, Mèo Cam đã làm việc tốt nào?",
                        "Cất đồ chơi và đánh răng sạch ★",
                        "Thức khuya nghịch đồ chơi"
                )
        );
        STORIES = Collections.unmodifiableList(list);
    }

    private StoryContent() {
    }

    @NonNull
    public static List<StoryBook> getAllStories() {
        return STORIES;
    }

    @Nullable
    public static StoryBook getStoryById(@Nullable String storyId) {
        if (storyId == null) {
            return null;
        }
        for (StoryBook story : STORIES) {
            if (story.getId().equalsIgnoreCase(storyId)) {
                return story;
            }
        }
        return null;
    }
}
