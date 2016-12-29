package ooo.oxo.excited.preview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ooo.oxo.excited.R;

/**
 * Showing this view while preview wrong.
 * Created by seasonyuu on 2016/11/25.
 */

public class PreviewWrongFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_preview_wrong, container, false);
    }
}
