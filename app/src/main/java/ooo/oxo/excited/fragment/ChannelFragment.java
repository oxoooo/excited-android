package ooo.oxo.excited.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import java.util.ArrayList;
import java.util.List;

import ooo.oxo.excited.ChannelActivity;
import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.R;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.channel.ChannelAdapter;
import ooo.oxo.excited.fragment.callback.AppBarCallback;
import ooo.oxo.excited.model.Channel;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.ScreenUtils;
import ooo.oxo.excited.utils.ToastUtils;

public class ChannelFragment extends BaseFragment implements ChannelAdapter.OnChannelClickListener {

    public static final String CHANNEL = "channel";
    public static final String TAG = "Channel";
    public final int REQUEST_CHANNEL = 0x1;

    private SwipeRefreshLayout refreshLayout;
    private RecyclerView list;
    private View dividerView;
    private View convertView;

    private ChannelAdapter adapter;
    private QueryAPI queryAPI;
    private List<Channel> channels = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (appBarCallback != null) {
            appBarCallback.setExpanded(true, true);
        }
        if (convertView == null) {
            View view = inflater.inflate(R.layout.layout_channel, container, false);
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
            list = (RecyclerView) view.findViewById(R.id.recycler_channel);
            dividerView = view.findViewById(R.id.divider);

            adapter = new ChannelAdapter(context, channels);
            adapter.setOnChannelClickListener(this);
            list.setAdapter(adapter);

            int offset = getResources().getDimensionPixelOffset(R.dimen.default_toolbar_height);
            refreshLayout.setProgressViewOffset(true, -offset / 4, offset / 2);

            if (context instanceof AppBarCallback) {
                AppBarLayout appBarLayout = ((AppBarCallback) context).getAppBar();
                appBarLayout.addOnOffsetChangedListener(
                        (appBarLayout1, verticalOffset) -> {
                            dividerView.setPivotX(0);
                            if (verticalOffset > -200)
                                dividerView.setScaleX(1f * (200 + verticalOffset) / 200);
                            else
                                dividerView.setScaleX(0);
                        });
            }

            convertView = view;
        }
        if (containerCallback != null)
            list.setPadding(0, list.getPaddingTop(),
                    0, containerCallback.getWindowInsetsRect().bottom);
        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        queryAPI = ExcitedRetrofitFactory.getRetrofit(context).createApi(QueryAPI.class);

        refreshLayout.setOnRefreshListener(() -> loadChannels(true));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ScreenUtils.getHeight(context) > ScreenUtils.getWidth(context)) {
            if (containerCallback != null) {
                ViewGroup container = containerCallback.getContainer();
                container.getViewTreeObserver().addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (container.getHeight() > 0) {
                            int top = context.findViewById(R.id.appbar).getHeight();
                            int height = context.findViewById(R.id.bottomBar).getHeight();
                            int target = ScreenUtils.getHeight(context) - top - height;
                            adapter.setHeight(target);
                            container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }
                    }
                });
            }
        }

        if (adapter.getItemCount() == 0 && !refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
            loadChannels();
        }
    }

    private void loadChannels() {
        loadChannels(false);
    }

    private void loadChannels(boolean clean) {
        queryAPI.getData(QueryField.queryChannel(true))
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .doOnNext(channels -> {
                    if (clean) this.channels.clear();
                })
                .map(newData -> newData.channels)
                .doAfterTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(channels -> {
                    this.channels.addAll(channels);
                    adapter.notifyDataSetChanged();
                }, throwable -> {
                    ToastUtils.shorts(context, throwable.getMessage());
                });
    }

    @Override
    public void onChannelClick(Channel channel) {
        Intent intent = new Intent(context, ChannelActivity.class);
        intent.putExtra(CHANNEL, channel);
        startActivityForResult(intent, REQUEST_CHANNEL);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHANNEL) {
            if (data != null) {
                Channel channel = data.getParcelableExtra("channel");
                for (Channel c : channels) {
                    if (channel.id.equals(c.id)) {
                        c.followed = channel.followed;
                        break;
                    }
                }
                adapter.notifyDataSetChanged();
            }
        }
    }
}
