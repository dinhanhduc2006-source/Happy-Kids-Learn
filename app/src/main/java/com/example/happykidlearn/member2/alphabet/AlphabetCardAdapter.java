package com.example.happykidlearn.member2.alphabet;

import android.content.Context;
import android.content.res.ColorStateList;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happykidlearn.R;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class AlphabetCardAdapter extends RecyclerView.Adapter<AlphabetCardAdapter.AlphabetViewHolder> {

    public interface OnAlphabetActionListener {
        void onSpeakLetterClicked(AlphabetContent.AlphabetCardItem item);

        void onSpeakWordClicked(AlphabetContent.AlphabetCardItem item);

        default void onSpeakBothClicked(AlphabetContent.AlphabetCardItem item) {
            onSpeakWordClicked(item);
        }

        default void onCardFlipped(AlphabetContent.AlphabetCardItem item, boolean isBackVisible) {
        }
    }

    private final List<AlphabetContent.AlphabetCardItem> cards;
    private final OnAlphabetActionListener listener;
    private final Set<String> learnedLetters = new HashSet<>();
    private final Set<Integer> flippedPositions = new HashSet<>();

    public AlphabetCardAdapter(
            @NonNull List<AlphabetContent.AlphabetCardItem> cards,
            @NonNull OnAlphabetActionListener listener
    ) {
        this.cards = cards;
        this.listener = listener;
    }

    public void updateLearnedLetters(@NonNull Set<String> completedUpperLetters) {
        if (learnedLetters.equals(completedUpperLetters)) {
            return;
        }
        learnedLetters.clear();
        learnedLetters.addAll(completedUpperLetters);
        notifyDataSetChanged();
    }

    public void resetFlippedStateForPosition(int position) {
        if (flippedPositions.remove(position)) {
            notifyItemChanged(position);
        }
    }

    @NonNull
    @Override
    public AlphabetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.m2_item_alphabet_flashcard, parent, false);
        return new AlphabetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlphabetViewHolder holder, int position) {
        AlphabetContent.AlphabetCardItem item = cards.get(position);
        boolean isLearned = learnedLetters.contains(item.getUpperLetter());
        boolean isBackVisible = flippedPositions.contains(item.getIndex());
        holder.bind(item, cards.size(), isLearned, isBackVisible, learnedLetters, flippedPositions, listener);
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    static class AlphabetViewHolder extends RecyclerView.ViewHolder {

        private final View cardFlashcard;
        private final View llCardFront;
        private final View llCardBack;
        private final TextView tvCardIndex;
        private final TextView tvLearnedBadge;
        private final View llLettersBox;
        private final TextView tvUpper;
        private final TextView tvLower;
        private final View llIllustBox;
        private final ImageView ivIllustration;
        private final TextView tvExampleWord;
        private final TextView tvPhoneticHint;
        private final TextView btnFlipFront;
        private final Button btnSpeakLetter;
        private final Button btnSpeakWord;

        // Back face views
        private final TextView tvBackLetterBadge;
        private final TextView btnFlipBack;
        private final ImageView ivBackIllustration;
        private final TextView tvBackWord;
        private final TextView tvDiscoveryStory;
        private final Button btnSpeakBoth;

        AlphabetViewHolder(@NonNull View itemView) {
            super(itemView);
            cardFlashcard = itemView.findViewById(R.id.card_m2_alpha_flashcard);
            llCardFront = itemView.findViewById(R.id.ll_m2_alpha_card_front);
            llCardBack = itemView.findViewById(R.id.ll_m2_alpha_card_back);
            tvCardIndex = itemView.findViewById(R.id.tv_m2_alpha_card_index);
            tvLearnedBadge = itemView.findViewById(R.id.tv_m2_alpha_learned_badge);
            llLettersBox = itemView.findViewById(R.id.ll_m2_alpha_letters_box);
            tvUpper = itemView.findViewById(R.id.tv_m2_alpha_upper);
            tvLower = itemView.findViewById(R.id.tv_m2_alpha_lower);
            llIllustBox = itemView.findViewById(R.id.ll_m2_alpha_illust_box);
            ivIllustration = itemView.findViewById(R.id.iv_m2_alpha_illustration);
            tvExampleWord = itemView.findViewById(R.id.tv_m2_alpha_example_word);
            tvPhoneticHint = itemView.findViewById(R.id.tv_m2_alpha_phonetic_hint);
            btnFlipFront = itemView.findViewById(R.id.btn_m2_alpha_flip_front);
            btnSpeakLetter = itemView.findViewById(R.id.btn_m2_alpha_speak_letter);
            btnSpeakWord = itemView.findViewById(R.id.btn_m2_alpha_speak_word);

            tvBackLetterBadge = itemView.findViewById(R.id.tv_m2_alpha_back_letter_badge);
            btnFlipBack = itemView.findViewById(R.id.btn_m2_alpha_flip_back);
            ivBackIllustration = itemView.findViewById(R.id.iv_m2_alpha_back_illustration);
            tvBackWord = itemView.findViewById(R.id.tv_m2_alpha_back_word);
            tvDiscoveryStory = itemView.findViewById(R.id.tv_m2_alpha_discovery_story);
            btnSpeakBoth = itemView.findViewById(R.id.btn_m2_alpha_speak_both);

            float density = itemView.getResources().getDisplayMetrics().density;
            cardFlashcard.setCameraDistance(8000f * density);
        }

        void bind(
                @NonNull AlphabetContent.AlphabetCardItem item,
                int totalCount,
                boolean isLearned,
                boolean isBackVisible,
                @NonNull Set<String> learnedLetters,
                @NonNull Set<Integer> flippedPositions,
                @NonNull OnAlphabetActionListener listener
        ) {
            Context context = itemView.getContext();
            int accentColor = ContextCompat.getColor(context, item.getAccentColorResId());

            cardFlashcard.animate().cancel();
            cardFlashcard.setRotationY(0f);
            llCardFront.setVisibility(isBackVisible ? View.GONE : View.VISIBLE);
            llCardBack.setVisibility(isBackVisible ? View.VISIBLE : View.GONE);

            tvCardIndex.setText(context.getString(
                    R.string.m2_alphabet_card_index_format,
                    item.getIndex() + 1
            ));

            if (isLearned) {
                bindLearnedBadgeState(context, tvLearnedBadge, true);
            } else {
                bindLearnedBadgeState(context, tvLearnedBadge, false);
            }

            tvUpper.setText(item.getUpperLetter());
            tvUpper.setTextColor(accentColor);
            tvLower.setText(item.getLowerLetter());

            ivIllustration.setImageResource(item.getIllustrationResId());
            ivIllustration.setContentDescription(context.getString(
                    R.string.m2_cd_alphabet_illustration_format,
                    item.getExampleWord()
            ));

            CharSequence highlightedWord = buildHighlightedWord(item.getExampleWord(), item.getLowerLetter(), accentColor);
            tvExampleWord.setText(highlightedWord);
            tvPhoneticHint.setText("🔊 Âm đọc: " + item.getSpokenLetterName() + " • Chạm chữ để nghe");

            btnSpeakLetter.setBackgroundTintList(ColorStateList.valueOf(accentColor));

            // Back side data
            tvBackLetterBadge.setText("Chữ " + item.getUpperLetter() + " • " + item.getLowerLetter());
            ivBackIllustration.setImageResource(item.getIllustrationResId());
            tvBackWord.setText(highlightedWord);
            tvDiscoveryStory.setText(item.getDiscoverySentence());
            btnSpeakBoth.setBackgroundTintList(ColorStateList.valueOf(accentColor));

            llLettersBox.setOnClickListener(v -> {
                learnedLetters.add(item.getUpperLetter());
                bindLearnedBadgeState(context, tvLearnedBadge, true);
                playPulseAnimation(llLettersBox);
                listener.onSpeakLetterClicked(item);
            });

            View.OnClickListener speakWordClick = v -> {
                learnedLetters.add(item.getUpperLetter());
                bindLearnedBadgeState(context, tvLearnedBadge, true);
                playPulseAnimation(ivIllustration);
                listener.onSpeakWordClicked(item);
            };
            ivIllustration.setOnClickListener(speakWordClick);
            if (llIllustBox != null) {
                llIllustBox.setOnClickListener(speakWordClick);
            }

            btnSpeakLetter.setOnClickListener(v -> {
                learnedLetters.add(item.getUpperLetter());
                bindLearnedBadgeState(context, tvLearnedBadge, true);
                playPulseAnimation(llLettersBox);
                listener.onSpeakLetterClicked(item);
            });

            btnSpeakWord.setOnClickListener(v -> {
                learnedLetters.add(item.getUpperLetter());
                bindLearnedBadgeState(context, tvLearnedBadge, true);
                playPulseAnimation(ivIllustration);
                listener.onSpeakWordClicked(item);
            });

            if (btnFlipFront != null) {
                btnFlipFront.setOnClickListener(v -> {
                    learnedLetters.add(item.getUpperLetter());
                    bindLearnedBadgeState(context, tvLearnedBadge, true);
                    flipCard(true, item, flippedPositions, listener);
                });
            }
            if (btnFlipBack != null) {
                btnFlipBack.setOnClickListener(v -> flipCard(false, item, flippedPositions, listener));
            }
            if (btnSpeakBoth != null) {
                btnSpeakBoth.setOnClickListener(v -> {
                    learnedLetters.add(item.getUpperLetter());
                    bindLearnedBadgeState(context, tvLearnedBadge, true);
                    playPulseAnimation(ivBackIllustration);
                    listener.onSpeakBothClicked(item);
                });
            }
        }

        private static void bindLearnedBadgeState(Context context, TextView tvLearnedBadge, boolean isLearned) {
            if (isLearned) {
                tvLearnedBadge.setText(R.string.m2_alphabet_status_learned);
                tvLearnedBadge.setTextColor(ContextCompat.getColor(context, R.color.m2_badge_ready_text));
                tvLearnedBadge.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.m2_badge_ready_bg)
                ));
            } else {
                tvLearnedBadge.setText(R.string.m2_alphabet_status_new);
                tvLearnedBadge.setTextColor(ContextCompat.getColor(context, R.color.m2_badge_soon_text));
                tvLearnedBadge.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.m2_badge_soon_bg)
                ));
            }
        }

        private void flipCard(
                boolean showBack,
                @NonNull AlphabetContent.AlphabetCardItem item,
                @NonNull Set<Integer> flippedPositions,
                @NonNull OnAlphabetActionListener listener
        ) {
            if (showBack) {
                flippedPositions.add(item.getIndex());
            } else {
                flippedPositions.remove(item.getIndex());
            }
            llCardFront.setVisibility(showBack ? View.GONE : View.VISIBLE);
            llCardBack.setVisibility(showBack ? View.VISIBLE : View.GONE);
            if (areAnimationsEnabled(itemView.getContext())) {
                cardFlashcard.setRotationY(showBack ? -75f : 75f);
                cardFlashcard.animate()
                        .rotationY(0f)
                        .setDuration(220)
                        .start();
            }
            listener.onCardFlipped(item, showBack);
        }

        private CharSequence buildHighlightedWord(String word, String lowerLetter, int highlightColor) {
            SpannableString spannable = new SpannableString(word);
            Locale viLocale = Locale.forLanguageTag("vi-VN");
            String normalizedWord = AlphabetContent.stripVietnameseToneMarksOnly(word).toLowerCase(viLocale);
            String target = AlphabetContent.stripVietnameseToneMarksOnly(lowerLetter).toLowerCase(viLocale);

            int fromIndex = 0;
            while (fromIndex < normalizedWord.length()) {
                int matchIdx = normalizedWord.indexOf(target, fromIndex);
                if (matchIdx < 0) {
                    break;
                }
                int endIdx = Math.min(word.length(), matchIdx + target.length());
                spannable.setSpan(
                        new ForegroundColorSpan(highlightColor),
                        matchIdx,
                        endIdx,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                fromIndex = matchIdx + Math.max(1, target.length());
            }
            return spannable;
        }

        private void playPulseAnimation(View target) {
            if (!areAnimationsEnabled(target.getContext())) {
                return;
            }
            target.animate()
                    .scaleX(1.06f)
                    .scaleY(1.06f)
                    .setDuration(100)
                    .withEndAction(() -> target.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(100)
                            .start())
                    .start();
        }

        private boolean areAnimationsEnabled(Context context) {
            try {
                float scale = Settings.Global.getFloat(
                        context.getContentResolver(),
                        Settings.Global.ANIMATOR_DURATION_SCALE,
                        1.0f
                );
                return scale > 0.0f;
            } catch (Exception e) {
                return true;
            }
        }
    }
}
