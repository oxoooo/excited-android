package ooo.oxo.excited.provider;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.drakeet.multitype.ItemViewProvider;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.R;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.model.Channel;
import ooo.oxo.excited.provider.item.Header;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.ToastUtils;

public class HeaderViewProvider extends ItemViewProvider<Header, HeaderViewProvider.HeaderHolder> {


    @NonNull
    @Override
    protected HeaderHolder onCreateViewHolder(@NonNull LayoutInflater inflater,
                                              @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_channel_header, parent, false);
        return new HeaderHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull HeaderHolder holder, @NonNull Header header) {
        Channel channel = header.channel;
        holder.createTime.setText(holder.createTime.getContext().getString(R.string.create_at,
                channel.createdAt));
        holder.channelDesc.setText(channel.description);
        if (channel.followed) {
            holder.follow.setText(holder.follow.getContext().getString(R.string.unfollow));
        } else {
            holder.follow.setText(holder.follow.getContext().getString(R.string.follow));
        }

        holder.follow.setOnClickListener(view ->
                follow(holder, header.adapter, channel, channel.followed));
    }

    private void follow(HeaderHolder holder, MultiTypeAdapter adapter,
                        Channel channel, boolean followed) {
        Context context = holder.itemView.getContext();

        QueryAPI queryAPI = ExcitedRetrofitFactory.getRetrofit(context).createApi(QueryAPI.class);
        queryAPI.mutation(QueryField.setFollowState(channel.id, !followed))
                .compose(RxMainThread.mainThread())
                .map(newData -> newData.followState)
                .subscribe(followState -> {
                    boolean currentFollowed = followState.channel.followed;
                    holder.follow.setText(currentFollowed ? context.getString(R.string.unfollow)
                            : context.getString(R.string.follow)
                    );
                    ToastUtils.shorts(context, currentFollowed ?
                            R.string.follow_success : R.string.unfollow_success);
                    channel.followed = currentFollowed;
                    adapter.notifyItemChanged(0);
                }, throwable -> {});

    }

    static class HeaderHolder extends RecyclerView.ViewHolder {

        TextView createTime;
        TextView channelDesc;
        TextView follow;

        public HeaderHolder(View itemView) {
            super(itemView);
            createTime = (TextView) itemView.findViewById(R.id.create_time);
            channelDesc = (TextView) itemView.findViewById(R.id.channel_desc);
            follow = (TextView) itemView.findViewById(R.id.follow);
        }
    }
}
