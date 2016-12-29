package ooo.oxo.excited.provider.data;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupMenu;

import ooo.oxo.excited.BrowserActivity;
import ooo.oxo.excited.ExcitedRetrofitFactory;
import ooo.oxo.excited.ImageActivity;
import ooo.oxo.excited.R;
import ooo.oxo.excited.api.CardApi;
import ooo.oxo.excited.api.QueryAPI;
import ooo.oxo.excited.api.QueryField;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.ViewProvider;
import ooo.oxo.excited.rx.RxMainThread;
import ooo.oxo.excited.utils.ToastUtils;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static ooo.oxo.excited.MainActivity.PICTURE;
import static ooo.oxo.excited.MainActivity.WEB;


public class DataService {

    private Context context;

    public DataService(@NonNull Context context) {
        this.context = context;
    }

    public void inflateMenu(View actionView, String itemId) {
        PopupMenu actionMenu = new PopupMenu(context, actionView, Gravity.END);
        actionMenu.inflate(R.menu.menu_report);
        actionMenu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.report) {
                report(itemId);
            }
            return true;
        });
        actionMenu.show();
    }

    private void report(String itemId) {
        cardApi().report(itemId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (response.code() >= 200 && response.code() < 300) {
                        ToastUtils.longs(context, R.string.feedback_text);
                    }
                }, throwable -> ToastUtils.shorts(context, throwable.getMessage()));
    }

    //起飞
    public void fly(Card card, String tag) {
        if (tag.contains(PICTURE)) {
            flyToImageActivity(card);
        } else if (tag.contains(WEB)) {
            flyToBrowserActivity(card);
        }
    }

    private void flyToImageActivity(Card card) {
        final Intent pictureIntent = new Intent(context, ImageActivity.class);
        pictureIntent.setData(Uri.parse(card.cover));
        pictureIntent.putExtra("uuid", card.uuid);
        context.startActivity(pictureIntent);
    }

    private void flyToBrowserActivity(Card card) {
        final Intent browserIntent = new Intent(context, BrowserActivity.class);
        browserIntent.setData(Uri.parse(card.source));
        if (card.refined)
            browserIntent.putExtra(BrowserActivity.REFINED_URL, card.link);
        browserIntent.putExtra(BrowserActivity.TITLE, card.title);
        context.startActivity(browserIntent);
    }

    public void vote(ViewProvider provider, String itemId, final int index) {
        queryAPI().mutation(QueryField.vote(itemId))
                .filter(newData -> newData != null)
                .map(newData -> newData.voteCard.card)
                .compose(RxMainThread.mainThread())
                .subscribe(card -> updateCardItem(provider, card, index),
                        throwable -> {
                            if (throwable instanceof HttpException) {
                                HttpException exception = (HttpException) throwable;
                                if (exception.code() == 400) {// 不可以进行重复续命,不对用户做提示}
                                } else {
                                    ToastUtils.shorts(context, throwable.getMessage());
                                }
                                provider.updateCard(null, index);
                            }
                        });
    }

    private void updateCardItem(ViewProvider viewProvider, Card card, int index) {
        viewProvider.updateCard(card, index);
    }

    private CardApi cardApi() {
        return ExcitedRetrofitFactory.getRetrofit(context).createApi(CardApi.class);
    }

    private QueryAPI queryAPI() {
        return ExcitedRetrofitFactory.getRetrofit(context).createApi(QueryAPI.class);
    }

}
