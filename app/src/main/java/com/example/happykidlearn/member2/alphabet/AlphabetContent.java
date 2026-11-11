package com.example.happykidlearn.member2.alphabet;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;

import com.example.happykidlearn.R;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class AlphabetContent {

    public static final int EXPECTED_LETTER_COUNT = 29;
    public static final int GROUP_MILESTONE_SIZE = 5;

    public static final class AlphabetCardItem {
        private final int index;
        private final String upperLetter;
        private final String lowerLetter;
        private final String spokenLetterName;
        private final String exampleWord;
        private final String discoverySentence;
        @DrawableRes
        private final int illustrationResId;
        @ColorRes
        private final int accentColorResId;

        public AlphabetCardItem(
                int index,
                @NonNull String upperLetter,
                @NonNull String lowerLetter,
                @NonNull String spokenLetterName,
                @NonNull String exampleWord,
                @DrawableRes int illustrationResId,
                @ColorRes int accentColorResId
        ) {
            this(
                    index,
                    upperLetter,
                    lowerLetter,
                    spokenLetterName,
                    exampleWord,
                    "Cùng Sao Bông đọc to " + spokenLetterName.toLowerCase(Locale.forLanguageTag("vi-VN"))
                            + " trong từ \"" + exampleWord + "\" nhé!",
                    illustrationResId,
                    accentColorResId
            );
        }

        public AlphabetCardItem(
                int index,
                @NonNull String upperLetter,
                @NonNull String lowerLetter,
                @NonNull String spokenLetterName,
                @NonNull String exampleWord,
                @NonNull String discoverySentence,
                @DrawableRes int illustrationResId,
                @ColorRes int accentColorResId
        ) {
            this.index = index;
            this.upperLetter = upperLetter;
            this.lowerLetter = lowerLetter;
            this.spokenLetterName = spokenLetterName;
            this.exampleWord = exampleWord;
            this.discoverySentence = discoverySentence;
            this.illustrationResId = illustrationResId;
            this.accentColorResId = accentColorResId;
        }

        public int getIndex() {
            return index;
        }

        @NonNull
        public String getUpperLetter() {
            return upperLetter;
        }

        @NonNull
        public String getLowerLetter() {
            return lowerLetter;
        }

        @NonNull
        public String getSpokenLetterName() {
            return spokenLetterName;
        }

        @NonNull
        public String getExampleWord() {
            return exampleWord;
        }

        @NonNull
        public String getDiscoverySentence() {
            return discoverySentence;
        }

        @DrawableRes
        public int getIllustrationResId() {
            return illustrationResId;
        }

        @ColorRes
        public int getAccentColorResId() {
            return accentColorResId;
        }
    }

    private static final List<AlphabetCardItem> CARDS;

    static {
        List<AlphabetCardItem> list = new ArrayList<>(EXPECTED_LETTER_COUNT);
        list.add(new AlphabetCardItem(0, "A", "a", "Chữ a", "Ao cá",
                "Bên ao cá trong veo có đàn cá vàng bơi lội vui vẻ!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_blue));
        list.add(new AlphabetCardItem(1, "Ă", "ă", "Chữ á", "Mặt trăng",
                "Chữ Ă đội chiếc mũ nhỏ giống vầng mặt trăng khuyết trên trời đêm!", R.drawable.m2_illust_alpha_moon, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(2, "Â", "â", "Chữ ớ", "Đám mây",
                "Chữ Â đội nón xinh ngắm đám mây trắng bồng bềnh trôi!", R.drawable.m2_illust_alpha_nature, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(3, "B", "b", "Chữ bờ", "Bóng bay",
                "Chùm bóng bay đủ sắc màu bay cao giữa khu vườn sách!", R.drawable.m2_illust_alpha_object, R.color.m2_accent_pink));
        list.add(new AlphabetCardItem(4, "C", "c", "Chữ cờ", "Con cá",
                "Chú cá nhỏ vẫy đuôi tạo nên những bong bóng nước tròn xoe!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_purple));
        list.add(new AlphabetCardItem(5, "D", "d", "Chữ dờ", "Dưa hấu",
                "Miếng dưa hấu đỏ tươi mát ngọt cho ngày hè rực rỡ!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(6, "Đ", "đ", "Chữ đờ", "Đèn lồng",
                "Chiếc đèn lồng ngôi sao lung linh tỏa sáng đêm hội trăng rằm!", R.drawable.m2_illust_alpha_star, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(7, "E", "e", "Chữ e", "Em bé",
                "Em bé mỉm cười tươi tắn khi nghe Sao Bông đọc chữ!", R.drawable.m2_illust_alpha_object, R.color.m2_accent_pink));
        list.add(new AlphabetCardItem(8, "Ê", "ê", "Chữ ê", "Búp bê",
                "Bạn búp bê nhỏ xinh đội chiếc mũ giống hệt chữ Ê!", R.drawable.m2_illust_alpha_object, R.color.m2_accent_purple));
        list.add(new AlphabetCardItem(9, "G", "g", "Chữ gờ", "Con gà",
                "Chú gà con dậy sớm gọi mặt trời thức giấc mỗi sáng!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(10, "H", "h", "Chữ hờ", "Hoa hồng",
                "Bông hoa hồng năm cánh tỏa hương thơm ngát khắp khu vườn!", R.drawable.m2_illust_alpha_flower, R.color.m2_accent_pink));
        list.add(new AlphabetCardItem(11, "I", "i", "Chữ i", "Viên bi",
                "Chữ i có dấu chấm tròn xinh như viên bi lấp lánh!", R.drawable.m2_illust_alpha_object, R.color.m2_accent_blue));
        list.add(new AlphabetCardItem(12, "K", "k", "Chữ ca", "Cây kem",
                "Cây kem ngọt mát làm bé thích mê trong ngày nắng ấm!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_pink));
        list.add(new AlphabetCardItem(13, "L", "l", "Chữ lờ", "Lá cây",
                "Chiếc lá cây xanh biếc đung đưa đón làn gió mát!", R.drawable.m2_illust_alpha_nature, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(14, "M", "m", "Chữ mờ", "Con mèo",
                "Chú mèo con ngoan ngoãn nằm sưởi nắng bên hiên nhà!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(15, "N", "n", "Chữ nờ", "Quả na",
                "Quả na chín thơm lừng trên cành cây trong vườn bà!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(16, "O", "o", "Chữ o", "Con ong",
                "Chữ O tròn trịa cùng chú ong chăm chỉ tìm mật hoa!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(17, "Ô", "ô", "Chữ ô", "Ô tô",
                "Chiếc ô tô nhỏbon bon lăn bánh trên con đường khám phá!", R.drawable.m2_illust_alpha_vehicle, R.color.m2_accent_blue));
        list.add(new AlphabetCardItem(18, "Ơ", "ơ", "Chữ ơ", "Quả mơ",
                "Chữ Ơ có chiếc móc nhỏ duyên dáng như cuống quả mơ vàng!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(19, "P", "p", "Chữ pờ", "Đèn pin",
                "Chiếc đèn pin soi sáng hành trình thám hiểm của Sao Bông!", R.drawable.m2_illust_alpha_object, R.color.m2_accent_purple));
        list.add(new AlphabetCardItem(20, "Q", "q", "Chữ quy", "Quả cam",
                "Quả cam tròn mọng nước giàu vitamin cho bé khỏe mạnh!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(21, "R", "r", "Chữ rờ", "Con rùa",
                "Bạn rùa nhỏ kiên trì bước từng bước vững chãi tới đích!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(22, "S", "s", "Chữ sờ", "Ngôi sao",
                "Ngôi sao vàng tỏa sáng lấp lánh giống bạn thân Sao Bông!", R.drawable.m2_illust_alpha_star, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(23, "T", "t", "Chữ tờ", "Quả táo",
                "Quả táo đỏ giòn tan trên cành cây xanh mát!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_pink));
        list.add(new AlphabetCardItem(24, "U", "u", "Chữ u", "Đu đủ",
                "Quả đu đủ chín vàng ngọt lịm trong khu vườn mùa thu!", R.drawable.m2_illust_alpha_fruit, R.color.m2_accent_green));
        list.add(new AlphabetCardItem(25, "Ư", "ư", "Chữ ư", "Sư tử",
                "Bạn sư tử bờm vàng dũng cảm và thân thiện của khu rừng!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_orange));
        list.add(new AlphabetCardItem(26, "V", "v", "Chữ vờ", "Con vịt",
                "Chú vịt vàng bơi lội tung tăng trên mặt hồ xanh mát!", R.drawable.m2_illust_alpha_animal, R.color.m2_accent_purple));
        list.add(new AlphabetCardItem(27, "X", "x", "Chữ xờ", "Xe đạp",
                "Chiếc xe đạp hai bánh đưa bé dạo quanh công viên xanh!", R.drawable.m2_illust_alpha_vehicle, R.color.m2_accent_blue));
        list.add(new AlphabetCardItem(28, "Y", "y", "Chữ y", "Y tá",
                "Cô y tá dịu dàng chăm sóc sức khỏe cho mọi người!", R.drawable.m2_illust_alpha_flower, R.color.m2_accent_pink));
        CARDS = Collections.unmodifiableList(list);
    }

    private AlphabetContent() {
    }

    @NonNull
    public static List<AlphabetCardItem> getAllCards() {
        return CARDS;
    }

    @NonNull
    public static List<AlphabetCardItem> buildListeningChoices(int targetIndex, long seed) {
        int safeTarget = Math.max(0, Math.min(CARDS.size() - 1, targetIndex));
        AlphabetCardItem targetItem = CARDS.get(safeTarget);

        List<AlphabetCardItem> candidates = new ArrayList<>(CARDS.size() - 1);
        for (int i = 0; i < CARDS.size(); i++) {
            if (i != safeTarget) {
                candidates.add(CARDS.get(i));
            }
        }
        Random random = new Random(seed);
        Collections.shuffle(candidates, random);

        List<AlphabetCardItem> choices = new ArrayList<>(3);
        choices.add(targetItem);
        choices.add(candidates.get(0));
        choices.add(candidates.get(1));
        Collections.shuffle(choices, random);
        return choices;
    }

    public static String stripVietnameseToneMarksOnly(@NonNull String input) {
        String nfd = Normalizer.normalize(input, Normalizer.Form.NFD);
        StringBuilder sb = new StringBuilder(nfd.length());
        for (int i = 0; i < nfd.length(); i++) {
            char c = nfd.charAt(i);
            // Chỉ loại bỏ 5 dấu thanh: huyền (\u0300), sắc (\u0301), hỏi (\u0309), ngã (\u0303), nặng (\u0323)
            if (c != '\u0300' && c != '\u0301' && c != '\u0309' && c != '\u0303' && c != '\u0323') {
                sb.append(c);
            }
        }
        return Normalizer.normalize(sb.toString(), Normalizer.Form.NFC);
    }

    public static int findTargetLetterIndex(@NonNull String exampleWord, @NonNull String lowerLetter) {
        Locale viLocale = Locale.forLanguageTag("vi-VN");
        String normalizedWord = stripVietnameseToneMarksOnly(exampleWord).toLowerCase(viLocale);
        String target = stripVietnameseToneMarksOnly(lowerLetter).toLowerCase(viLocale);
        return normalizedWord.indexOf(target);
    }
}
