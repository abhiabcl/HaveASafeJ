package com.tech.flavor.haveasafej.util;

import android.text.TextUtils;

/**
 * Created by etbdefi on 1/31/2016.
 */
public class HsjValidator {

    public final static boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;

        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static final boolean isValidPhoneNumber(CharSequence target) {
        if (target == null || TextUtils.isEmpty(target)) {
            return false;
        } else {
            return android.util.Patterns.PHONE.matcher(target).matches();
        }
    }
}
