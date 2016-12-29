package ooo.oxo.excited.data;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.model.Card;

/**
 * @author zsj
 */

public interface IData {

    void addData(MultiTypeAdapter adapter, List<Card> cards);

    interface IItems extends IData {

        Items items();

        /**
         * 是否显示点赞
         * @return true 为显示， false 为不显示
         */
        boolean showPlus();

    }

}
