package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.widget.RatioImageView;

/**
 * Created by zsj on 2016/10/18.
 */

public class VideoViewProvider extends ViewProvider<Video, VideoViewProvider.Holder> {

    private boolean isPlus = false;

    public VideoViewProvider(@NonNull DataService dataService) {
        super(dataService);
    }

    @NonNull
    @Override
    protected Holder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_video, parent, false);
        return new Holder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, @NonNull Video videoItem) {
        Holder holder = (Holder) cardHolder;
        multiTypeAdapter = videoItem.adapter;
        cards = videoItem.cards;
        Card card = videoItem.card;
        holder.card = card;
        holder.index = videoItem.index;

        holder.videoCover.setOriginalSize(16, 9);
        Glide.with(holder.videoCover.getContext())
                .load(card.cover)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.videoCover);
        holder.videoTitle.setText(card.title);
        bindCommonData(card, holder, videoItem.showPlus);

        if (!isPlus) {
            holder.springView.setWidth((float) card.remains / (float) card.sum);
        } else {
            holder.springView.startSpring((float) card.remains / (float) card.sum);
            isPlus = false;
        }
    }

    @Override
    public void updateCard(Card card, int index) {
        isPlus = true;
        updateItemCard(card, index);
    }

    class Holder extends CardHolder implements View.OnClickListener {

        FrameLayout movieContent;
        RatioImageView videoCover;
        ImageButton play;
        TextView videoTitle;
        int index;
        Card card;

        Holder(View itemView) {
            super(itemView);
            videoCover = (RatioImageView) itemView.findViewById(R.id.video_cover);
            play = (ImageButton) itemView.findViewById(R.id.play);
            videoTitle = (TextView) itemView.findViewById(R.id.video_title);
            movieContent = (FrameLayout) itemView.findViewById(R.id.movie_content);
            movieContent.setOnClickListener(this);
            plus.setOnClickListener(this);
            more.setOnClickListener(this);

            more.setOnLongClickListener((view) -> more.performClick());
        }

        @Override
        public void onClick(View v) {
            onActionClick(v, VideoViewProvider.this, card, index, "video");
        }
    }
}
