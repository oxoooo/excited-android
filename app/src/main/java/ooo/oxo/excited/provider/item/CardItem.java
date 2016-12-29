package ooo.oxo.excited.provider.item;

import android.support.annotation.NonNull;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.model.Data;

/**
 * Created by zsj on 2016/10/19.
 */

public class CardItem {

    public Data data;
    public int index;
    public MultiTypeAdapter adapter;
    public boolean showPlus;

    public Card card;
    public List<Card> cards;

    CardItem(@NonNull Card card, int index, @NonNull MultiTypeAdapter adapter,
             @NonNull List<Card> cards, boolean showPlus) {
        this.card = card;
        this.index = index;
        this.adapter = adapter;
        this.cards = cards;
        this.showPlus = showPlus;
    }

}
