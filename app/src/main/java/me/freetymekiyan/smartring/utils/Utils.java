package me.freetymekiyan.smartring.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import me.freetymekiyan.smartring.R;

/**
 * Created by Kiyan on 4/30/15.
 */
public class Utils {

    public static float getUpperLimitValue(Context ctx) {
        final SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(ctx);
        String age = sp.getString(ctx.getString(R.string.key_age), "");
        boolean gender = sp.getBoolean(ctx.getString(R.string.key_gender), true);
        if (age.isEmpty()) {
            return 0f;
        }
        return gender ? getMaleHr(age) : getFemaleHr(age);
    }

    /**
     * Gulati method, for women, 2010
     */
    private static float getFemaleHr(String age) {
        return 206 - 0.88f * Integer.valueOf(age);
    }

    /**
     * Least objectionable formula
     */
    private static float getMaleHr(String age) {
        return 205.8f - 0.685f * Integer.valueOf(age);
    }

}
