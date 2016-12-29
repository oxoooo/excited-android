package ooo.oxo.excited.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ooo.oxo.excited.R;

/**
 * Created by seasonyuu on 2016/12/6.
 */

public class PhoneArea {
    private static PhoneArea phoneArea;
    private String[] areas;
    private String[] areasIndex;
    private String[] areasNumber;

    private PhoneArea() {
    }

    public static PhoneArea getInstance(Context context) {
        if (phoneArea == null) {
            phoneArea = new PhoneArea();
            phoneArea.init(context);
        }
        return phoneArea;
    }

    private void init(Context context) {
        areas = context.getResources().getStringArray(R.array.areas_zh);
        areasIndex = context.getResources().getStringArray(R.array.area_index_zh);
        areasNumber = context.getResources().getStringArray(R.array.areas_number_zh);
    }

    public List<String> getAreas() {
        return Collections.unmodifiableList(Arrays.asList(areas));
    }

    public List<String> getAreasIndex() {
        return Collections.unmodifiableList(Arrays.asList(areasIndex));
    }

    public List<String> getAreasNumber() {
        return Collections.unmodifiableList(Arrays.asList(areasNumber));
    }

}
