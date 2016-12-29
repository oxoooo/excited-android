package ooo.oxo.excited.provider;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.listener.OnItemClickListener;
import ooo.oxo.excited.utils.RelativeDateFormat;

/**
 * Created by zsj on 2016/10/21.
 */

public abstract class ViewProvider<C, H extends CardHolder> extends
        ItemViewProvider<C, CardHolder> {

    private DataService delegate;

    List<Card> cards;
    MultiTypeAdapter multiTypeAdapter;
    private OnItemClickListener onItemClickListener;

    public ViewProvider(@NonNull DataService dataService) {
        this.delegate = dataService;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public abstract void updateCard(Card card, int index);

    void updateItemCard(Card card, int index) {
        if (card == null || (cards.get(index).remains == card.remains &&
                cards.get(index).sum == card.sum)) {
            //卡片续命数未变化，后期考虑是否做处理
        } else {
            cards.get(index).remains = card.remains;
            cards.get(index).sum = card.sum;
        }
        multiTypeAdapter.notifyItemChanged(index);
    }

    @SuppressLint("DefaultLocale")
    void bindCommonData(Card card, CardHolder holder, boolean showPlus) {
        holder.category.setText(card.headName);
        holder.time.setText(card.distance);
        holder.remain.setText(String.format("%ds", card.remains));
        holder.sum.setText(String.format("/%d", card.sum));
        holder.plus.clearAnimation();
        Glide.with(holder.iconCategory.getContext())
                .load(card.headIcon)
                .into(holder.iconCategory);
        if (showPlus) {
            holder.plusContainer.setVisibility(View.VISIBLE);
        } else {
            holder.plusContainer.setVisibility(View.GONE);
        }
    }

    void onActionClick(View view, ViewProvider provider, Card card, int index, String tag) {
        switch (view.getId()) {
            case R.id.plus:
                AlphaAnimation animation = new AlphaAnimation(1f, 0.2f);
                animation.setRepeatMode(Animation.REVERSE);
                animation.setRepeatCount(Animation.INFINITE);
                animation.setDuration(400);
                view.startAnimation(animation);
                delegate.vote(provider, card.id, index);
                break;
            case R.id.web_content:
            case R.id.movie_content:
            case R.id.picture:
                delegate.fly(card, tag);
                break;
            case R.id.music_content:
                if (onItemClickListener != null)
                    onItemClickListener.onItemClick(card, tag);
                break;
            case R.id.more:
                delegate.inflateMenu(view, card.id);
                break;
        }
    }

}
