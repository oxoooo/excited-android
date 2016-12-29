package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.widget.RatioImageView;

/**
 * Created by zsj on 2016/10/20.
 */

public class PictureViewProvider extends
        ViewProvider<Picture, PictureViewProvider.Holder> {

    private static final String RATIO_1_1 = "1:1";
    private static final String RATIO_16_9 = "16:9";
    private boolean isPlus = false;


    public PictureViewProvider(@NonNull DataService dataService) {
        super(dataService);
    }

    @NonNull @Override
    protected Holder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                        @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_picture, parent, false);
        return new Holder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, @NonNull Picture pictureItem) {
        Holder holder = (Holder) cardHolder;
        cards = pictureItem.cards;
        Card card = pictureItem.card;
        holder.card = card;
        multiTypeAdapter = pictureItem.adapter;
        holder.index = pictureItem.index;

        setImageDesc(holder, card);

        if (card.ratio.equals(RATIO_1_1)) {
            holder.picture.setOriginalSize(1, 1);
        } else if (card.ratio.equals(RATIO_16_9)){
            holder.picture.setOriginalSize(16, 9);
        }

        Glide.with(holder.picture.getContext())
                .load(card.cover)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.picture);
        bindCommonData(card, holder, pictureItem.showPlus);

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

    private void setImageDesc(Holder holder, Card card) {
        if (!TextUtils.isEmpty(card.description)) {
            holder.pictureDesc.setVisibility(View.VISIBLE);
            holder.pictureDesc.setText(card.description);
        } else {
            holder.pictureDesc.setVisibility(View.GONE);
        }
    }

    class Holder extends CardHolder implements View.OnClickListener {

        RatioImageView picture;
        TextView pictureDesc;
        int index;
        Card card;

        Holder(View itemView) {
            super(itemView);
            picture = (RatioImageView) itemView.findViewById(R.id.picture);
            pictureDesc = (TextView) itemView.findViewById(R.id.picture_desc);
            plus.setOnClickListener(this);
            picture.setOnClickListener(this);
            more.setOnClickListener(this);

            more.setOnLongClickListener((view) -> more.performClick());
        }

        @Override
        public void onClick(View v) {
            onActionClick(v, PictureViewProvider.this, card, index, "image");
        }
    }
}
