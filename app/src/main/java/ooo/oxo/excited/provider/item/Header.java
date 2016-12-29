package ooo.oxo.excited.provider.item;


import android.support.annotation.NonNull;

import me.drakeet.multitype.Item;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.model.Channel;

public class Header implements Item {

    public Channel channel;
    public MultiTypeAdapter adapter;

    public Header(@NonNull Channel channel, @NonNull MultiTypeAdapter adapter) {
        this.channel = channel;
        this.adapter = adapter;
    }

}
