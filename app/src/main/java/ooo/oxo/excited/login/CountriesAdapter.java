package ooo.oxo.excited.login;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ooo.oxo.excited.R;
import ooo.oxo.excited.utils.PhoneArea;

/**
 * Created by seasonyuu on 2016/12/7.
 */

public class CountriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> areas;
    private List<String> index;
    private List<String> numbers;
    private PhoneArea phoneArea;
    private int count = 0;
    private SparseIntArray viewTypeArray;
    private SparseIntArray realIndexArray;

    private static final int TYPE_INDEX = 0x1;
    private static final int TYPE_COUNTRY = 0x2;

    public interface OnItemSelectedListener {
        void onItemSelected(View itemView, View parent, int position);
    }

    private OnItemSelectedListener onItemSelectedListener;

    public CountriesAdapter(Context context) {
        phoneArea = PhoneArea.getInstance(context);

        areas = phoneArea.getAreas();
        index = phoneArea.getAreasIndex();
        numbers = phoneArea.getAreasNumber();

        count = areas.size();

        Set<String> diffIndex = new HashSet<>();
        diffIndex.addAll(index);
        count += diffIndex.size();

        viewTypeArray = new SparseIntArray();
        realIndexArray = new SparseIntArray();
        int calculateCount = 0;
        for (int i = 0; i < areas.size(); i++) {
            viewTypeArray.put(calculateCount, TYPE_INDEX);
            realIndexArray.put(calculateCount, i);
            calculateCount++;
            String indexChar = index.get(i);
            while (i < areas.size()) {
                if (index.get(i).equals(indexChar)) {
                    viewTypeArray.put(calculateCount, TYPE_COUNTRY);
                    realIndexArray.put(calculateCount, i);
                    calculateCount++;
                } else {
                    break;
                }
                i++;
            }
            i--;
        }
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_INDEX) {
            return new IndexHolder(LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false));
        }
        return new CountryHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_country, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_INDEX) {
            ((TextView) holder.itemView.findViewById(android.R.id.text1))
                    .setText(index.get(getRealIndex(position)));
        } else {
            ((TextView) holder.itemView.findViewById(R.id.text1))
                    .setText(areas.get(getRealIndex(position)));
            ((TextView) holder.itemView.findViewById(R.id.text2))
                    .setText("(" + numbers.get(getRealIndex(position)) + ")");
        }
    }

    public String getItem(int position) {
        int viewType = getItemViewType(position);
        int index = getRealIndex(position);
        return viewType == TYPE_INDEX ? this.index.get(index) : areas.get(index);
    }

    public int getRealIndex(int position) {
        return realIndexArray.get(position);
    }

    @Override
    public int getItemCount() {
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        return viewTypeArray.get(position);
    }

    public List<String> getAreas() {
        return areas;
    }

    public List<String> getIndex() {
        return index;
    }

    public List<String> getNumbers() {
        return numbers;
    }

    private class IndexHolder extends RecyclerView.ViewHolder {

        public IndexHolder(View itemView) {
            super(itemView);
        }
    }

    private class CountryHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public CountryHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemSelectedListener != null)
                onItemSelectedListener.onItemSelected(view, (View) view.getParent(), getAdapterPosition());
        }
    }

}
