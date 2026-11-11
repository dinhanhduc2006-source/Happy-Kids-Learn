package com.example.happykidlearn.member2.english;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.happykidlearn.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class EnglishContent {

    public static final String TOPIC_COLORS = "colors";
    public static final String TOPIC_ANIMALS = "animals";
    public static final String TOPIC_FAMILY = "family";
    public static final String TOPIC_NUMBERS = "numbers";

    public static final class VocabItem {
        private final String id;
        private final String topicId;
        private final String englishWord;
        private final String phonetic;
        private final String vietnameseMeaning;
        @DrawableRes
        private final int illustrationResId;
        @ColorRes
        private final int accentColorResId;
        private final String exampleSentence;

        public VocabItem(
                @NonNull String id,
                @NonNull String topicId,
                @NonNull String englishWord,
                @NonNull String phonetic,
                @NonNull String vietnameseMeaning,
                @DrawableRes int illustrationResId,
                @ColorRes int accentColorResId) {
            this(
                    id,
                    topicId,
                    englishWord,
                    phonetic,
                    vietnameseMeaning,
                    illustrationResId,
                    accentColorResId,
                    "Look! It is " + englishWord.toLowerCase() + ". • Đây là " + vietnameseMeaning.toLowerCase() + "!");
        }

        public VocabItem(
                @NonNull String id,
                @NonNull String topicId,
                @NonNull String englishWord,
                @NonNull String phonetic,
                @NonNull String vietnameseMeaning,
                @DrawableRes int illustrationResId,
                @ColorRes int accentColorResId,
                @NonNull String exampleSentence) {
            this.id = id;
            this.topicId = topicId;
            this.englishWord = englishWord;
            this.phonetic = phonetic;
            this.vietnameseMeaning = vietnameseMeaning;
            this.illustrationResId = illustrationResId;
            this.accentColorResId = accentColorResId;
            this.exampleSentence = exampleSentence;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getTopicId() {
            return topicId;
        }

        @NonNull
        public String getEnglishWord() {
            return englishWord;
        }

        @NonNull
        public String getPhonetic() {
            return phonetic;
        }

        @NonNull
        public String getVietnameseMeaning() {
            return vietnameseMeaning;
        }

        @DrawableRes
        public int getIllustrationResId() {
            return illustrationResId;
        }

        @ColorRes
        public int getAccentColorResId() {
            return accentColorResId;
        }

        @NonNull
        public String getExampleSentence() {
            return exampleSentence;
        }
    }

    public static final class Topic {
        private final String id;
        private final String englishName;
        private final String vietnameseName;
        private final String worldSubtitle;
        private final List<VocabItem> items;

        public Topic(
                @NonNull String id,
                @NonNull String englishName,
                @NonNull String vietnameseName,
                @NonNull List<VocabItem> items) {
            this(id, englishName, vietnameseName, englishName + " • " + vietnameseName, items);
        }

        public Topic(
                @NonNull String id,
                @NonNull String englishName,
                @NonNull String vietnameseName,
                @NonNull String worldSubtitle,
                @NonNull List<VocabItem> items) {
            this.id = id;
            this.englishName = englishName;
            this.vietnameseName = vietnameseName;
            this.worldSubtitle = worldSubtitle;
            this.items = Collections.unmodifiableList(new ArrayList<>(items));
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getEnglishName() {
            return englishName;
        }

        @NonNull
        public String getVietnameseName() {
            return vietnameseName;
        }

        @NonNull
        public String getWorldSubtitle() {
            return worldSubtitle;
        }

        @NonNull
        public String getDisplayChipTitle() {
            return englishName + " (" + vietnameseName + ")";
        }

        @NonNull
        public List<VocabItem> getItems() {
            return items;
        }
    }

    private static final List<Topic> TOPICS;

    static {
        List<VocabItem> colors = Arrays.asList(
                new VocabItem("en_color_red", TOPIC_COLORS, "Red", "/red/", "Màu đỏ", R.drawable.m2_illust_en_color_red,
                        R.color.accent_pink, "Look! A bright red apple. • Quả táo màu đỏ tươi!"),
                new VocabItem("en_color_blue", TOPIC_COLORS, "Blue", "/bluː/", "Màu xanh dương",
                        R.drawable.m2_illust_en_color_blue, R.color.m2_accent_blue,
                        "The sky is blue! • Bầu trời màu xanh dương!"),
                new VocabItem("en_color_yellow", TOPIC_COLORS, "Yellow", "/ˈjel.oʊ/", "Màu vàng",
                        R.drawable.m2_illust_en_color_yellow, R.color.m2_accent_yellow,
                        "A warm yellow sun! • Ông mặt trời màu vàng ấm áp!"),
                new VocabItem("en_color_green", TOPIC_COLORS, "Green", "/ɡriːn/", "Màu xanh lá",
                        R.drawable.m2_illust_en_color_green, R.color.accent_green,
                        "Fresh green leaves! • Những chiếc lá màu xanh tươi!"),
                new VocabItem("en_color_orange", TOPIC_COLORS, "Orange", "/ˈɔːr.ɪndʒ/", "Màu cam",
                        R.drawable.m2_illust_en_color_orange, R.color.accent_orange,
                        "Sweet orange juice! • Nước cam màu cam ngọt mát!"),
                new VocabItem("en_color_purple", TOPIC_COLORS, "Purple", "/ˈpɜːr.pəl/", "Màu tím",
                        R.drawable.m2_illust_en_color_purple, R.color.purple_500,
                        "A lovely purple flower! • Bông hoa màu tím xinh xắn!"));

        List<VocabItem> animals = Arrays.asList(
                new VocabItem("en_animal_cat", TOPIC_ANIMALS, "Cat", "/kæt/", "Con mèo", R.drawable.m2_illust_en_cat,
                        R.color.accent_orange, "The cute cat says meow! • Chú mèo nhỏ kêu meo meo!"),
                new VocabItem("en_animal_dog", TOPIC_ANIMALS, "Dog", "/dɔːɡ/", "Con chó", R.drawable.m2_illust_en_dog,
                        R.color.purple_500, "A friendly little dog! • Chú cún con thân thiện!"),
                new VocabItem("en_animal_fish", TOPIC_ANIMALS, "Fish", "/fɪʃ/", "Con cá",
                        R.drawable.m2_illust_alpha_animal, R.color.m2_accent_blue,
                        "The fish swims in the pond! • Chú cá bơi trong hồ!"),
                new VocabItem("en_animal_bird", TOPIC_ANIMALS, "Bird", "/bɜːrd/", "Con chim",
                        R.drawable.m2_illust_en_bird, R.color.accent_pink,
                        "The little bird sings a song! • Chú chim nhỏ hót líu lo!"),
                new VocabItem("en_animal_duck", TOPIC_ANIMALS, "Duck", "/dʌk/", "Con vịt", R.drawable.m2_illust_en_duck,
                        R.color.m2_accent_yellow, "Yellow duck on the water! • Chú vịt vàng bơi trên mặt hồ!"),
                new VocabItem("en_animal_rabbit", TOPIC_ANIMALS, "Rabbit", "/ˈræb.ɪt/", "Con thỏ",
                        R.drawable.m2_illust_en_rabbit, R.color.accent_green,
                        "White rabbit loves carrots! • Chú thỏ trắng thích ăn cà rốt!"));

        List<VocabItem> family = Arrays.asList(
                new VocabItem("en_family_father", TOPIC_FAMILY, "Father", "/ˈfɑː.ðɚ/", "Bố / Ba",
                        R.drawable.m2_illust_en_family, R.color.m2_accent_blue,
                        "I love my father! • Bé yêu bố rất nhiều!"),
                new VocabItem("en_family_mother", TOPIC_FAMILY, "Mother", "/ˈmʌð.ɚ/", "Mẹ",
                        R.drawable.m2_illust_en_family, R.color.accent_pink,
                        "My mother hugs me warmly! • Mẹ ôm bé thật ấm áp!"),
                new VocabItem("en_family_brother", TOPIC_FAMILY, "Brother", "/ˈbrʌð.ɚ/", "Anh / Em trai",
                        R.drawable.m2_illust_en_family, R.color.accent_green,
                        "Playing with my brother! • Cùng chơi đùa với anh/em trai!"),
                new VocabItem("en_family_sister", TOPIC_FAMILY, "Sister", "/ˈsɪs.tɚ/", "Chị / Em gái",
                        R.drawable.m2_illust_en_family, R.color.purple_500,
                        "Reading books with my sister! • Đọc sách cùng chị/em gái!"),
                new VocabItem("en_family_grandpa", TOPIC_FAMILY, "Grandpa", "/ˈɡræn.pɑː/", "Ông",
                        R.drawable.m2_illust_en_family, R.color.accent_orange,
                        "Grandpa tells fun stories! • Ông kể chuyện cổ tích thật vui!"),
                new VocabItem("en_family_baby", TOPIC_FAMILY, "Baby", "/ˈbeɪ.bi/", "Em bé",
                        R.drawable.m2_illust_en_family, R.color.m2_accent_yellow,
                        "The happy little baby smiles! • Em bé cười tươi đáng yêu!"));

        List<VocabItem> numbers = Arrays.asList(
                new VocabItem("en_num_one", TOPIC_NUMBERS, "One", "/wʌn/", "Số 1 (Một)", R.drawable.m2_illust_en_num_1,
                        R.color.purple_500, "One bright gem! • Một viên ngọc lấp lánh!"),
                new VocabItem("en_num_two", TOPIC_NUMBERS, "Two", "/tuː/", "Số 2 (Hai)", R.drawable.m2_illust_en_num_2,
                        R.color.accent_orange, "Two happy friends! • Hai người bạn vui vẻ!"),
                new VocabItem("en_num_three", TOPIC_NUMBERS, "Three", "/θriː/", "Số 3 (Ba)",
                        R.drawable.m2_illust_en_num_3, R.color.accent_green,
                        "Count one, two, three! • Cùng đếm một, hai, ba!"),
                new VocabItem("en_num_four", TOPIC_NUMBERS, "Four", "/fɔːr/", "Số 4 (Bốn)",
                        R.drawable.m2_illust_en_num_4, R.color.accent_pink,
                        "Four pink pearls! • Bốn viên ngọc hồng xinh!"),
                new VocabItem("en_num_five", TOPIC_NUMBERS, "Five", "/faɪv/", "Số 5 (Năm)",
                        R.drawable.m2_illust_en_num_5, R.color.m2_accent_blue,
                        "High five with Sao Bong! • Đập tay năm ngón cùng Sao Bông!"),
                new VocabItem("en_num_six", TOPIC_NUMBERS, "Six", "/sɪks/", "Số 6 (Sáu)", R.drawable.m2_illust_en_num_6,
                        R.color.m2_accent_yellow, "Six golden stars! • Sáu ngôi sao vàng rực rỡ!"));

        List<Topic> topicList = Arrays.asList(
                new Topic(TOPIC_COLORS, "Colors", "Màu sắc", "🎨 Xưởng Vẽ Cầu Vồng • Thế giới Sắc Màu", colors),
                new Topic(TOPIC_ANIMALS, "Animals", "Con vật", "🐾 Nông Trại Vui Nhộn • Thế giới Muông Thú", animals),
                new Topic(TOPIC_FAMILY, "Family", "Gia đình", "🏡 Ngôi Nhà Ấm Áp • Thế giới Gia Đình", family),
                new Topic(TOPIC_NUMBERS, "Numbers", "Số đếm", "🔢 Đảo Ngọc Đếm Số • Thế giới Con Số", numbers));
        TOPICS = Collections.unmodifiableList(topicList);
    }

    private EnglishContent() {
    }

    @NonNull
    public static List<Topic> getAllTopics() {
        return TOPICS;
    }

    @Nullable
    public static Topic getTopicById(@Nullable String topicId) {
        if (topicId == null) {
            return null;
        }
        for (Topic topic : TOPICS) {
            if (topic.getId().equalsIgnoreCase(topicId)) {
                return topic;
            }
        }
        return null;
    }

    public static int getTotalVocabularyCount() {
        int total = 0;
        for (Topic topic : TOPICS) {
            total += topic.getItems().size();
        }
        return total;
    }
}
