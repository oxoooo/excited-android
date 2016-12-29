package ooo.oxo.excited.preview;

import android.content.Context;
import android.content.Intent;

import com.trello.rxlifecycle.components.support.RxFragment;

import ooo.oxo.excited.MainActivity;
import ooo.oxo.excited.fragment.FavoritesFragment;
import ooo.oxo.excited.provider.item.SendingCard;


public class PreviewFragment extends RxFragment {

    static final int REQUEST_CODE = 10006;

    Context context;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public void createCard(int type, String url, String channelId, String title, String desc) {
        SendingCard sendingCard = new SendingCard();
        sendingCard.type = type;
        sendingCard.channelId = channelId;
        sendingCard.title = title;
        sendingCard.url = url;
        sendingCard.description = desc;

        Intent intent = new Intent(getContext(), MainActivity.class);
        intent.putExtra("fragment", FavoritesFragment.TAG);
        intent.putExtra("sending_card", sendingCard);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
