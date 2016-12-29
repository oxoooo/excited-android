package ooo.oxo.excited.preview;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import ooo.oxo.excited.utils.ToastUtils;
import ooo.oxo.excited.widget.RatioImageView;


public class PreviewWebFragment extends PreviewFragment implements View.OnClickListener {

    private ImageView iconCategory;
    private TextView category;
    private RatioImageView webCover;
    private TextView webTitle;
    private TextView webDesc;
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
        View view = inflater.inflate(R.layout.layout_preview_web, container, false);
        iconCategory = (ImageView) view.findViewById(R.id.icon_category);
        category = (TextView) view.findViewById(R.id.category);
        webCover = (RatioImageView) view.findViewById(R.id.web_cover);
        webTitle = (TextView) view.findViewById(R.id.web_title);
        webDesc = (TextView) view.findViewById(R.id.web_desc);
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

        webCover.setOriginalSize(16, 9);
        String url = card.cover;
        if (url != null && url.length() > 0) {
            webCover.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(card.cover)
                    .into(webCover);
        } else {
            webCover.setVisibility(View.GONE);
        }

        Glide.with(this)
                .load(card.headIcon)
                .into(iconCategory);

        category.setText(card.headName);
        webTitle.setText(card.title);
        editTitle.setText(card.title);

        String desc = card.description;
        if (TextUtils.isEmpty(desc)) {
            webDesc.setVisibility(View.GONE);
            editDesc.setText(null);
        } else {
            editDesc.setText(card.description);
            webDesc.setText(card.description);
        }

        editDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    webDesc.setText(charSequence);
                else
                    webDesc.setText(card.description);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        editTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0)
                    webTitle.setText(charSequence);
                else
                    webTitle.setText(card.title);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private View.OnClickListener submitListener = view -> {
        String title = editTitle.getText().toString();
        String desc = editDesc.getText().toString();
        if (channelId == null) {
            ToastUtils.shorts(context, R.string.empty_channel_text);
        } else {
            createCard(SendingCard.WEB, shareUrl, channelId, title, desc);
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
