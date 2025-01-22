package com.example.promptsharepro22.utils;

import android.text.TextUtils;

public class UserInfoUtil {

    public static boolean isValidUsername(String username) {
        return !TextUtils.isEmpty(username) && username.length() >= 3;
    }

    public static boolean isValidUscId(String uscId) {
        return !TextUtils.isEmpty(uscId) && uscId.length() == 10 && uscId.matches("\\d+");
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && email.endsWith("@usc.edu");
    }

    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 8;
    }
}
