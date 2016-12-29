package ooo.oxo.excited.fragment;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import me.drakeet.multitype.Items;
import me.drakeet.multitype.MultiTypeAdapter;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.LoginActivity;
import ooo.oxo.excited.LoginManager;
import ooo.oxo.excited.R;
import ooo.oxo.excited.Register;
import ooo.oxo.excited.api.ImageAPI;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.data.DataObserver;
import ooo.oxo.excited.data.IData;
import ooo.oxo.excited.database.CardConverter;
import ooo.oxo.excited.database.FavSQLiteHelper;
import ooo.oxo.excited.database.QuerySQL;
import ooo.oxo.excited.fragment.callback.Callback;
import ooo.oxo.excited.model.AddCard;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.model.ImageResult;
import ooo.oxo.excited.provider.MusicViewProvider;
import ooo.oxo.excited.provider.PictureViewProvider;
import ooo.oxo.excited.provider.VideoViewProvider;
import ooo.oxo.excited.provider.WebViewProvider;
import ooo.oxo.excited.provider.data.DataService;
import ooo.oxo.excited.provider.item.Music;
import ooo.oxo.excited.provider.item.Picture;
import ooo.oxo.excited.provider.item.SendingCard;
import ooo.oxo.excited.provider.item.Video;
import ooo.oxo.excited.provider.item.Web;
import ooo.oxo.excited.provider.listener.OnItemClickListener;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.APIResponse;
import ooo.oxo.excited.utils.BitmapUtils;
import ooo.oxo.excited.utils.PreferenceManager;
import ooo.oxo.excited.utils.SnackbarUtils;
import ooo.oxo.excited.utils.ToastUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static ooo.oxo.excited.LoginActivity.ID;
import static ooo.oxo.excited.LoginActivity.TOKEN;


public class FavoritesFragment extends BaseFragment
        implements OnItemClickListener, View.OnClickListener, IData.IItems {

    public static final String TAG = "Favorites";

    private RecyclerView list;
    private SwipeRefreshLayout refreshLayout;
    private MultiTypeAdapter adapter;
    private ViewStub emptyDataView;
    private View convertView;

    private QueryAPI queryAPI;
    private Items items = new Items();
    private Context context;
    private IItems iItems;
    private Callback callback;
    private boolean isLogin;
    private BriteDatabase db;
    private SendingCard sendingCard;


    public void registerCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iItems = new DataObserver(this);
        queryAPI = ExcitedRetrofitFactory.getRetrofit(context).createApi(QueryAPI.class);
        FavSQLiteHelper favSQLiteHelper = new FavSQLiteHelper(context);
        SqlBrite sqlBrite = new SqlBrite.Builder().build();
        db = sqlBrite.wrapDatabaseHelper(favSQLiteHelper, Schedulers.io());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        isLogin = LoginManager.checkLogin(context);
        if (convertView == null) {
            if (!isLogin) {
                View view = inflater.inflate(R.layout.layout_favorites_unlogin, container, false);
                Button login = (Button) view.findViewById(R.id.login);
                login.setOnClickListener(this);
                if (appBarCallback != null) {
                    appBarCallback.setExpanded(true, false);
                }
                convertView = view;
            } else {
                View view = inflater.inflate(R.layout.favorites_fragment, container, false);
                emptyDataView = (ViewStub) view.findViewById(R.id.empty_data_view);
                list = (RecyclerView) view.findViewById(R.id.list);
                refreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.refreshLayout);
                refreshLayout.setOnRefreshListener(() -> loadData(true));
                convertView = view;
            }
        } else {
            if (appBarCallback != null) {
                if (isLogin && (adapter != null && adapter.getItemCount() > 0)) {
                    appBarCallback.setExpanded(false, false);
                } else {
                    appBarCallback.setExpanded(true, false);
                }
            }
        }
        if (isLogin && containerCallback != null)
            list.setPadding(0, list.getPaddingTop(),
                    0, containerCallback.getWindowInsetsRect().bottom);
        return convertView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (isLogin) {
            if (getArguments() != null)
                sendingCard = getArguments().getParcelable("sending_card");
            if (sendingCard != null) {
                items.add(sendingCard);
                postCard(sendingCard);
            }
            if (adapter == null) {
                adapter = new MultiTypeAdapter(items);

                Register.registerItem(adapter, new DataService(context));
                list.setAdapter(adapter);

                setListener();
            }
        }
    }

    private void postCard(SendingCard sendingCard) {
        if (sendingCard == null)
            return;
        if (sendingCard.type == SendingCard.IMAGE) {
            ImageAPI imageAPI = ExcitedRetrofitFactory
                    .getRetrofit(getContext()).createApi(ImageAPI.class);
            File file = null;
            try {
                file = BitmapUtils.compress(sendingCard.url);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file == null) {
                handleError(getString(R.string.file_not_exits));
                return;
            }
            RequestBody requestFile =
                    RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            imageAPI.post(body)
                    .compose(bindToLifecycle())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(response -> {
                        if (response.isSuccessful()) {
                            ImageResult imageResult = APIResponse
                                    .response(getContext(), response);
                            if (imageResult != null) {
                                sendingCard.url = imageResult.url;
                                createImageCard(QueryField.addImageCard(sendingCard.url,
                                        sendingCard.description, sendingCard.channelId));
                            }
                        } else {
                            handleError(getString(R.string.upload_failed));
                        }
                    }, throwable -> {
                        handleError(throwable.getMessage());
                    });
        } else {
            createWebCard(QueryField.addWebCard(sendingCard.url,
                    sendingCard.title, sendingCard.description, sendingCard.channelId));
        }
    }

    private void createWebCard(String query) {
        queryAPI.getData(query)
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .map(newData -> newData.webCard)
                .subscribe(this::handleAddCard,
                        throwable -> handleError(throwable.getMessage()));

    }

    private void handleError(String errorMessage) {
        Snackbar snackbar = Snackbar.make(refreshLayout, errorMessage, Snackbar.LENGTH_LONG);
        if (containerCallback != null) {
            Rect rect = containerCallback.getWindowInsetsRect();
            snackbar.getView().setPadding(rect.left, 0, rect.right, rect.bottom);
        }
        snackbar.setAction(R.string.retry, (v -> postCard(sendingCard)));
        snackbar.setActionTextColor(ContextCompat.getColor(context, R.color.empty_text_color));
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                if (event != DISMISS_EVENT_ACTION) {
                    getArguments().putParcelable("sending_card", null);
                    sendingCard = null;
                    items.remove(0);
                    adapter.notifyItemRemoved(0);
                    if (items.size() == 0) {
                        inflateEmptyView();
                    }
                }
            }
        });
        snackbar.show();
    }

    private void handleAddCard(AddCard addCard) {
        if (addCard.notice != null) {
            handleError(addCard.notice.text);
        } else if (items.size() > 0 && items.get(0) instanceof SendingCard) {
            items.remove(0);
            List<Card> cards = new ArrayList<>();
            cards.add(addCard.card);
            sendingCard = null;
            insertIntoDatabase(cards);
            getArguments().putParcelable("sending_card", null);
            if (containerCallback != null)
                SnackbarUtils.shorts(refreshLayout, R.string.post_success,
                        containerCallback.getWindowInsetsRect());
            else
                SnackbarUtils.shorts(refreshLayout, R.string.post_success);
        }
    }

    private void createImageCard(String query) {
        queryAPI.getData(query)
                .compose(bindToLifecycle())
                .compose(RxMainThread.mainThread())
                .map(newData -> newData.imageCard)
                .subscribe(this::handleAddCard, throwable -> {
                    if (throwable instanceof SocketTimeoutException) {
                        ToastUtils.longs(context, R.string.post_error);
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (adapter != null && isLogin)
            if (items.size() > 0 && items.get(0) instanceof SendingCard) {
                refreshLayout.setRefreshing(false);
                loadDataFromDatabase();
            } else if (adapter.getItemCount() == 0 && !refreshLayout.isRefreshing()) {
                refreshLayout.setRefreshing(true);
                loadDataFromDatabase();
            }
    }

    private void loadDataFromDatabase() {
        Observable<SqlBrite.Query> observable = db.createQuery(FavSQLiteHelper.Fav.TABLE_NAME,
                QuerySQL.selectFavorite(PreferenceManager.getValue(getContext(), TOKEN),
                        PreferenceManager.getValue(getContext(), ID)));
        observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(query -> {
                    Cursor cursor = query.run();
                    if (refreshLayout.isRefreshing())
                        refreshLayout.setRefreshing(false);
                    if (cursor == null) {
                        loadData(true);
                    } else {
                        List<Card> cards = new ArrayList<>();
                        while (cursor.moveToNext()) {
                            cards.add(CardConverter.createCard(cursor));
                        }
                        if (cards.size() > 0) {
                            Iterator<Object> iterator = items.iterator();
                            while (iterator.hasNext()) {
                                if (!(iterator.next() instanceof SendingCard)) {
                                    iterator.remove();
                                }
                            }
                            addData(adapter, cards);
                            if (appBarCallback != null) {
                                appBarCallback.setExpanded(cards.size() == 0, false);
                            }
                        } else
                            loadData(true);
                        cursor.close();
                    }
                }, throwable -> {
                    if (refreshLayout.isRefreshing())
                        refreshLayout.setRefreshing(false);
                    throwable.printStackTrace();
                    ToastUtils.shorts(getContext(), throwable.getMessage());
                });
    }

    private void loadData(final boolean clean) {
        queryAPI.getData(QueryField.favorites())
                .compose(bindToLifecycle())
                .doOnNext(response -> {
                    if (clean) items.clear();
                })
                .compose(RxMainThread.mainThread())
                .map(newData -> {
                    if (newData != null) {
                        if (newData.user == null) {
                            boolean isLogin = checkLocalLoginState();
                            if (isLogin) {
                                clearLoginState();
                                inflateUnloginView();
                            }
                            return null;
                        }
                        return newData.user.cards;
                    }
                    return null;
                })
                .doOnNext(cards -> {
                    if (cards != null) {
                        if (cards.size() == 0 && items.size() == 0) {
                            inflateEmptyView();
                        }
                    }
                })
                .doAfterTerminate(() -> refreshLayout.setRefreshing(false))
                .subscribe(cards -> {
                    if (cards != null) insertIntoDatabase(cards);
                }, throwable -> ToastUtils.shorts(context, throwable.getMessage()));

    }

    private void insertIntoDatabase(List<Card> cards) {
        if (cards.size() == 0)
            return;

        String token = PreferenceManager.getValue(context, TOKEN);
        String id = PreferenceManager.getValue(context, ID);
        BriteDatabase.Transaction transaction = db.newTransaction();
        try {
            for (Card card : cards) {
                db.insert(FavSQLiteHelper.Fav.TABLE_NAME,
                        CardConverter.createContentValues(card, id, token),
                        SQLiteDatabase.CONFLICT_REPLACE);
            }
            transaction.markSuccessful();
        } finally {
            transaction.end();
        }
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
        return false;
    }

    private boolean checkLocalLoginState() {
        String token = PreferenceManager.getValue(context, TOKEN);
        return token != null;
    }

    private void clearLoginState() {
        PreferenceManager.putString(context, TOKEN, null);
    }

    private void inflateUnloginView() {
        emptyDataView.setLayoutResource(R.layout.layout_favorites_unlogin);
        View view = emptyDataView.inflate();
        Button login = (Button) view.findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    private void inflateEmptyView() {
        if (emptyDataView.getLayoutResource() != R.layout.empty_favorites_data_view) {
            emptyDataView.setLayoutResource(R.layout.empty_favorites_data_view);
            if (!emptyDataView.isShown())
                emptyDataView.inflate();
        }
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

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        if (db != null)
            db.close();
        super.onDestroy();
    }

}
