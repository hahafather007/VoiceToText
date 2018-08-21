package com.hahafather007.voicetotext.utils;

import android.content.Context;
import android.util.TypedValue;

public class DimensionUtil {
    private static int applyDimensionPixelSize(Context context, float value, int unit) {
        return (int) TypedValue
                .applyDimension(unit, value, context.getResources().getDisplayMetrics());
    }

    public static int dp2px(Context context, float value) {
        return applyDimensionPixelSize(context, value, TypedValue.COMPLEX_UNIT_DIP);
    }

    public static int sp2px(Context context, float value) {
        return applyDimensionPixelSize(context, value, TypedValue.COMPLEX_UNIT_SP);
    }
}
