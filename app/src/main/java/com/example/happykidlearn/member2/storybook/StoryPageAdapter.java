package com.example.happykidlearn.member2.storybook;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happykidlearn.R;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class StoryPageAdapter extends RecyclerView.Adapter<StoryPageAdapter.PageViewHolder> {

    public interface OnPageIllustrationClickListener {
        void onIllustrationClicked(int pageIndex, StoryPage page);
    }

    public interface OnStoryQuestionAnswerListener {
        void onQuestionAnswered(boolean isCorrect);
    }

    private final StoryContent.StoryBook storyBook;
    private final OnPageIllustrationClickListener clickListener;
    private final OnStoryQuestionAnswerListener questionAnswerListener;

    private int highlightedPageIndex = -1;
    private int highlightStartChar = -1;
    private int highlightEndChar = -1;

    public StoryPageAdapter(
            @NonNull StoryContent.StoryBook storyBook,
            @NonNull OnPageIllustrationClickListener clickListener
    ) {
        this(storyBook, clickListener, null);
    }

    public StoryPageAdapter(
            @NonNull StoryContent.StoryBook storyBook,
            @NonNull OnPageIllustrationClickListener clickListener,
            @Nullable OnStoryQuestionAnswerListener questionAnswerListener
    ) {
        this.storyBook = storyBook;
        this.clickListener = clickListener;
        this.questionAnswerListener = questionAnswerListener;
    }

    public void updateHighlight(int pageIndex, int startChar, int endChar) {
        int prevPage = highlightedPageIndex;
        highlightedPageIndex = pageIndex;
        highlightStartChar = startChar;
        highlightEndChar = endChar;

        if (prevPage >= 0 && prevPage < getItemCount() && prevPage != pageIndex) {
            notifyItemChanged(prevPage);
        }
        if (pageIndex >= 0 && pageIndex < getItemCount()) {
            notifyItemChanged(pageIndex);
        }
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.m2_item_story_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        List<StoryPage> pages = storyBook.getPages();
        StoryPage page = pages.get(position);
        boolean isLastPage = (position == pages.size() - 1);
        int start = (position == highlightedPageIndex) ? highlightStartChar : -1;
        int end = (position == highlightedPageIndex) ? highlightEndChar : -1;
        holder.bind(storyBook, page, isLastPage, start, end, clickListener, questionAnswerListener);
    }

    @Override
    public int getItemCount() {
        return storyBook.getPages().size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardPage;
        private final ImageView ivIllust;
        private final TextView tvCaption;
        private final TextView tvPageText;
        private final TextView tvMoral;
        private final View layoutQuizBox;
        private final TextView tvQuestion;
        private final Button btnAnsCorrect;
        private final Button btnAnsWrong;

        PageViewHolder(@NonNull View itemView) {
            super(itemView);
            cardPage = itemView.findViewById(R.id.card_m2_story_page);
            ivIllust = itemView.findViewById(R.id.iv_m2_story_page_illust);
            tvCaption = itemView.findViewById(R.id.tv_m2_story_page_caption);
            tvPageText = itemView.findViewById(R.id.tv_m2_story_page_text);
            tvMoral = itemView.findViewById(R.id.tv_m2_story_page_moral);
            layoutQuizBox = itemView.findViewById(R.id.layout_m2_story_page_quiz_box);
            tvQuestion = itemView.findViewById(R.id.tv_m2_story_page_question);
            btnAnsCorrect = itemView.findViewById(R.id.btn_m2_story_ans_correct);
            btnAnsWrong = itemView.findViewById(R.id.btn_m2_story_ans_wrong);
        }

        void bind(
                @NonNull StoryContent.StoryBook story,
                @NonNull StoryPage page,
                boolean isLastPage,
                int highlightStart,
                int highlightEnd,
                @NonNull OnPageIllustrationClickListener listener,
                @Nullable OnStoryQuestionAnswerListener questionListener
        ) {
            Context context = itemView.getContext();

            ivIllust.setImageResource(page.getIllustrationResId());
            ivIllust.setContentDescription(page.getSceneCaption());
            tvCaption.setText("👆 " + page.getSceneCaption());

            String fullText = page.getPageText();
            SpannableString spannable = new SpannableString(fullText);
            if (highlightStart >= 0 && highlightEnd > highlightStart && highlightEnd <= fullText.length()) {
                int bgHighlight = ContextCompat.getColor(context, R.color.m2_accent_yellow_light);
                int fgHighlight = ContextCompat.getColor(context, R.color.m2_accent_purple);
                spannable.setSpan(
                        new BackgroundColorSpan(bgHighlight),
                        highlightStart,
                        highlightEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
                spannable.setSpan(
                        new ForegroundColorSpan(fgHighlight),
                        highlightStart,
                        highlightEnd,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }
            tvPageText.setText(spannable);

            if (isLastPage) {
                tvMoral.setVisibility(View.VISIBLE);
                tvMoral.setText(context.getString(R.string.m2_storybook_moral_prefix, story.getMoralLesson()));
                if (layoutQuizBox != null) {
                    layoutQuizBox.setVisibility(View.VISIBLE);
                    tvQuestion.setText(story.getMiniQuestion());
                    btnAnsCorrect.setText(story.getCorrectAnswer());
                    btnAnsWrong.setText(story.getWrongAnswer());

                    btnAnsCorrect.setOnClickListener(v -> {
                        Member2SoundEffects.playCorrect(context);
                        tvQuestion.setText("🌟 Chính xác! Bé nhớ bài học câu chuyện giỏi quá!");
                        if (questionListener != null) {
                            questionListener.onQuestionAnswered(true);
                        }
                    });
                    btnAnsWrong.setOnClickListener(v -> {
                        Member2SoundEffects.playGentleTry(context);
                        tvQuestion.setText("💡 Bé chọn đáp án việc tốt ở nút màu xanh nhé!");
                        if (questionListener != null) {
                            questionListener.onQuestionAnswered(false);
                        }
                    });
                }
            } else {
                tvMoral.setVisibility(View.GONE);
                if (layoutQuizBox != null) {
                    layoutQuizBox.setVisibility(View.GONE);
                }
            }

            ivIllust.setOnClickListener(v -> {
                v.animate()
                        .scaleX(1.08f)
                        .scaleY(1.08f)
                        .setDuration(100)
                        .withEndAction(() -> v.animate().scaleX(1f).scaleY(1f).setDuration(100).start())
                        .start();
                listener.onIllustrationClicked(page.getPageIndex(), page);
            });
        }
    }
}
