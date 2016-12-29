package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.utils.CircleTransform;

/**
 * Created by zsj on 2016/10/18.
 */

public class MusicViewProvider extends ViewProvider<Music, MusicViewProvider.Holder> {

    private boolean isPlus = false;

    public MusicViewProvider(@NonNull DataService dataService) {
        super(dataService);
    }

    @NonNull @Override
    protected Holder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                        @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_music, parent, false);
        return new Holder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, @NonNull Music musicItem) {
        Holder holder = (Holder) cardHolder;
        cards = musicItem.cards;
        Card card = musicItem.card;
        multiTypeAdapter = musicItem.adapter;
        holder.index = musicItem.index;
        holder.card = card;
        Glide.with(holder.cover.getContext())
                .load(card.cover)
                .transform(new CircleTransform(holder.cover.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.cover);
        holder.musicTitle.setText(card.title);
        holder.singer.setText(card.authorName);
        bindCommonData(card, holder, musicItem.showPlus);

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

        LinearLayout musicContent;
        ImageView cover;
        TextView musicTitle;
        TextView singer;
        int index;
        Card card;

        Holder(View itemView) {
            super(itemView);
            cover = (ImageView) itemView.findViewById(R.id.cover);
            musicTitle = (TextView) itemView.findViewById(R.id.music_title);
            singer = (TextView) itemView.findViewById(R.id.singer);
            plus.setOnClickListener(this);
            musicContent = (LinearLayout) itemView.findViewById(R.id.music_content);
            musicContent.setOnClickListener(this);
            more.setOnClickListener(this);

            more.setOnLongClickListener((view) -> more.performClick());
        }

        @Override
        public void onClick(View v)  {
            onActionClick(v, MusicViewProvider.this, card, index, "music");
        }
    }
}
