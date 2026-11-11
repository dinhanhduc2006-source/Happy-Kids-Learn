package com.example.happykidlearn.member2.dashboard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.happykidlearn.R;
import com.example.happykidlearn.member2.common.Member2ContentCatalog;
import com.example.happykidlearn.member2.common.Member2SoundEffects;
import com.example.happykidlearn.models.GameModel;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class GameGridAdapter extends RecyclerView.Adapter<GameGridAdapter.GameViewHolder> {

    public interface OnGameCardClickListener {
        void onGameCardClicked(HomeGameUiModel item);
    }

    private final List<HomeGameUiModel> items = new ArrayList<>();
    private final OnGameCardClickListener clickListener;

    public GameGridAdapter(@NonNull OnGameCardClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void submitList(@NonNull List<HomeGameUiModel> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public GameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.m2_item_home_game_card, parent, false);
        return new GameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameViewHolder holder, int position) {
        holder.bind(items.get(position), clickListener);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class GameViewHolder extends RecyclerView.ViewHolder {

        private final MaterialCardView cardRoot;
        private final View flIconBg;
        private final ImageView ivIcon;
        private final TextView tvStatus;
        private final TextView tvCategory;
        private final TextView tvTitle;
        private final TextView tvDescription;
        private final ProgressBar pbProgress;
        private final TextView tvProgressText;
        private final TextView tvStars;

        GameViewHolder(@NonNull View itemView) {
            super(itemView);
            cardRoot = itemView.findViewById(R.id.card_m2_game_root);
            flIconBg = itemView.findViewById(R.id.fl_m2_card_icon_bg);
            ivIcon = itemView.findViewById(R.id.iv_m2_card_icon);
            tvStatus = itemView.findViewById(R.id.tv_m2_card_status);
            tvCategory = itemView.findViewById(R.id.tv_m2_card_category);
            tvTitle = itemView.findViewById(R.id.tv_m2_card_title);
            tvDescription = itemView.findViewById(R.id.tv_m2_card_description);
            pbProgress = itemView.findViewById(R.id.pb_m2_card_progress);
            tvProgressText = itemView.findViewById(R.id.tv_m2_card_progress_text);
            tvStars = itemView.findViewById(R.id.tv_m2_card_stars);
        }

        void bind(@NonNull HomeGameUiModel model, @NonNull OnGameCardClickListener listener) {
            Context context = itemView.getContext();
            GameModel game = model.getGameModel();

            int coverColor = ContextCompat.getColor(context, model.getAccentColorResId());
            cardRoot.setCardBackgroundColor(coverColor);

            int coverArtRes = Member2ContentCatalog.resolveBookCoverIllustration(game);
            ivIcon.setImageResource(coverArtRes);
            ivIcon.setContentDescription(context.getString(R.string.m2_cd_game_icon_format, game.getTitle()));

            String displayCategory = Member2ContentCatalog.resolveCategoryDisplayLabel(game.getCategory());
            tvCategory.setText(displayCategory);
            tvCategory.setTextColor(coverColor);
            tvTitle.setText(game.getTitle());
            tvDescription.setText(game.getDescription());

            if (model.isReadyToPlay()) {
                tvStatus.setText(R.string.m2_game_status_ready);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.m2_badge_ready_text));
                tvStatus.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.m2_badge_ready_bg)
                ));
                cardRoot.setAlpha(1.0f);
            } else {
                tvStatus.setText(R.string.m2_game_status_coming_soon);
                tvStatus.setTextColor(ContextCompat.getColor(context, R.color.m2_badge_soon_text));
                tvStatus.setBackgroundTintList(ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.m2_badge_soon_bg)
                ));
                cardRoot.setAlpha(0.95f);
            }

            pbProgress.setProgressTintList(ColorStateList.valueOf(coverColor));
            int targetPercent = model.getProgressPercent();
            if (pbProgress.getProgress() != targetPercent) {
                ObjectAnimator progressAnim = ObjectAnimator.ofInt(
                        pbProgress, "progress", pbProgress.getProgress(), targetPercent
                );
                progressAnim.setDuration(320);
                progressAnim.setInterpolator(new DecelerateInterpolator());
                progressAnim.start();
            } else {
                pbProgress.setProgress(targetPercent);
            }

            if (model.hasKnownTotal()) {
                tvProgressText.setText(context.getString(
                        R.string.m2_game_progress_format,
                        model.getCompletedLevels(),
                        model.getTotalLevels()
                ));
            } else {
                tvProgressText.setText(R.string.m2_game_progress_no_data);
            }

            tvStars.setText(String.valueOf(model.getStarsEarned()));

            cardRoot.setOnClickListener(v -> {
                if (model.isReadyToPlay()) {
                    Member2SoundEffects.playOpenBook(context);
                } else {
                    Member2SoundEffects.playGentleTry(context);
                }
                v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(75)
                        .withEndAction(() -> v.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(85)
                                .withEndAction(() -> listener.onGameCardClicked(model))
                                .start())
                        .start();
            });
        }
    }
}
