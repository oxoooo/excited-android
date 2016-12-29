package ooo.oxo.excited.provider;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ooo.oxo.excited.R;
import ooo.oxo.excited.widget.SpringView;

/**
 * Created by zsj on 2016/10/19.
 */

public class CardHolder extends RecyclerView.ViewHolder {

    ImageView iconCategory;
    TextView category;
    TextView time;
    ImageButton plus;
    SpringView springView;
    TextView remain;
    TextView sum;
    ImageButton more;

    RelativeLayout plusContainer;

    CardHolder(View itemView) {
        super(itemView);
        time = (TextView) itemView.findViewById(R.id.time);
        plus = (ImageButton) itemView.findViewById(R.id.plus);
        springView = (SpringView) itemView.findViewById(R.id.spring_view);
        sum = (TextView) itemView.findViewById(R.id.sum);
        remain = (TextView) itemView.findViewById(R.id.remain);
        iconCategory = (ImageView) itemView.findViewById(R.id.icon_category);
        category = (TextView) itemView.findViewById(R.id.category);
        more = (ImageButton) itemView.findViewById(R.id.more);
        plusContainer = (RelativeLayout) itemView.findViewById(R.id.plus_container);
    }

}
