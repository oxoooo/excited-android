package ooo.oxo.excited.provider.item;

import android.support.annotation.NonNull;

import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.model.Card;

/**
 * Created by zsj on 2016/10/18.
 */

public class Music extends CardItem {

    public Music(@NonNull Card card, int index, @NonNull MultiTypeAdapter adapter,
                 @NonNull List<Card> cards, boolean showPlus) {
        super(card, index, adapter, cards, showPlus);
    }

}
