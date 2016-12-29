package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Web;
import ooo.oxo.excited.widget.RatioImageView;

/**
 * Created by zsj on 2016/10/18.
 */

public class WebViewProvider extends ViewProvider<Web, WebViewProvider.Holder> {

    private boolean isPlus = false;

    public WebViewProvider(@NonNull DataService dataService) {
        super(dataService);
    }

    @NonNull
    @Override
    protected Holder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_web, parent, false);
        return new Holder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull CardHolder cardHolder, @NonNull Web webItem) {
        Holder holder = (Holder) cardHolder;
        cards = webItem.cards;
        Card card = webItem.card;
        holder.card = card;
        multiTypeAdapter = webItem.adapter;
        holder.index = webItem.index;

        showTitleAndDesc(holder, card);
        bindCommonData(card, holder, webItem.showPlus);

        if (!isPlus) {
            holder.springView.setWidth((float) card.remains / (float) card.sum);
        } else {
            holder.springView.startSpring(
                    (float) card.remains / (float) card.sum);
            isPlus = false;
        }

        holder.webCover.setOriginalSize(16, 9);
        if (card.cover != null) {
            if (TextUtils.isEmpty(card.cover)) {
                holder.webCover.setVisibility(View.GONE);
            } else {
                holder.webCover.setVisibility(View.VISIBLE);
                Glide.with(holder.webCover.getContext())
                        .load(card.cover)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.webCover);
            }
        } else {
            holder.webCover.setVisibility(View.GONE);
        }
    }

    @Override
    public void updateCard(Card card, int index) {
        isPlus = true;
        updateItemCard(card, index);
    }

    private void showTitleAndDesc(Holder holder, Card card) {
        if (card == null)
            return;

        if (TextUtils.isEmpty(card.title)) {
            holder.webTitle.setVisibility(View.GONE);
        } else {
            holder.webTitle.setVisibility(View.VISIBLE);
            holder.webTitle.setText(card.title);
        }

        if (TextUtils.isEmpty(card.description)) {
            holder.webDesc.setVisibility(View.GONE);
        } else {
            holder.webDesc.setVisibility(View.VISIBLE);
            holder.webDesc.setText(card.description);
        }
    }

    class Holder extends CardHolder implements View.OnClickListener {

        LinearLayout webContent;
        RatioImageView webCover;
        TextView webTitle;
        TextView webDesc;
        int index;
        Card card;

        public Holder(View itemView) {
            super(itemView);
            webCover = (RatioImageView) itemView.findViewById(R.id.web_cover);
            webTitle = (TextView) itemView.findViewById(R.id.web_title);
            webDesc = (TextView) itemView.findViewById(R.id.web_desc);
            webContent = (LinearLayout) itemView.findViewById(R.id.web_content);
            plus.setOnClickListener(this);
            webContent.setOnClickListener(this);
            more.setOnClickListener(this);

            more.setOnLongClickListener((view) -> more.performClick());
        }

        @Override
        public void onClick(View v) {
            onActionClick(v, WebViewProvider.this, card, index, "web");
        }
    }
}
