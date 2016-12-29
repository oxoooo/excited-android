package ooo.oxo.excited.data;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.item.EmptyChannel;
import ooo.oxo.excited.provider.item.Footer;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.provider.item.Web;

import static ooo.oxo.excited.MainActivity.COVER_WEB;
import static ooo.oxo.excited.MainActivity.MUSIC;
import static ooo.oxo.excited.MainActivity.PICTURE;
import static ooo.oxo.excited.MainActivity.VIDEO;
import static ooo.oxo.excited.MainActivity.WEB;

/**
 * @author zsj
 */

public class DataObserver implements IData.IItems {

    private IItems iItems;

    public DataObserver(IItems iItems) {
        this.iItems = iItems;
    }

    @Override
    public void addData(MultiTypeAdapter adapter, List<Card> cards) {
        Items items = items();
        for (int index = 0; index < cards.size(); index++) {
            final Card card = cards.get(index);
            if (card.type.equals(MUSIC)) {
                items.add(new Music(card, index, adapter, cards, showPlus()));
            } else if (card.type.equals(VIDEO)) {
                items.add(new Video(card, index, adapter, cards, showPlus()));
            } else if (card.type.equals(WEB)) {
                items.add(new Web(card, index, adapter, cards, showPlus()));
            } else if (card.type.equals(COVER_WEB)) {
                items.add(new Web(card, index, adapter, cards, showPlus()));
            } else if (card.type.equals(PICTURE)) {
                items.add(new Picture(card, index, adapter, cards, showPlus()));
            }
        }
        if (items.size() > 1)
            items.add(new Footer());
        else
            items.add(new EmptyChannel());
        adapter.notifyDataSetChanged();
    }

    @Override
    public Items items() {
        return iItems.items();
    }

    @Override
    public boolean showPlus() {
        return iItems.showPlus();
    }

}
