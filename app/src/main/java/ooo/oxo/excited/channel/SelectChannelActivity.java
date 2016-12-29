package ooo.oxo.excited.channel;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.R;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.model.Channel;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.view.MiuiStatusBarCompat;
import ooo.oxo.excited.widget.InsetsToolbar;


public class SelectChannelActivity extends RxAppCompatActivity
        implements ChannelAdapter.OnChannelClickListener {

    private ChannelAdapter adapter;
    private List<Channel> channels = new ArrayList<>();

    private QueryAPI queryAPI;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MiuiStatusBarCompat.enableLightStatusBar(getWindow());
        setContentView(R.layout.select_channel_activity);
        InsetsToolbar toolbar = (InsetsToolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(view -> finish());

        queryAPI = ExcitedRetrofitFactory.getRetrofit(this).createApi(QueryAPI.class);

        RecyclerView list = (RecyclerView) findViewById(R.id.recycler_channel);
        adapter = new ChannelAdapter(this, channels);
        adapter.setOnChannelClickListener(this);
        list.setAdapter(adapter);

        loadChannels();
    }

    private void loadChannels() {
        queryAPI.getData(QueryField.queryChannel(false))
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .map(newData -> newData.channels)
                .subscribe(channels -> {
                    this.channels.addAll(channels);
                    adapter.notifyDataSetChanged();
                }, throwable -> {
                    ToastUtils.shorts(this, throwable.getMessage());
                });
    }

    @Override
    public void onChannelClick(Channel channel) {
        Intent intent = new Intent();
        intent.putExtra("id", channel.id);
        intent.putExtra("name", channel.name);
        setResult(RESULT_OK, intent);
        finish();
    }

}
