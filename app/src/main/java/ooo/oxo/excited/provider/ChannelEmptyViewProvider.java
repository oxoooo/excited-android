package ooo.oxo.excited.provider;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.drakeet.multitype.ItemViewProvider;
import ooo.oxo.excited.R;
import ooo.oxo.excited.provider.item.EmptyChannel;

/**
 * Created by seasonyuu on 2016/12/16.
 */

public class ChannelEmptyViewProvider extends
        ItemViewProvider<EmptyChannel, ChannelEmptyViewProvider.ViewHolder> {
    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        return new ViewHolder(inflater.inflate(R.layout.layout_channel_empty, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull EmptyChannel emptyChannel) {

    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
