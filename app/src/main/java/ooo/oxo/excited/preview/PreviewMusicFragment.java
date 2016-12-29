package ooo.oxo.excited.preview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import ooo.oxo.excited.R;
import ooo.oxo.excited.channel.SelectChannelActivity;
import ooo.oxo.excited.model.Card;
import ooo.oxo.excited.provider.item.SendingCard;
import ooo.oxo.excited.utils.CircleTransform;
import ooo.oxo.excited.utils.ToastUtils;


public class PreviewMusicFragment extends PreviewFragment implements View.OnClickListener {

    private ImageView iconCategory;
    private TextView category;
    private ImageView cover;
    private TextView musicTitle;
    private TextView singer;
    private EditText editTitle;
    private EditText editDesc;
    private TextView channelText;

    private Card card;
    private String channelId;
    private String shareUrl;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        card = getArguments().getParcelable("card");
        shareUrl = getArguments().getString("url");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_preview_music, container, false);
        iconCategory = (ImageView) view.findViewById(R.id.icon_category);
        category = (TextView) view.findViewById(R.id.category);
        cover = (ImageView) view.findViewById(R.id.cover);
        musicTitle = (TextView) view.findViewById(R.id.music_title);
        singer = (TextView) view.findViewById(R.id.singer);
        editTitle = (EditText) view.findViewById(R.id.edit_title);
        editDesc = (EditText) view.findViewById(R.id.edit_desc);
        channelText = (TextView) view.findViewById(R.id.channel);
        FrameLayout selectChannel = (FrameLayout) view.findViewById(R.id.select_channel);
        selectChannel.setOnClickListener(this);
        Button submit = (Button) view.findViewById(R.id.submit);
        submit.setOnClickListener(submitListener);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Glide.with(this)
                .load(card.cover)
                .transform(new CircleTransform(context))
                .into(cover);

        Glide.with(this)
                .load(card.headIcon)
                .into(iconCategory);

        category.setText(card.headName);
        musicTitle.setText(card.title);
        singer.setText(card.authorName);

        String desc = card.description;
        if (TextUtils.isEmpty(desc)) {
            editDesc.setText(null);
        } else {
            editDesc.setText(card.description);
        }
    }

    private View.OnClickListener submitListener = view -> {
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        if (channelId == null) {
            ToastUtils.shorts(context, R.string.empty_channel_text);
        } else {
            createCard(SendingCard.MUSIC, shareUrl, channelId, title, desc);
        }
    };

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, SelectChannelActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && data != null) {
            channelId = data.getStringExtra("id");
            String channel = data.getStringExtra("name");
            channelText.setText(channel);
        }
    }
}
