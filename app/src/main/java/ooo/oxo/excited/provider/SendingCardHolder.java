package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.drakeet.multitype.ItemViewProvider;
import ooo.oxo.excited.R;
import ooo.oxo.excited.provider.item.SendingCard;

/**
 * 分享链接进度item
 * Created by seasonyuu on 2016/12/20.
 */

public class SendingCardHolder extends ItemViewProvider<SendingCard, RecyclerView.ViewHolder> {
    @NonNull
    @Override
    protected RecyclerView.ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new Holder(inflater.inflate(R.layout.item_sending_card, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull SendingCard sendingCard) {
        Holder holder = (Holder) viewHolder;
        switch (sendingCard.type) {
            case SendingCard.MUSIC:
                holder.textView.setText(R.string.sending_music_card);
                break;
            case SendingCard.IMAGE:
                holder.textView.setText(R.string.sending_image_card);
                break;
            case SendingCard.WEB:
                holder.textView.setText(R.string.sending_web_card);
                break;
        }
    }

    private class Holder extends RecyclerView.ViewHolder {
        TextView textView;

        Holder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.sending_card_type);
        }
    }
}
