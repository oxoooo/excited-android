package ooo.oxo.excited.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.R;
import ooo.oxo.excited.Register;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.data.DataObserver;
import ooo.oxo.excited.data.IData;
import ooo.oxo.excited.fragment.callback.Callback;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.MusicViewProvider;
import ooo.oxo.excited.provider.PictureViewProvider;
import ooo.oxo.excited.provider.VideoViewProvider;
import ooo.oxo.excited.provider.WebViewProvider;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.provider.item.Web;
import ooo.oxo.excited.provider.listener.OnItemClickListener;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.NoItemAnimator;
import ooo.oxo.excited.utils.SnackbarUtils;

public class ExcitedFragment extends BaseFragment implements OnItemClickListener, IData.IItems {

    public static final String TAG = "Excited";

    private RecyclerView list;
    private SwipeRefreshLayout refreshLayout;
    private View convertView;

    private MultiTypeAdapter adapter;
    private Items items = new Items();
    private IItems iItems;

    private QueryAPI queryAPI;
    private Callback callback;


    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iItems = new DataObserver(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (appBarCallback != null) {
            appBarCallback.setExpanded(false, false);
        }
        if (convertView == null) {
            View view = inflater.inflate(R.layout.excited_fragment, container, false);
            list = (RecyclerView) view.findViewById(R.id.list);
            refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
            refreshLayout.setOnRefreshListener(() -> loadData(true));
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

        if (adapter == null) {
            adapter = new MultiTypeAdapter(items);

            Register.registerItem(adapter, new DataService(context));
            list.setItemAnimator(new NoItemAnimator());
            list.setAdapter(adapter);

            setListener();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter.getItemCount() == 0 && !refreshLayout.isRefreshing()) {
            refreshLayout.setRefreshing(true);
            loadData();
        }
    }

    private void loadData() {
        loadData(false);
    }

    private void loadData(final boolean clean) {
        queryAPI.getData(QueryField.timeline())
                .compose(bindToLifecycle())
                .filter(newData -> newData != null)
                .doOnNext(newData -> {
                    if (clean) items.clear();
                })
                .map(newData -> newData.timeline)
                .compose(RxMainThread.mainThread())
                .doAfterTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(cards -> addData(adapter, cards), throwable -> {
                    throwable.printStackTrace();
                    if (containerCallback != null) {
                        SnackbarUtils.longs(refreshLayout, throwable.getMessage(),
                                containerCallback.getWindowInsetsRect());
                    } else {
                        SnackbarUtils.longs(refreshLayout, throwable.getMessage(), null);
                    }
                });
    }

    @Override
    public void addData(MultiTypeAdapter adapter, List<Card> cards) {
        iItems.addData(adapter, cards);
    }

    @Override
    public Items items() {
        return items;
    }

    @Override
    public boolean showPlus() {
        return true;
    }

    private void setListener() {
        MusicViewProvider musicProvider = adapter.getProviderByClass(Music.class);
        WebViewProvider webProvider = adapter.getProviderByClass(Web.class);
        VideoViewProvider videoProvider = adapter.getProviderByClass(Video.class);
        PictureViewProvider pictureProvider = adapter.getProviderByClass(Picture.class);
        musicProvider.setOnItemClickListener(this);
        webProvider.setOnItemClickListener(this);
        videoProvider.setOnItemClickListener(this);
        pictureProvider.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(Card card, String tag) {
        if (callback != null) callback.callback(card, tag);
    }

}
