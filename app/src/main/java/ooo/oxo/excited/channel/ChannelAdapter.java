package ooo.oxo.excited.channel;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RippleDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import ooo.oxo.excited.LoginManager;
import ooo.oxo.excited.R;
import ooo.oxo.excited.model.Channel;
import ooo.oxo.excited.model.Data;
import ooo.oxo.excited.utils.CircleTransform;


public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ChannelHolder> {

    private Context context;
    private List<Channel> channels;
    private OnChannelClickListener onChannelClickListener;

    private int height;

    public void setOnChannelClickListener(OnChannelClickListener onChannelClickListener) {
        this.onChannelClickListener = onChannelClickListener;
    }

    public ChannelAdapter(Context context, List<Channel> channels) {
        this.context = context;
        this.channels = channels;
    }

    @Override
    public ChannelHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_channel, parent, false);
        return new ChannelHolder(view);
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void onBindViewHolder(ChannelHolder holder, int position) {
        Channel channel = channels.get(position);
        holder.id = channel.id;
        holder.name = channel.name;
        holder.text.setText(channel.name);
        Glide.with(context)
                .load(channel.icon)
                .transform(new CircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.icon);

        if (channel.followed) {
            holder.icon.setBackgroundResource(R.drawable.channel_follow_bg);
        } else {
            holder.icon.setBackgroundResource(R.drawable.channel_unfollow_bg);
        }
    }

    @Override
    public int getItemCount() {
        return channels.size();
    }

    class ChannelHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView icon;
        TextView text;
        String id;
        String name;

        public ChannelHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.channel_text);
            icon = (ImageView) itemView.findViewById(R.id.channel_icon);
            itemView.setOnClickListener(this);

            int itemHeight = height / 3;
            if (itemHeight > 0) {
                ViewGroup.LayoutParams viewParams = itemView.getLayoutParams();
                viewParams.height = itemHeight;
                itemView.setLayoutParams(viewParams);

                int iconSize = (int) (0.45 * itemHeight);
                int margin = (int) (0.1 * itemHeight);
                int textSize = (int) (0.09 * itemHeight);

                LinearLayout.LayoutParams iconParams = (LinearLayout.LayoutParams) icon.getLayoutParams();
                iconParams.height = iconSize;
                iconParams.width = iconSize;
                iconParams.topMargin = margin * 2;
                icon.setLayoutParams(iconParams);

                ((LinearLayout.LayoutParams) text.getLayoutParams()).topMargin = margin;
                text.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            }
        }

        @Override
        public void onClick(View view) {
            if (onChannelClickListener != null) {
                onChannelClickListener.onChannelClick(channels.get(getAdapterPosition()));
            }
        }
    }

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }
}
